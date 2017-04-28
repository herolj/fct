package com.fct.finance.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/4/7.
 */

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberAccount {

    //会会Id
    @Id
    private Integer memberId;

    //手机号码
    private String cellPhone;

    //累计获得积分
    private Integer accumulatePoints;

    //当前积分
    private Integer points;

    //累计收入金额,每次结算自动加,不做为任何业务，纯属记录统计显示。
    private BigDecimal accumulateIncome;

    //冻结金额
    private BigDecimal frozenAmount;

    //可用金额=充值金额+可申请提现金额（业务端使用）
    private  BigDecimal availableAmount;

    //充值金额（不可进行退款与提现）消费优先级最高。
    private  BigDecimal rechargeAmount;

    //可申请提现金额
    private  BigDecimal withdrawAmount;

    //创建时间
    private Date createTime;

    /**  区分充值金额不退和不可申请提现，防止充值中会有优惠赠送活动
     *
     * 收入情况下100元
     * availableAmount +=100 ; withdrawAmount +=100;
     *
     * 充值情况下100元
     * availableAmount +=100 ; rechargeAmount +=100;
     *
     * 消费情况下100元
     * 判断 availableAmount>=100
     * availableAmount -=100 ;rechargeAmount -=100（如不足）剩下的 withdrawAmount -=?;
     *
     * 提现情况下100元
     * 判断 withdrawAmount>=100
     *
     * availableAmount -=100 ; withdrawAmount -=100;
     * */

}