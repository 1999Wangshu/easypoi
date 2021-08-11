package com.example.easypoi.pojo;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ContentRowHeight(10) //行高
@HeadRowHeight(30)	// 表头行高
@ColumnWidth(15)		// 表头行宽EasyExcel
public class CustomerImportExcel extends BaseRowModel {

    @ApiModelProperty(value = "客户编号(主键)")
    @ExcelProperty(value = "客户编号")
    @NumberFormat("#%")          //参照java.text.DecimalFormat。
    private Integer custId;

    @ApiModelProperty(value = "客户名称")
    @ExcelProperty(value = "客户名称")
    private String custName;

    @ApiModelProperty(value = "负责人id")
    @ExcelProperty(value = "负责人id")
    private Integer custUserId;

    @ApiModelProperty(value = "创建人id")
    @ExcelProperty(value = "创建人id")
    private Integer custCreateId;

    @ApiModelProperty(value = "客户信息来源")
    @ExcelProperty(value = "客户信息来源")
    private String custSource;

    @ApiModelProperty(value = "客户所属行业")
    @ExcelProperty(value = "客户信息来源")
    private String custIndustry;

    @ApiModelProperty(value = "客户级别")
    @ExcelProperty(value = "客户信息来源")
    private String custLevel;

    @ApiModelProperty(value = "联系人")
    @ExcelProperty(value = "联系人")
    private String custLinkman;

    @ApiModelProperty(value = "固定电话")
    @ColumnWidth(30)
    @ExcelProperty(value = "固定电话")
    private String custPhone;

    @ApiModelProperty(value = "移动电话")
    @ColumnWidth(30)
    @ExcelProperty(value = "移动电话")
    private String custMobile;

    @ApiModelProperty(value = "邮政编码")
    @ExcelProperty(value = "邮政编码")
    private String custZipcode;

    @ApiModelProperty(value = "联系地址")
    @ColumnWidth(30)
    @ExcelProperty(value = "联系地址")
    private String custAddress;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    private Date custCreatetime;

}
