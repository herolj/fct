package com.fct.finance.data.repository;

import com.fct.finance.data.entity.RechargeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/4/21.
 */
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Integer> {
    Page<RechargeRecord> findAll(Specification<RechargeRecord> spec, Pageable pageable);  //分页按条件查询
}
