package com.fct.finance.interfaces;

import com.fct.finance.data.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * Created by jon on 2017/4/7.
 */
public interface FinanceService {

    //生成支付记录
    PayOrder createPayOrder(PayOrder payOrder);

    //根据支付订单获取支付记录
    PayOrder getPayOrder(String orderId);

    PayOrder getPayOrderByTrade(String tradeType,String tradeId);

    //第三方支付成功处理业务
    PayOrder paySuccess(String orderId,String platform,String notifyData);

    //业务交易处理通知
    void payTradeNotify(String jsonMQPayTrade);

    //根据条件获取支付记录
    Page<PayOrder> findPayRecord(Integer memberId, String cellPhone, String platform, String tradeId, String tradeType,
                                 Integer status,String beginTime,String endTime,Integer pageIndex, Integer pageSize);

    //根据用户id获取账户
    MemberAccount getMemberAccount(Integer memberId);

    //获取账户列表
    Page<MemberAccount> findMemberAccount(String cellPhone,Integer orderBy,Integer pageIndex,Integer pageSize);

    //获取账户变更记录
    Page<MemberAccountHistory> findMemberAccountHistory(Integer memberId, String cellPhone,String tradeId, String tradeType,
                                                        Integer pageIndex, Integer pageSize);
    //创建退款记录
    RefundRecord createRefundRecord(RefundRecord refund);

    //财务确认退款
    void refundConfirm(Integer omsOperaterId,String ids);

    //第三方支付平台同步通知退款成功
    void refundSuccess(Integer refundId,String notifyData);

    //根据条件获取退款数据
    Page<RefundRecord> findRefundRecord(Integer memberId,String cellPhone,String tradeId,String tradeType,String payPlatform,
                                        Integer status,String beginTime,String endTime,Integer pageIndex,Integer pageSize);
    //用户申请提现
    void applyWithdraw(WithdrawRecord withdrawRecord);

    //提现完成
    void withdrawSuccess(Integer omsOperaterId,Integer id);

    //提现失败
    void withdrawFail(Integer omsOperaterId,Integer id,String desc);

    //根据条件获取提现数据
    Page<WithdrawRecord> findWithdrawRecord(Integer memberId, String cellPhone, Integer status,
                                            String beginTime,String endTime,Integer pageIndex, Integer pageSize);
    //创建销售结算，并返回结算Id，订单保存
    Integer createSettleRecord(SettleRecord settleRecord);

    //财务批量确认结算
    void settleConfirm(Integer omsOperaterId,String ids);

    //拒绝结算
    void settleRefuse(Integer omsOperaterId,Integer id,String remark);

    void settleTask();

    //根据id获取结算明细
    SettleRecord getSettleRecord(Integer recordId);

    //根据条件获取结算数据列表
    Page<SettleRecord> findSettleRecord(Integer memberId, String cellPhone,String tradeType, String tradeId, Integer status,
                                        String beginTime, String endTime, Integer pageIndex,Integer pageSize);
    //获取支付平台方式
    List<PayPlatform> findPayPlatform();

    Integer createRechargeRecord(RechargeRecord record);

    RechargeRecord getRechargeRecord(Integer id);

    void rechargeSuccess(Integer id, String payOrderId, String payPlatform, String payTime,String payStatus);

    Page<RechargeRecord> findRechargeRecord(Integer memberId,String cellPhone,Integer status,
                                            String beginTime, String endTime, Integer pageIndex,Integer pageSize);
}
