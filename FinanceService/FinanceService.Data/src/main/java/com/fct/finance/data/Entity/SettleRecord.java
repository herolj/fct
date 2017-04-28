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
public class SettleRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 店长用户id
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 用户手机号码
    /// </summary>
    private String cellPhone;

    /// <summary>
    /// 店铺Id
    /// </summary>
    private Integer shopId;

    /// <summary>
    /// 交易类型 buy
    /// </summary>
    private String tradeType;

    /// <summary>
    /// 交易类型主键
    /// </summary>
    private String tradeId;

    /// <summary>
    /// 销售金额
    /// </summary>
    private BigDecimal saleAmount;

    /// <summary>
    /// 销售佣金
    /// </summary>
    private BigDecimal commission;

    /// <summary>
    /// 邀请者会员Id
    /// </summary>
    private Integer inviterId;

    /// <summary>
    /// 邀请者获得佣金
    /// </summary>
    private BigDecimal inviterCommission;

    /// <summary>
    /// 管理员操作者Id
    /// </summary>
    private Integer omsOperaterId;

    /// <summary>
    /// 结算状态（0:待确认,1:财务已确认,2:结算成功,3:拒绝）
    /// </summary>
    private Integer status;;

    /// <summary>
    /// 拒绝结算时的备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 更新时间
    /// </summary>
    private Date updateTime;

}
