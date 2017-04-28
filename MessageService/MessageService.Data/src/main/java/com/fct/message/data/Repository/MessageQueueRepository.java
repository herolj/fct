package com.fct.message.data.repository;


//import com.fct.finance.data.entity.MemberAccount;
import com.fct.message.data.entity.MessageQueue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/7.
 */
public interface MessageQueueRepository extends JpaRepository<MessageQueue, Integer>{

    @Query(nativeQuery = true, value = "select * from MessageQueue where typeId=?1 and status=0 and requestcount<=3 limit 500")
    List<MessageQueue> findByTypeId(String typeId);

    @Query(nativeQuery = true, value = "UPDATE MessageQueue SET Status=1,RequestCount=1,ProcessTime='?2' WHERE Id=?1")
    void complete(Integer id, Date date);

    @Query(nativeQuery = true, value = "UPDATE MessageQueue SET Status=0,RequestCount=0 WHERE Id=?1")
    void resume(Integer id);

    Page<MessageQueue> findAll(Specification<MessageQueue> spec, Pageable pageable);  //分页按条件查询

}

