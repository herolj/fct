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
public class PayOrder {

    @Id
    private String orderId;

    /// <summary>
    /// 会员Id
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 手机号码
    /// </summary>
    private String cellPhone;

    /// <summary>
    /// 相关业务类型
    /// </summary>
    private String tradeType;

    /// <summary>
    /// 相关数据主键
    /// </summary>
    private String tradeId;

    /// <summary>
    /// 备注
    /// </summary>
    private  String Desc;

    /// <summary>
    /// 使用帐户支付的金额
    /// </summary>
    private BigDecimal accountAmount;

    /// <summary>
    /// 使用积分
    /// </summary>
    private Integer points;

    /// <summary>
    /// 使用折扣金额
    /// </summary>
    private BigDecimal discountAmount;

    /// <summary>
    /// 总金额
    /// </summary>
    private  BigDecimal totalAmount;

    /// <summary>
    /// 支付金额
    /// </summary>
    private  BigDecimal payAmount;

    /// <summary>
    /// 支付平台
    /// </summary>
    private String payPlatform;

    /// <summary>
    /// 支付时间
    /// </summary>
    private Date payTime;

    /// <summary>
    /// 状态{0：待付款，1：付款成功，2：关闭，3：异常}
    /// </summary>
    private Integer status;

    /// <summary>
    /// 宝贝详情显示url
    /// </summary>
    private String showUrl;

    /// <summary>
    /// 支付成功同步通知地址
    /// </summary>
    private String callbackUrl;

    /// <summary>
    /// 支付成功异步通知地址
    /// </summary>
    private String notifyUrl;

    /// <summary>
    /// 第三方通知内容
    /// </summary>
    private String notifyData;

    private BigDecimal refundAmount;

    private Integer refundPoints;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 订单过期时间
    /// </summary>
    private Date expiredTime;

}
