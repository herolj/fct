package com.fct.finance.service.business;

import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.data.repository.WithdrawRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public class WithdrawRecordManager {
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;

    public static WithdrawRecordManager instance = new WithdrawRecordManager();

    @Transactional
    public void apply(WithdrawRecord record)
    {

        if (record.getMemberId() <= 0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        if (StringUtils.isEmpty(record.getName()))
        {
            throw new IllegalArgumentException("提现账户姓名为空");
        }
        if (StringUtils.isEmpty(record.getBankName()))
        {
            throw new IllegalArgumentException("提现平台为空");
        }
        if (StringUtils.isEmpty(record.getBankAccount()))
        {
            throw new IllegalArgumentException("提现账号为空");
        }

        if (record.getAmount().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("提现金额为空。");
        }

        if(withdrawRecordRepository.countByMemberIdAndStatus(record.getMemberId(),0)>0)
        {
            throw new IllegalArgumentException("还有尚未处理的提现,不可连续申请。");
        }

        MemberAccount account = MemberAccountManager.instance.findById(record.getMemberId());
        if(account == null || account.getWithdrawAmount().doubleValue() <record.getAmount().doubleValue())
        {
            throw  new IllegalArgumentException("非法操作。");
        }

        account.setAvailableAmount(account.getAvailableAmount().subtract(record.getAmount()));
        account.setWithdrawAmount(account.getWithdrawAmount().subtract(record.getAmount()));

        MemberAccountManager.instance.save(account);    //

        record.setStatus(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(record.getCreateTime());
        withdrawRecordRepository.save(record);

        MemberAccountHistory history = new MemberAccountHistory();
        history.setTradeId(record.getId().toString());
        history.setTradeType(Constants.enumTradeType.withdrawal.toString());
        history.setMemberId(record.getMemberId());
        history.setAmount(record.getAmount());
        history.setBalanceAmount(account.getAvailableAmount());
        history.setPoints(0);
        history.setBalancePoints(account.getPoints());
        history.setRemark("提现");
        history.setBehaviorType(0); //支出
        MemberAccountHistoryManager.instance.Create(history);

    }


    public void updateStatus(Integer omsOperaterId,Integer id, Integer status,String desc)
    {
        withdrawRecordRepository.updateStatus(omsOperaterId,id,status,desc,new Date());
    }

    public Page<WithdrawRecord> findAll(Integer memberId, String cellPhone, Integer status,
                                        String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<WithdrawRecord> spec = new Specification<WithdrawRecord>() {
            @Override
            public Predicate toPredicate(Root<WithdrawRecord> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
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

        return withdrawRecordRepository.findAll(spec,pageable);
    }
}
