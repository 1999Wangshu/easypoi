package com.example.easypoi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.easypoi.mapper.CustomerMapper;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.pojo.CustomerImportExcel;
import com.example.easypoi.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Customer> selectCustomer() {
        return customerMapper.selectList(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> selectByWrapper(String name, String endLevel, String startLevel) {

        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(Customer::getCustName, name)
                .between(Customer::getCustLevel, startLevel, endLevel);

        return customerMapper.selectList(wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<Customer> selectByPage(String name, Integer pageNum, Integer pageSize) {


        Page<Customer> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Customer> query = new QueryWrapper<>();
        query.lambda().like(name != null && !"".equals(name),Customer::getCustName, name);
        return customerMapper.selectPage(page, query);
    }

    @Override
    @Transactional
    public void batchSaveByExcel(List<Customer> list) {

        AtomicInteger i = new AtomicInteger(1);

        list.stream().forEach(dat ->{
            i.set(i.get() +1);
            System.out.println("id" + dat.getCustId());
            if (StringUtils.isBlank(dat.getCustId().toString())){
                throw new RuntimeException("第"+i+"行客户id不能为空");
            }
            if (StringUtils.isBlank(dat.getCustName())){
                throw new RuntimeException("第"+i+"行客户名称不能为空");
            }
            if (StringUtils.isBlank(dat.getCustCreatetime().toString())){
                throw new RuntimeException("第"+i+"行创建时间不能为空");
            }

            Customer customer = new Customer();
            BeanUtils.copyProperties(dat,customer);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            String id = "";
            for (int x = 0; x < 7; x++) {
                id += random.nextInt(10);
            }
            customer.setCustId(Integer.parseInt(id));
            customerMapper.insert(customer);
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveEasyExcel(List<CustomerImportExcel> importExcels) {

        AtomicInteger i = new AtomicInteger(1);
        ArrayList<Customer> list = new ArrayList<>();

        importExcels.stream().forEach(dat ->{
            i.set(i.get()+1);
            if (StringUtils.isBlank(dat.getCustId().toString())){
                throw new RuntimeException("第"+i+"行客户id不能为空");
            }
            if (StringUtils.isBlank(dat.getCustName())){
                throw new RuntimeException("第"+i+"行客户名称不能为空");
            }
            if (StringUtils.isBlank(dat.getCustCreatetime().toString())){
                throw new RuntimeException("第"+i+"行创建时间不能为空");
            }
            Customer customer = new Customer();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            String id = "";
            for (int x = 0; x < 7; x++) {
                id += random.nextInt(10);
            }
            customer.setCustId(Integer.parseInt(id));
            customer.setCustCreatetime(new Date());
            customer.setCustAddress(dat.getCustAddress());
            customer.setCustCreateId(dat.getCustCreateId());
            customer.setCustIndustry(dat.getCustIndustry());
            customer.setCustLevel(dat.getCustLevel());
            customer.setCustLinkman(dat.getCustLinkman());
            customer.setCustMobile(dat.getCustMobile());
            customer.setCustName(dat.getCustName());
            customer.setCustPhone(dat.getCustPhone());
            customer.setCustSource(dat.getCustSource());
            customer.setCustUserId(dat.getCustUserId());
            customer.setCustZipcode(dat.getCustZipcode());
            list.add(customer);

        });

        list.forEach(dat->{
            customerMapper.insert(dat);
        });

    }
}
