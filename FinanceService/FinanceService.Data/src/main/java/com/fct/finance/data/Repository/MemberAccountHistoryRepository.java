package com.fct.finance.data.repository;

import com.fct.finance.data.entity.MemberAccountHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/4/10.
 */
public interface MemberAccountHistoryRepository extends JpaRepository<MemberAccountHistory, Long> {

    Page<MemberAccountHistory> findAll(Specification<MemberAccountHistory> spec, Pageable pageable);  //分页按条件查询
}
