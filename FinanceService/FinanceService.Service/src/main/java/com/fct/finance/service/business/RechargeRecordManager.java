package com.fct.finance.service.business;

import com.fct.common.converter.DateFormatter;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.data.repository.RechargeRecordRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
public class RechargeRecordManager {

    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    public static RechargeRecordManager instance = new RechargeRecordManager();

    public Integer create(RechargeRecord record)
    {
        if(record.getMemberId()<=0)
        {
            throw new IllegalArgumentException("用户Id为空。");
        }
        if(StringUtils.isEmpty(record.getCellPhone()))
        {
            throw new IllegalArgumentException("手机号为空。");
        }
        record.setCreateTime(new Date());

        rechargeRecordRepository.save(record);

        return record.getId();
    }

    public RechargeRecord findById(Integer id)
    {
        return rechargeRecordRepository.findOne(id);
    }

    public void paySuccess(Integer id, String payOrderId, String payPlatform, String payTime,String payStatus)
    {
        RechargeRecord record = rechargeRecordRepository.findOne(id);
        record.setPayOrderId(payOrderId);
        record.setPayPlatform(payPlatform);
        record.setPayTime(DateFormatter.parseDateTime(payTime));
        if(payStatus =="success") {
            record.setStatus(1);    //充值成功
        }
        else
        {
            record.setStatus(2);    //充值失败
        }
        rechargeRecordRepository.save(record);
    }


    public Page<RechargeRecord> findAll(Integer memberId, String cellPhone, Integer status,
                                            String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<RechargeRecord> spec = new Specification<RechargeRecord>() {
            @Override
            public Predicate toPredicate(Root<RechargeRecord> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }

                if (memberId > 0) {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }

                if (!StringUtils.isEmpty(beginTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("beginTime"), beginTime));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
                }
                if (status > -1) {
                    predicates.add(cb.equal(root.get("status"), status));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return rechargeRecordRepository.findAll(spec,pageable);
    }
}
