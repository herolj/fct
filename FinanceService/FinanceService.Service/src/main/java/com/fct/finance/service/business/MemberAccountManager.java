package com.fct.finance.service.business;

import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.repository.MemberAccountRepository;
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
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public class MemberAccountManager {

    @Autowired
    private MemberAccountRepository memberAccountRepository;

    public static MemberAccountManager instance = new MemberAccountManager();

    public void save(MemberAccount account)
    {
        memberAccountRepository.saveAndFlush(account);
    }

    public MemberAccount findById(int memberId)
    {
        return memberAccountRepository.findOne(memberId);
    }

    public Page<MemberAccount> findAll(String cellPhone,Integer orderBy,Integer pageIndex,Integer pageSize)
    {
        String sortName = " CreateTime";
        switch (orderBy)
        {
            case 1:
                sortName = "availableAmount";
                break;
            case 2:
                sortName = "points";
                break;
            case 3:
                sortName = "accumulateIncome";
                break;
        }
        Sort sort = new Sort(Sort.Direction.DESC, sortName);
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<MemberAccount> spec = new Specification<MemberAccount>() {
            @Override
            public Predicate toPredicate(Root<MemberAccount> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return memberAccountRepository.findAll(spec,pageable);
    }

}
