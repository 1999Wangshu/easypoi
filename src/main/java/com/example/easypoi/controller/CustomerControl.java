package com.example.easypoi.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.pojo.CustomerImportExcel;
import com.example.easypoi.service.CustomerService;
import com.example.easypoi.utils.EasyExcelUtils;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.Sun;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Api("customer")
@RequestMapping("/a")
public class CustomerControl {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/import")
    @ApiOperation("excel基础导入")
    public ResultBody importCustomerDaily(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<CustomerImportExcel> list = EasyExcel.read(inputStream)
                .head(CustomerImportExcel.class)
                // 设置sheet,默认读取第一个
                .sheet()
                // 设置标题所在行数
                .headRowNumber(2)
                .doReadSync();

        System.out.println(ReflectionToStringBuilder.toString(list.get(0)));

        customerService.batchSaveEasyExcel(list);

        return ResultBody.ok();
    }

    @PostMapping("/export")
    @ApiOperation(value = "excel基础导出",notes = "查询所有客户信息，返回所有客户信息的集合")
    public void exportCustomerDaily(HttpServletResponse response) throws IOException {

        List<Customer> customers = customerService.selectCustomer();
        List<CustomerImportExcel> list = new ArrayList<>();
        customers.stream().forEach(dat->{
            CustomerImportExcel excel = new CustomerImportExcel();
            BeanUtils.copyProperties(dat,excel);
            list.add(excel);
        });
        System.out.println(ReflectionToStringBuilder.toString(list.get(0)));

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导出", "UTF-8");
        response.setHeader("Content-disposition",  "attachment;filename=" +fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), CustomerImportExcel.class)
                .sheet("sheet0")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(list);

        OutputStream outputStream = new FileOutputStream("D:/bb.xlsx");
        ExcelWriter excelWriter = EasyExcelFactory.getWriter(outputStream);
        WriteSheet sheet = EasyExcel.writerSheet("客户信息").build();
        excelWriter.write(list,sheet);
        excelWriter.finish();
        outputStream.close();

    }

    @GetMapping("/readLess1000Row")
    @ApiOperation("读取小于1000行")
    public ResultBody readLess1000Row(){
        String filePath = "D:/bb.xlsx";
        List<Object> objects = EasyExcelUtils.readLessThan1000Row(filePath);
        System.out.println(objects);
        return ResultBody.ok();
    }

    @GetMapping("/readSheet1000Row")
    @ApiOperation("读取指定1000行")
    public ResultBody readSheet1000Row(){
        String filePath = "D:/bb.xlsx";
        //第一个1代表sheet1, 第二个1代表从第几行开始读取数据，行号最小值为0
        Sheet sheet = new Sheet(1, 1);
        List<Object> objects = EasyExcelUtils.readLessThan1000RowBySheet(filePath,sheet);
        System.out.println(objects);


        return ResultBody.ok();
    }


    @PostMapping("/exportCustomerDaily")
    @ApiOperation("excel映射导出")
    public void exportCustomerDaily() throws IOException {

        String filePath = "D:/cc.xlsx";
        List<Customer> customers = customerService.selectCustomer();
        List<CustomerImportExcel> list = new ArrayList<>();
        customers.stream().forEach(dat->{
            CustomerImportExcel excel = new CustomerImportExcel();
            BeanUtils.copyProperties(dat,excel);
            list.add(excel);
        });

        EasyExcelUtils.writeWithTemplate(filePath, list);

    }



}
