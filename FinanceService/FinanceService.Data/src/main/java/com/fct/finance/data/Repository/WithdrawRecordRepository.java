package com.fct.finance.data.repository;

import com.fct.finance.data.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by jon on 2017/4/20.
 */
public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Integer> {

    Page<WithdrawRecord> findAll(Specification<WithdrawRecord> spec, Pageable pageable);  //分页按条件查询

    int countByMemberIdAndStatus(Integer memberId,Integer status);

    @Query(nativeQuery = true, value = "UPDATE WithdrawRecord SET Status=?3,remark=?4,omsOperaterId=?1,updateTime=?5 WHERE Id=?2 AND Status!=1")
    void updateStatus(Integer omsOperaterId, Integer id, Integer status, String desc, Date upTime);

}
