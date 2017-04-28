package com.fct.finance.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import javax.persistence.*;

/**
 * Created by jon on 2017/4/7.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayPlatform {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 平台名称：支付宝APP支付、微信APP支付、微信H5支付、银联H5支付、银联app支付、线下支付
    /// </summary>
    private String name;

    /// <summary>
    /// fct_alipayapp、fct_wxpayapp、fct_wxpayh5、fct_unionpayh5、fct_unionpayapp、fct_offline
    /// </summary>
    private String code;

    /// <summary>
    /// 状态：{1:启用，0:禁用}
    /// </summary>
    private Integer status;

    /// <summary>
    /// 排序优先级
    /// </summary>
    private Integer sortIndex;
}
