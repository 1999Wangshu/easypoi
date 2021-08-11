package com.example.easypoi.pojo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@ApiModel(value = "客户实体")
public class User implements Serializable {

    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户id", height = 5, width = 10, isImportField = "true_st")
    private Integer userId;

    @Excel(name = "用户账号", height = 5, width = 10,orderNum = "1", isImportField = "true_st")
    @NotNull
    @ApiModelProperty(value = "用户账号")
    private String userCode;

    @Excel(name = "用户名称", height = 5, width = 10,orderNum = "2", isImportField = "true_st")
    @Pattern(regexp = "[\u4E00-\u9FA5]*", message = "不是中文")
    @ApiModelProperty(value = "用户名称")
    private String userName;

    @Excel(name = "用户密码", height = 5, width = 10,orderNum = "3", isImportField = "true_st")
    @Min(value = 6)
    @ApiModelProperty(value = "用户密码")
    private String userPassword;

    @Excel(name = "用户状态1:正常,0:停用", height = 5, width = 20,orderNum = "4", isImportField = "true_st")
    @ApiModelProperty(value = "状态 1:正常,0:停用")
    private Integer userState;

}
