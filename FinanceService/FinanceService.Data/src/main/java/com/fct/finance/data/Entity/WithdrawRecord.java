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
public class WithdrawRecord {

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
    /// 账户姓名
    /// </summary>
    private String name;

    /// <summary>
    /// 提现平台(支付宝、银行)
    /// </summary>
    private String bankName;

    /// <summary>
    /// 账号
    /// </summary>
    private String bankAccount;

    /// <summary>
    /// 提现金额
    /// </summary>
    private BigDecimal amount;

    /// <summary>
    /// 操作管理员Id
    /// </summary>
    private Integer omsOperaterId;

    /// <summary>
    /// 状态{0:待处理,1:已处理,2:处理失败}
    /// </summary>
    private Integer status;;

    ////// <summary>
    /// 备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 最后更新时间
    /// </summary>
    private Date updateTime;
}
