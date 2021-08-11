package com.example.easypoi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.pojo.CustomerImportExcel;

import java.util.List;

public interface CustomerService extends IService<Customer> {


    List<Customer> selectCustomer();

    List<Customer> selectByWrapper(String name, String endLevel, String startLevel);

    IPage<Customer> selectByPage(String name, Integer pageNum, Integer pageSize);

    void batchSaveByExcel(List<Customer> list);

    void batchSaveEasyExcel(List<CustomerImportExcel> list);
}
