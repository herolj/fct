package com.fct.finance.service.business;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.data.repository.PayPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
public class PayPlatformManager {
    @Autowired
    private PayPlatformRepository payPlatformRepository;

    public static PayPlatformManager instance = new PayPlatformManager();

    public List<PayPlatform> findAll()
    {
        Sort sort = new Sort(Sort.Direction.ASC, "SortIndex");

        return payPlatformRepository.findAll(sort);
    }
}
