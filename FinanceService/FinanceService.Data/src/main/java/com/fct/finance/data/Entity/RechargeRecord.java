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
public class RechargeRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 会员Id
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 手机号码
    /// </summary>
    private String cellPhone;

    /// <summary>
    /// 充值金额,线上应付金额
    /// </summary>
    private BigDecimal amount;

    /// <summary>
    /// 赠送金额，活动期间默认0
    /// </summary>
    private BigDecimal giftAmount;

    /// <summary>
    /// 支付订单号
    /// </summary>
    private String payOrderId;

    /// <summary>
    /// 支付平台方式
    /// </summary>
    private String payPlatform;

    /// <summary>
    /// 支付时间
    /// </summary>
    private Date payTime;

    /// <summary>
    /// 充值状态：等待充值，充值成功。充值失败。
    /// </summary>
    private Integer status;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 订单过期时间，过期关闭不可操作
    /// </summary>
    private Date expiredTime;
}
