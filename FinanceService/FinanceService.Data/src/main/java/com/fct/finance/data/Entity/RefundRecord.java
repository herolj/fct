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
public class RefundRecord {
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
    /// 业务交易类型
    /// </summary>
    private String tradeType;

    /// <summary>
    /// 业务交易Id
    /// </summary>
    private String tradeId;

    /// <summary>
    /// 退款方式(0:余额,1:原路返回,2:线下转账)
    /// </summary>
    private Integer method;

    /// <summary>
    /// 支付订单号(用于原路返回)
    /// </summary>
    private String payOrderId;

    /// <summary>
    /// 支付平台(用于原路返回)
    /// </summary>
    private String payPlatform;

    /// <summary>
    /// 第三方支付平台交易Id
    /// </summary>
    private String payPlatformOrderId;

    /// <summary>
    /// 线上支付金额
    /// </summary>
    private BigDecimal payAmount;

    /// <summary>
    /// 原路退回现金
    /// </summary>
    private BigDecimal cashAmount;

    /// <summary>
    /// 退回账户余额
    /// </summary>
    private BigDecimal accountAmount;

    /// <summary>
    /// 退回积分
    /// </summary>
    private Integer points;

    /// <summary>
    /// 总退款金额
    /// </summary>
    private BigDecimal refundAmount;

    /// <summary>
    /// 备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 退款状态(0:待处理,1:财务已确认,2:退款中,3:退款成功)
    /// </summary>
    private Integer status;

    /// <summary>
    /// 操作管理员Id
    /// </summary>
    private Integer omsOperatorId;

    /// <summary>
    /// 第三方通知内容
    /// </summary>
    private String notifyData;

    /// <summary>
    /// 是否通知业务方处理完成
    /// </summary>
    private Integer isNotify;

    /// <summary>
    /// 更新时间
    /// </summary>
    private Date updateTime;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;
}
