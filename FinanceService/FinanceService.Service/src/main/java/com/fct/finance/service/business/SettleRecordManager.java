package com.fct.finance.service.business;

import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.SettleRecord;
import com.fct.finance.data.repository.SettleRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public class SettleRecordManager {

    @Autowired
    private SettleRecordRepository settleRecordRepository;

    public static SettleRecordManager instance = new SettleRecordManager();

    public Integer create(SettleRecord record)
    {
        if(record.getMemberId() <=0)
        {
            throw new IllegalArgumentException("结算店长用户id为空。");
        }
        if(record.getShopId()<=0)
        {
            throw new IllegalArgumentException("店铺id为空。");
        }
        if(StringUtils.isEmpty(record.getTradeId()))
        {
            throw new IllegalArgumentException("交易订单号为空。");
        }
        if(StringUtils.isEmpty(record.getTradeType()))
        {
            throw new IllegalArgumentException("交易订单类型为空。");
        }
        if(record.getSaleAmount().doubleValue()<=0 || record.getCommission().doubleValue()<=0)
        {
            throw new IllegalArgumentException("金额非法。");
        }

        if (record.getCommission().doubleValue() >= record.getSaleAmount().doubleValue())
        {
            throw new IllegalArgumentException("结算金额不合法");
        }

        if(settleRecordRepository.countByTradeIdAndTradeType(record.getTradeId(),record.getTradeType())>0)
        {
            throw new IllegalArgumentException("已结算");
        }
        if(record.getInviterId()>0)
        {
            //额外奖励推荐者
            record.setInviterCommission(record.getCommission().multiply(new BigDecimal("0.1")));
        }

        record.setStatus(0);
        settleRecordRepository.save(record);
        return record.getId();
    }

    public void updateStatus(Integer omsOperaterId,Integer status,String ids,String desc)
    {
        settleRecordRepository.updateStatus(omsOperaterId,ids,status,desc,new Date());
    }

    /// <summary>
    /// 将财务确认结算数据同步更新到用户虚拟账户
    /// </summary>
    public void task()
    {

        List<SettleRecord> ls = settleRecordRepository.findByStatus(1);
        for (SettleRecord sr:ls
             ) {
            sr.setStatus(2);    //结算成功
            sr.setUpdateTime(new Date());
            settleRecordRepository.save(sr);

            addAccountAmount(sr.getMemberId(),sr.getCellPhone(),sr.getCommission(),sr.getId());

            if(sr.getInviterId()>0 && sr.getInviterCommission().doubleValue()>0)
            {
                addAccountAmount(sr.getInviterId(),"",sr.getInviterCommission(),sr.getId());
            }

        }

    }

    /// <summary>
    /// 增加虚拟余额
    /// </summary>
    void addAccountAmount(Integer memberId,String cellPhone,BigDecimal commission,Integer settleId)
    {
        MemberAccount account = MemberAccountManager.instance.findById(memberId);

        if (account == null)
        {
            account = new MemberAccount();
            account.setMemberId(memberId);
            account.setCellPhone(cellPhone);
            account.setCreateTime(new Date());
        }
        account.setAvailableAmount(account.getAvailableAmount().add(commission));
        account.setAccumulateIncome(account.getAccumulateIncome().add(commission));
        account.setWithdrawAmount(account.getWithdrawAmount().add(commission));

        MemberAccountManager.instance.save(account);

        MemberAccountHistory history = new MemberAccountHistory();
        history.setTradeId(settleId.toString());
        history.setTradeType(Constants.enumTradeType.settle.toString());
        history.setMemberId(memberId);
        history.setAmount(commission);
        history.setBalanceAmount(account.getAvailableAmount());
        history.setPoints(0);
        history.setBalancePoints(account.getPoints());
        history.setRemark("佣金结算");
        history.setBehaviorType(1); //收入
        MemberAccountHistoryManager.instance.Create(history);

    }

    public SettleRecord findById(Integer id)
    {
        return settleRecordRepository.findOne(id);
    }

    public Page<SettleRecord> findAll(Integer memberId, String cellPhone,String tradeType, String tradeId, Integer status,
                                      String beginTime, String endTime, Integer pageIndex,Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<SettleRecord> spec = new Specification<SettleRecord>() {
            @Override
            public Predicate toPredicate(Root<SettleRecord> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }

                if (!StringUtils.isEmpty(tradeType)) {
                    predicates.add(cb.equal(root.get("tradeType"), tradeType));
                }

                if (!StringUtils.isEmpty(tradeId)) {
                    predicates.add(cb.equal(root.get("tradeId"), tradeId));
                }

                if(memberId>0)
                {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("status"), status));
                }
                if (!StringUtils.isEmpty(beginTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("beginTime"), beginTime));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return settleRecordRepository.findAll(spec,pageable);
    }
}
