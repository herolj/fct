package com.fct.message.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jon on 2017/4/7.
 */

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageQueue {

    //会会Id
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String sourceAppName;

    private String  typeId;

    private String targetModule;

    private String body;

    private String remark;

    private Date processTime;

    private Integer requestCount;

    private String failMessage;

    private Integer status;

    private Date createTime;
}