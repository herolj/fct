package com.fct.finance.service;

import com.fct.finance.data.entity.*;
import com.fct.finance.service.business.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by jon on 2017/4/7.
 */
@Service(value = "financeService")
public class FinanceServiceImpl implements com.fct.finance.interfaces.FinanceService {

    @Override
    public PayOrder createPayOrder(PayOrder payOrder) {

        return PayOrderManager.instance.create(payOrder);
    }

    @Override
    public PayOrder getPayOrder(String orderId){
        return  PayOrderManager.instance.findOne(orderId);
    }

    @Override
    public PayOrder getPayOrderByTrade(String tradeType,String tradeId)
    {
        return  PayOrderManager.instance.findByTrade(tradeType,tradeId);
    }

    @Override
    public PayOrder paySuccess(String orderId,String platform,String notifyData){
        return  PayOrderManager.instance.paySuccess(orderId,platform,notifyData);
    }

    @Override
    public void payTradeNotify(String jsonMQPayTrade){

        PayOrderManager.instance.tradeNotify(jsonMQPayTrade);
    }

    @Override
    public Page<PayOrder> findPayRecord(Integer memberId, String cellPhone, String platform, String tradeId, String tradeType,
                                        Integer status,String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        return  PayOrderManager.instance.findAll(memberId,cellPhone,platform,tradeId,tradeType,status,
                beginTime,endTime,pageIndex,pageSize);
    }

    @Override
    public MemberAccount getMemberAccount(Integer memberId)
    {
        return MemberAccountManager.instance.findById(memberId);
    }

    @Override
    public Page<MemberAccount> findMemberAccount(String cellPhone,Integer orderBy,Integer pageIndex,Integer pageSize)
    {
        return  MemberAccountManager.instance.findAll(cellPhone,orderBy,pageIndex,pageSize);
    }

    @Override
    public Page<MemberAccountHistory> findMemberAccountHistory(Integer memberId,String cellPhone, String tradeId, String tradeType,
                                                        Integer pageIndex, Integer pageSize)
    {
        return MemberAccountHistoryManager.instance.findAll(memberId,cellPhone,tradeId,tradeType,pageIndex,pageSize);
    }
    @Override
    public RefundRecord createRefundRecord(RefundRecord refund)
    {
        return RefundRecordManager.instance.create(refund);
    }

    @Override
    public void refundConfirm(Integer omsOperaterId,String ids)
    {
        RefundRecordManager.instance.confirm(omsOperaterId,ids);
    }

    @Override
    public void refundSuccess(Integer refundId,String notifyData)
    {
        RefundRecordManager.instance.success(refundId,notifyData);
    }

    @Override
    public Page<RefundRecord> findRefundRecord(Integer memberId,String cellPhone,String tradeId,String tradeType,String payPlatform,
                                        Integer status,String beginTime,String endTime,Integer pageIndex,Integer pageSize)
    {
        return  RefundRecordManager.instance.findAll(memberId,cellPhone,tradeId,tradeType,payPlatform,status,beginTime,endTime,
                pageIndex,pageSize);
    }
    @Override
    public void applyWithdraw(WithdrawRecord withdrawRecord)
    {
        WithdrawRecordManager.instance.apply(withdrawRecord);
    }

    @Override
    public void withdrawSuccess(Integer omsOperaterId,Integer id)
    {
        WithdrawRecordManager.instance.updateStatus(omsOperaterId,id,1,"");
    }

    @Override
    public void withdrawFail(Integer omsOperaterId,Integer id,String desc)
    {
        WithdrawRecordManager.instance.updateStatus(omsOperaterId,id,2,desc);
    }

    @Override
    public Page<WithdrawRecord> findWithdrawRecord(Integer memberId, String cellPhone, Integer status,
                                            String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        return  WithdrawRecordManager.instance.findAll(memberId,cellPhone,status,beginTime,endTime,pageIndex,
                pageSize);
    }
    @Override
    public Integer createSettleRecord(SettleRecord settleRecord)
    {
        return  SettleRecordManager.instance.create(settleRecord);
    }

    @Override
    public void settleConfirm(Integer omsOperaterId,String ids)
    {
        SettleRecordManager.instance.updateStatus(omsOperaterId,1,ids,"");
    }

    @Override
    public void settleRefuse(Integer omsOperaterId,Integer id,String remark)
    {
        SettleRecordManager.instance.updateStatus(omsOperaterId,3,id.toString(),remark);
    }

    @Override
    public void settleTask()
    {
        SettleRecordManager.instance.task();
    }


    @Override
    public SettleRecord getSettleRecord(Integer recordId)
    {
        return  SettleRecordManager.instance.findById(recordId);
    }

    @Override
    public Page<SettleRecord> findSettleRecord(Integer memberId, String cellPhone,String tradeType, String tradeId, Integer status,
                                        String beginTime, String endTime, Integer pageIndex,Integer pageSize)
    {
        return  SettleRecordManager.instance.findAll(memberId,cellPhone,tradeType,tradeId,status,beginTime,endTime,
                pageIndex,pageSize);
    }
    @Override
    public List<PayPlatform> findPayPlatform()
    {
        return  PayPlatformManager.instance.findAll();
    }

    @Override
    public Integer createRechargeRecord(RechargeRecord record)
    {
        return RechargeRecordManager.instance.create(record);
    }

    @Override
    public RechargeRecord getRechargeRecord(Integer id)
    {
        return RechargeRecordManager.instance.findById(id);
    }

    @Override
    public void rechargeSuccess(Integer id, String payOrderId, String payPlatform, String payTime,String payStatus)
    {
        RechargeRecordManager.instance.paySuccess(id,payOrderId,payPlatform,payTime,payStatus);
    }

    @Override
    public Page<RechargeRecord> findRechargeRecord(Integer memberId,String cellPhone,Integer status,
                                            String beginTime, String endTime, Integer pageIndex,Integer pageSize)
    {
        return RechargeRecordManager.instance.findAll(memberId,cellPhone,status,beginTime,endTime,pageIndex,pageSize);
    }

}
