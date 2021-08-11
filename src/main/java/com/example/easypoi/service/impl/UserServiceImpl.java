package com.example.easypoi.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.easypoi.mapper.UserMapper;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.pojo.User;
import com.example.easypoi.service.CustomerService;
import com.example.easypoi.service.UserService;
import com.example.easypoi.utils.IOExcelUtils;
import com.example.easypoi.utils.POISheetUtils;
import com.example.easypoi.vo.ResultBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.easypoi.utils.IOExcelUtils.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CustomerService customerService;

    @Override
    public List<User> selectUser() {
        return userMapper.selectList(null);
    }

    @Override
    public List<User> findByUsername(String userName) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName,userName);
        return userMapper.selectList(wrapper);
    }

    @Override
    public void exportUser(HttpServletResponse response) {
        List<User> users = userMapper.selectList(null);

        try {
            exportExcel(users,"用户列表","用户信息表",User.class,System.currentTimeMillis()+"",response);
        } catch (IOException e) {
            throw new RuntimeException("导出数据异常");
        }
    }

    @Override
    public void exportSheets(HttpServletResponse response) {

        List<User> users = userMapper.selectList(null);

        List<Customer> customers = customerService.selectCustomer();

        //创建参数对象
        ExportParams exportParams1 = new ExportParams();
        //设置sheet
        exportParams1.setSheetName("用户信息");
        exportParams1.setTitle("用户列表");
        ExportParams exportParams2 = new ExportParams();
        exportParams2.setSheetName("客户信息");
        exportParams2.setTitle("客户列表");

        //创建sheet1使用的Map
        HashMap<String, Object> userMap = new HashMap<>();
        // title的参数为ExportParams类型
        userMap.put("title", exportParams1);
        // 模版导出对应得实体类型
        userMap.put("entity", User.class);
        // sheet中要填充得数据
        userMap.put("data", users);

        //创建sheet2使用的map
        HashMap<String, Object> customerMap = new HashMap<>();
        customerMap.put("title", exportParams2);
        customerMap.put("entity", Customer.class);
        customerMap.put("data", customers);

        //讲sheet1和sheet2进行包装
        ArrayList<Map<String,Object>> sheetsList = new ArrayList<>();
        sheetsList.add(userMap);
        sheetsList.add(customerMap);

        try {

            Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
            IOExcelUtils.downLoadExcel(response,IOExcelUtils.getUUID(),workbook);

        } catch (Exception e) {
            throw new RuntimeException("导出异常");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importExcel(List<User> users) {

        users.forEach(dat->{
            User user = new User();
            BeanUtils.copyProperties(dat,user);
            user.setUserId(Integer.parseInt(IOExcelUtils.getUUID(7)));
            userMapper.insert(user);
        });
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("");


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody importSheet(MultipartFile file,HttpServletResponse response) throws Exception{
        StringBuilder sb = new StringBuilder();
        // 根据file得到Workbook,主要是要根据这个对象获取,传过来的excel有几个sheet页
        try {
            InputStream is = file.getInputStream();
            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf(".") + 1);
            Workbook workBook = IOExcelUtils.getWorkbook(is,suffixName);
            ImportParams params = new ImportParams();
            //循环工作表sheet
            for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                Cell cell = workBook.getSheetAt(i).getRow(i).getCell(i);
                System.out.println("cell = " + cell);
                //表头在第几行
                params.setTitleRows(1);
                //距离表头中间几行不要有数据
                params.setStartRows(0);
                //第几个sheet页
                params.setStartSheetIndex(i);
                //验证数据
                params.setNeedVerify(true);
                params.setVerifyGroup(new Class[]{User.class});

                if (i == 0){
                    ExcelImportResult<User> result = ExcelImportUtil.importExcelMore(file.getInputStream(), User.class, params);
                    System.out.println(result.isVerfiyFail());
                    if (result.isVerfiyFail()){//校验数据是否合格
                        getError(result,response);
                    }
                    //合格
                    List<User> list = result.getList();
                    for (User user : list) {
                        System.out.println(user);
                    }
                    list.forEach(dat ->{
                        User user = new User();
                        BeanUtils.copyProperties(dat,user);
                        user.setUserId(Integer.parseInt(IOExcelUtils.getUUID(7)));
                        userMapper.insert(user);
                    });

                }else if (i==1){
                    ExcelImportResult<Customer> result = ExcelImportUtil.importExcelMore(file.getInputStream(), Customer.class, params);

                    if (result.isVerfiyFail()){//校验数据是否合格
                        List<Customer> errorList = result.getList();
                        System.out.println("hhhhhhhhh");
                        // 拼凑错误信息,自定义
                        for(int x=0;x<errorList.size();x++){
                            IOExcelUtils.getWrongInfo(sb, errorList, x, errorList.get(x), "name", "客户信息不合法");
                        }
                    }
                    List<Customer> list = result.getList();
                    for (Customer customer : list) {
                        System.out.println("customer = " + customer);
                    }
                    customerService.batchSaveByExcel(list);

                }
            }
        } catch (Exception e) {
            return ResultBody.error().message("导入失败！请检查导入文档的格式或插入数据是否正确"+sb.toString());
        }

        return ResultBody.ok().message("成功导入");

    }


    public void getError(ExcelImportResult<?> result, HttpServletResponse response) throws IOException {
        //拿到错误的文件薄
        Workbook failWorkbook = result.getFailWorkbook();
        //把这个文件导出
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); //mime类型

        response.setHeader("Content-disposition", "attachment;filename=error.xlsx"); //告诉浏览下载的是一个附件，名字叫做error.xlsx

        response.setHeader("Pragma", "No-cache");//设置不要缓存

        OutputStream ouputStream = response.getOutputStream();

        failWorkbook.write(ouputStream);

        ouputStream.flush();

        ouputStream.close();
    }

}
