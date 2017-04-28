package com.fct.finance.service.business;

import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.repository.MemberAccountHistoryRepository;
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
 * Created by jon on 2017/4/20.
 */
public class MemberAccountHistoryManager {
    @Autowired
    private MemberAccountHistoryRepository memberAccountHistoryRepository;

    public static MemberAccountHistoryManager instance = new MemberAccountHistoryManager();

    public void Create(MemberAccountHistory history)
    {
        history.setCreateTime(new Date());

        memberAccountHistoryRepository.save(history);
    }

    public Page<MemberAccountHistory> findAll(Integer memberId,String cellPhone, String tradeId, String tradeType,
                                              Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<MemberAccountHistory> spec = new Specification<MemberAccountHistory>() {
            @Override
            public Predicate toPredicate(Root<MemberAccountHistory> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                if (memberId>0) {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }
                if (!StringUtils.isEmpty(tradeId)) {
                    predicates.add(cb.equal(root.get("tradeId"), tradeId));
                }
                if (!StringUtils.isEmpty(tradeType)) {
                    predicates.add(cb.equal(root.get("tradeType"), tradeType));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return memberAccountHistoryRepository.findAll(spec,pageable);
    }
}
