package com.example.easypoi.pojo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("customer")
@ApiModel(value = "客户实体")
public class Customer implements Serializable {

    @JSONField(name="custId", ordinal = 1) //csv文件使用
    @ApiModelProperty(value = "客户编号(主键)")
    @Excel(name = "客户编号", height = 5, width = 10, isImportField = "true_st")
    private Integer custId;

    @JSONField(name="custName", ordinal = 2)
    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称", height = 5, width = 10,orderNum = "1", isImportField = "true_st")
    private String custName;

    @JSONField(name="custUserId", ordinal = 3)
    @Excel(name = "负责人id", height = 5, width = 10, orderNum = "2",isImportField = "true_st")
    @ApiModelProperty(value = "负责人id")
    private Integer custUserId;

    @JSONField(name="custCreateId", ordinal = 4)
    @Excel(name = "创建人id", height = 5, width = 10,orderNum = "3", isImportField = "true_st")
    @ApiModelProperty(value = "创建人id")
    private Integer custCreateId;

    @JSONField(name="custSource", ordinal = 5)
    @Excel(name = "客户信息来源", height = 5, width = 10,orderNum = "4", isImportField = "true_st")
    @ApiModelProperty(value = "客户信息来源")
    private String custSource;

    @JSONField(name="custIndustry", ordinal = 6)
    @Excel(name = "客户所属行业", height = 5, width = 10,orderNum = "5", isImportField = "true_st")
    @ApiModelProperty(value = "客户所属行业")
    private String custIndustry;

    @JSONField(name="custLevel", ordinal = 7)
    @Excel(name = "客户级别", height = 5, width = 10,orderNum = "6", isImportField = "true_st")
    @ApiModelProperty(value = "客户级别")
    private String custLevel;

    @JSONField(name="custLinkman", ordinal = 8)
    @Excel(name = "联系人", height = 5, width = 10,orderNum = "7", isImportField = "true_st")
    @ApiModelProperty(value = "联系人")
    private String custLinkman;

    @JSONField(name="custPhone", ordinal = 9)
    @Excel(name = "固定电话", height = 5, width = 20,orderNum = "8", isImportField = "true_st")
    @ApiModelProperty(value = "固定电话")
    private String custPhone;

    @JSONField(name="custMobile", ordinal = 10)
    @Excel(name = "移动电话", height = 5, width = 20,orderNum = "9", isImportField = "true_st")
    @ApiModelProperty(value = "移动电话")
    private String custMobile;

    @JSONField(name="custZipcode", ordinal = 11)
    @Excel(name = "邮政编码", height = 5, width = 10,orderNum = "10", isImportField = "true_st")
    @ApiModelProperty(value = "邮政编码")
    private String custZipcode;

    @JSONField(name="custAddress", ordinal = 12)
    @Excel(name = "联系地址", height = 5, width = 20,orderNum = "11", isImportField = "true_st")
    @ApiModelProperty(value = "联系地址")
    private String custAddress;

    @Excel(name = "创建时间", height = 5, width = 20,orderNum = "12", isImportField = "true_st",databaseFormat = "yyyy-MM-dd HH:mm:ss", format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "cust_createtime",fill = FieldFill.INSERT)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss",name="custCreatetime", ordinal = 13)
    private Date custCreatetime;

}
