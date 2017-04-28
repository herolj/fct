package com.fct.message.service;

import com.fct.message.data.entity.MessageQueue;
import com.fct.message.data.repository.MessageQueueRepository;
import com.fct.message.interfaces.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import java.util.List;

/**
 * Created by jon on 2017/4/11.
 */
@Service(value = "messageService")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageQueueRepository messageQueueRepository;

    @Override
    public void create(MessageQueue message) {

        message.setStatus(0);
        message.setRequestCount(0);
        message.setCreateTime(new Date());
        messageQueueRepository.save(message);
    }

    @Override
    public void send(String typeId,String targetModule,String sourceAppName,String jsonBody,String remark)
    {
        MessageQueue message = new MessageQueue();
        message.setTypeId(typeId);
        message.setTargetModule(targetModule);
        message.setSourceAppName(sourceAppName);
        message.setBody(jsonBody);
        message.setRemark(remark);
        message.setStatus(0);
        message.setRequestCount(0);
        message.setCreateTime(new Date());

        messageQueueRepository.save(message);
    }

    @Override
    public void complete(Integer id) {
        messageQueueRepository.complete(id,new Date());
    }

    @Override
    public void fail(Integer id, String message) {
        MessageQueue msg = messageQueueRepository.findOne(id);
        if(msg.getRequestCount()>=3)
        {
            msg.setStatus(-1);  //异常不在处理。
        }
        msg.setRequestCount(msg.getRequestCount()+1);
        msg.setFailMessage(message);
        msg.setProcessTime(new Date());
        messageQueueRepository.saveAndFlush(msg);
    }

    @Override
    public void resume(Integer id) {
        messageQueueRepository.resume(id);
    }

    @Override
    public List<MessageQueue> find(String typeId) {
        return messageQueueRepository.findByTypeId(typeId);
    }

    @Override
    public Page<MessageQueue> findAll(String targetModule,Integer status,Integer pageIndex, Integer pageSize) {

        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<MessageQueue> spec = new Specification<MessageQueue>() {
            @Override
            public Predicate toPredicate(Root<MessageQueue> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isEmpty(targetModule)) {
                    predicates.add(cb.equal(root.get("targetModule"), targetModule));
                }
                if(status > -1)
                {
                    predicates.add(cb.equal(root.get("status"),status));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return messageQueueRepository.findAll(spec,pageable);
    }
}
