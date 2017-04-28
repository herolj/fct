package com.fct.finance.data.repository;


import com.fct.finance.data.entity.MemberAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/4/7.
 */
public interface MemberAccountRepository extends JpaRepository<MemberAccount, Integer>{

    /**
     * 查询语句findBy后面跟着的名字是entity的属性名称 比如findById Id代表entity UserEntity 里面的id, 也对应表里面的id列
     * sql : select * from memberaccount where memberId = ?
     * @param memberId
     * @return
     */
    MemberAccount findByMemberId(Integer memberId);

    Page<MemberAccount> findAll(Specification<MemberAccount> spec, Pageable pageable);  //分页按条件查询

}

