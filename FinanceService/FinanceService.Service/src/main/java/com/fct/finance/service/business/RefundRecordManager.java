package com.fct.finance.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.common.json.JsonConverter;
import com.fct.common.logger.LogService;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.data.entity.RefundRecord;
import com.fct.finance.data.repository.RefundRecordRepository;
import com.fct.message.model.MQPayRefund;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jon on 2017/4/20.
 */
public class RefundRecordManager {

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    public static RefundRecordManager instance = new RefundRecordManager();

    @Transactional
    public RefundRecord create(RefundRecord refund)
    {
        if (refund.getMemberId() <= 0)
        {
            throw new IllegalArgumentException("退款用户Id不合法。");
        }
        if (StringUtils.isEmpty(refund.getCellPhone()))
        {
            throw new IllegalArgumentException("退款用户联系方式为空");
        }
        if (refund.getMethod() < 0)
        {
            throw new IllegalArgumentException("退款方式不合法");
        }
        if (StringUtils.isEmpty(refund.getTradeId()))
        {
            throw new IllegalArgumentException("退款业务Id为空");
        }
        if (StringUtils.isEmpty(refund.getTradeType()))
        {
            throw new IllegalArgumentException("退款业务类型为空");
        }
        if (refund.getRefundAmount().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("退款金额不合法");
        }

        if (refundRecordRepository.countByTradeIdAndTradeType(refund.getTradeId(),refund.getTradeType()) > 0)
        {
            throw new BaseException("已提交过退款处理。");
        }


        ///线下处理退款，无法校验其有效性，所以不做校验。
        ///
        //校验退款金额是否与支付时产生的一致。
        PayOrder pay = PayOrderManager.instance.findOne(refund.getPayOrderId());

        if (pay != null)
        {
            if(pay.getStatus() ==(int)Constants.enumPayStatus.fullrefund.getValue())
            {
                throw new BaseException("非法退款");
            }
            //如果支付订单为余额异常，则退款金额不得大于网上银行支付金额
            if ((pay.getTotalAmount().subtract(pay.getRefundAmount())).doubleValue() < refund.getRefundAmount().doubleValue() ||
                    refund.getCashAmount().doubleValue()>pay.getPayAmount().doubleValue() ||
                    refund.getPoints() > pay.getPoints() )
            {
                throw new BaseException("退款金额非法。");
            }
        }

        switch (refund.getMethod())
        {
            case 1: //原路返回
                if(refund.getAccountAmount().doubleValue() > pay.getAccountAmount().doubleValue())
                {
                    throw new BaseException("退款金额非法。");
                }

                refund.setPayPlatform(pay.getPayPlatform());
                refund.setPayPlatformOrderId(getPayPlatformOrderId(pay.getPayPlatform(),pay.getNotifyData()));
                refund.setPayAmount(pay.getPayAmount());

                break;
            case 0:    //全部退款至虚拟余额
                refund.setCashAmount(new BigDecimal(0));
                break;
        }
        refund.setCreateTime(new Date());
        refund.setUpdateTime(refund.getCreateTime());
        refund.setRemark("支付异常，系统默认退款。");
        refundRecordRepository.save(refund);

        //累计退款金额
        pay.setRefundAmount(pay.getRefundAmount().add(refund.getRefundAmount()));

        //如果支付订单金额都退掉，则状态更新为退款成功，否则部分退款
        Integer payStatus = pay.getTotalAmount() == pay.getRefundAmount() ? Constants.enumPayStatus.fullrefund.getValue():
                Constants.enumPayStatus.partrefund.getValue();
        pay.setStatus(payStatus);

        PayOrderManager.instance.save(pay);

        return refund;
    }

    /// <summary>
    /// 业务交易异常发起退款
    /// </summary>
    public void tradeException(MQPayRefund result, PayOrder pay)
    {
        //一个支付、一个业务行为对应一条退款记录。
        if (refundRecordRepository.countByTradeIdAndTradeType(result.trade_id,result.trade_type) > 0)
        {
            LogService.warning("pay_orderid:" + pay.getOrderId() + ",已提交过退款处理");
            return;
        }

        if (result.refund_amount.doubleValue()<pay.getRefundAmount().doubleValue())
        {
            throw new BaseException("退款金额非法。");
        }

        RefundRecord refund = new RefundRecord();
        refund.setMemberId(pay.getMemberId());
        refund.setCellPhone(pay.getCellPhone());
        refund.setPayOrderId(pay.getOrderId());
        refund.setPayPlatform(pay.getPayPlatform());
        refund.setPayPlatformOrderId(getPayPlatformOrderId(pay.getPayPlatform(),pay.getNotifyData()));
        refund.setMethod(result.method);
        refund.setTradeId(result.trade_id);
        refund.setTradeType(result.trade_type);
        refund.setStatus(Constants.enumRefundStatus.wait_handle.getValue()); //财务默认确认。

        switch (refund.getMethod())
        {
            case 1: //原路退回
                refund.setCashAmount(result.cash_amount);
                refund.setAccountAmount(result.account_amount);
                refund.setPayAmount(result.pay_amount);
                break;
            case 0:    //全部退款至虚拟余额
                refund.setCashAmount(new BigDecimal(0));
                break;
        }
        refund.setRefundAmount(result.refund_amount);
        refund.setPoints(result.points);

        refund.setCreateTime(new Date());
        refund.setUpdateTime(refund.getCreateTime());
        refund.setRemark("支付异常，系统默认退款。");
        refundRecordRepository.save(refund);

        //累计退款金额
        pay.setRefundAmount(pay.getRefundAmount().add(refund.getRefundAmount()));

        //如果支付订单金额都退掉，则状态更新为退款成功，否则部分退款
        Integer payStatus = pay.getTotalAmount() == pay.getRefundAmount() ? Constants.enumPayStatus.fullrefund.getValue():
                Constants.enumPayStatus.partrefund.getValue();
        pay.setStatus(payStatus);

    }

