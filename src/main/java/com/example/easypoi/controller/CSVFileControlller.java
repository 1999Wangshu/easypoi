package com.example.easypoi.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.service.CustomerService;
import com.example.easypoi.utils.ExportCSVUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/csv")
@Api(tags = "csv文件")
public class CSVFileControlller {

    @Autowired
    private CustomerService customerService;


    @PostMapping("/exportCsv")
    @ApiOperation("直接导出csv")
    private void getSkuList(HttpServletResponse response){

        IPage<Customer> page = customerService.selectByPage("小", 1, 10);
        List<Customer> list = page.getRecords();

        String[] tableHeaderArr = {"客户编号","客户名称","负责人id",
                "创建人id","客户信息来源","客户所属行业","客户级别","联系人",
                "固定电话","移动电话","邮政编码","联系地址","创建时间",};
        ArrayList<String> list1 = new ArrayList<>();

        for (Customer customer : list) {
            list1.add(customer.toString());
        }

        String fileName = "hh.csv";
        byte[] bytes = ExportCSVUtil.writeDataAfterToBytes(tableHeaderArr, list1);
        ExportCSVUtil.responseSetProperties(fileName,bytes, response);
    }


    @PostMapping("/exportObject")
    @ApiOperation("第三方导出csv")
    private void getCSVList(HttpServletResponse response){

        List<Customer> list = customerService.selectByPage("小", 1, 10).getRecords();

        String[] tableHeaderArr = {"客户编号","客户名称","负责人id",
                "创建人id","客户信息来源","客户所属行业","客户级别","联系人",
                "固定电话","移动电话","邮政编码","联系地址","创建时间",};
        List<Object[]> list1 = new ArrayList<>();

        for (Customer cs : list) {
            list1.add(new Object[]{cs.getCustId(), cs.getCustName(), cs.getCustUserId()});
        }

        String s = JSON.toJSONString(list);


        String fileName = "customer.csv";
        byte[] bytes = ExportCSVUtil.writeCsvAfterToBytes(tableHeaderArr,list1);
        //下载
        ExportCSVUtil.responseSetProperties(fileName,bytes, response);

    }

}
