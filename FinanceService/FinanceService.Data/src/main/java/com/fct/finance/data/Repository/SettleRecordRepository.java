package com.fct.finance.data.repository;

import com.fct.finance.data.entity.SettleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public interface SettleRecordRepository extends JpaRepository<SettleRecord, Integer> {

    Page<SettleRecord> findAll(Specification<SettleRecord> spec, Pageable pageable);  //分页按条件查询

    Integer countByTradeIdAndTradeType(String tradeId,String tradeType);

    @Query(nativeQuery = true, value = "UPDATE SettleRecord SET Status=?3,remark=?4,omsOperaterId=?1,updateTime=?5 WHERE Id IN(?2) AND Status!=1s")
    void updateStatus(Integer omsOperaterId, String ids, Integer status, String desc, Date upTime);

    List<SettleRecord> findByStatus(Integer status);
}