    private String getPayPlatformOrderId(String platform,String notifyData)
    {
        Map<String, String> dic = JsonConverter.toObject(notifyData,Map.class);
        switch (platform.toLowerCase())
        {
            case "alipay_fctwap":
            case "alipay_fctapp":
                return dic.containsKey("trade_no") ? dic.get("trade_no") : getStrForXmlDoc(dic.get("notify_data"), "/notify/trade_no");
            case "wxpay_fctwap":
            case "wxpay_fctapp":
                return dic.get("transaction_id");
            case "unionpay_fctwap":
            case "unionpay_fctapp":
                return dic.get("queryId");
            default:
                return "";
        }
    }

    private String getStrForXmlDoc(String xmlContent,String xmlNode)
    {
        try {

             Document doc = DocumentHelper.parseText(xmlContent);

             return doc.selectSingleNode(xmlNode).getText();
        }
        catch (DocumentException exp)
        {
            return "";
        }

    }

    @Transactional
    public void confirm(Integer omsOperaterId,String ids)
    {
        if(StringUtils.isEmpty(ids))
        {
            throw new IllegalArgumentException("退款Id为空。");
        }
        List<RefundRecord> ls = refundRecordRepository.findByIds(ids);
        for (RefundRecord refund:ls
             ) {

            refund.setStatus(Constants.enumRefundStatus.success.getValue()); //默认退款状态为成功
            refund.setOmsOperatorId(omsOperaterId);

            //退款至虚拟账户
            if (refund.getAccountAmount().doubleValue() > 0 || refund.getPoints()>0)
            {
                //更新用户虚拟余额。
                MemberAccount account = MemberAccountManager.instance.findById(refund.getMemberId());

                account.setAvailableAmount(account.getAvailableAmount().add(refund.getAccountAmount()));

                account.setRechargeAmount(account.getRechargeAmount().add(refund.getAccountAmount()));

                account.setPoints(account.getPoints()+refund.getPoints());

                MemberAccountManager.instance.save(account);

                MemberAccountHistory history = new MemberAccountHistory();
                history.setTradeId(refund.getId().toString());
                history.setTradeType(Constants.enumTradeType.refund.toString());
                history.setMemberId(refund.getMemberId());
                history.setAmount(refund.getAccountAmount());
                history.setBalanceAmount(account.getAvailableAmount());
                history.setPoints(refund.getPoints());
                history.setBalancePoints(account.getPoints());
                history.setRemark(refund.getRemark());
                history.setBehaviorType(1); //收入
                MemberAccountHistoryManager.instance.Create(history);

            }

            //如果有退款至现金，表示需要原路返回至支付平台。
            if (refund.getCashAmount().doubleValue() > 0 && refund.getPayPlatform() != "offline")
            {
                //如果原路返回支付平台，则更新退款状态为部份退款成功(余额退款成功)。
                refund.setStatus(Constants.enumRefundStatus.confirmed.getValue());
                //写入原路返回消息体，支付服务进行处理。
                sendMessageQ(refund);
            }

            refundRecordRepository.saveAndFlush(refund);
        }

    }

    private void sendMessageQ(RefundRecord refund)
    {
        //发送message
        MQPayRefund message = new MQPayRefund();

        //如果为银联支付则支付单号必须是银联提供的orgId
        message.pay_orderid = refund.getPayPlatform().toLowerCase() == "unionpay_fcth5" ? refund.getPayPlatformOrderId() : refund.getPayOrderId();
        message.pay_platform = refund.getPayPlatform();
        message.refund_id = refund.getId();
        message.pay_amount = refund.getPayAmount();
        message.cash_amount = refund.getCashAmount();
        message.desc = "退款";

        APIClient.messageService.send("mq_payrefund","MQPayRefund","com.fct.finance",
                JsonConverter.toJson(message),"原路返回退款至第三方支付平台");

    }

    /// <summary>
    /// 更新退款状态为成功。
    /// </summary>
    /// <param name="id"></param>
    public void success(Integer refundId,String notifyData)
    {
        if (refundId <= 0)
        {
            throw new IllegalArgumentException("退款处理Id为空。");
        }
        refundRecordRepository.updatSuccess(refundId,notifyData,Constants.enumRefundStatus.success.getValue());
    }

    public Page<RefundRecord> findAll(Integer memberId, String cellPhone, String tradeId, String tradeType, String payPlatform,
                        Integer status, String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<RefundRecord> spec = new Specification<RefundRecord>() {
            @Override
            public Predicate toPredicate(Root<RefundRecord> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                if(memberId>0)
                {
                    predicates.add(cb.equal(root.get("memberId"), memberId));
                }
                if (!StringUtils.isEmpty(tradeId)) {
                    predicates.add(cb.equal(root.get("tradeId"), tradeId));
                }
                if (!StringUtils.isEmpty(tradeType)) {
                    predicates.add(cb.equal(root.get("tradeType"), tradeType));
                }
                if (!StringUtils.isEmpty(payPlatform)) {
                    predicates.add(cb.equal(root.get("payPlatform"), payPlatform));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("status"), status));
                }
                if (!StringUtils.isEmpty(beginTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("beginTime"), beginTime));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return refundRecordRepository.findAll(spec,pageable);
    }
}
