package com.example.easypoi.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.easypoi.pojo.Customer;
import com.example.easypoi.service.CustomerService;
import com.example.easypoi.utils.IOExcelUtils;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cust")
@Api(tags = "CustController")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @GetMapping("/getCustomer")
    @ApiOperation(value = "获取所有列表", notes = "无需参数")
    public List<Customer> getCustomer() {
        return customerService.selectCustomer();
    }


    @GetMapping("/getByWrapper")
    @ApiOperation("模糊范围查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "客户名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "startLevel", value = "起始等级", required = false, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endLevel", value = "结束等级", required = false, dataType = "String")
    })
    public List<Customer> getByWrapper(@RequestParam(value = "username") String name, @RequestParam(value = "startLevel", required = false) String startLevel,
                                       @RequestParam(value = "endLevel", required = false) String endLevel) {

        return customerService.selectByWrapper(name, endLevel, startLevel);
    }


    @GetMapping("/getByPage")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "客户名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页大小", required = false, dataType = "Integer")
    })
    public ResultBody<IPage<Customer>> getByPage(@RequestParam(value = "username") String name, @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return ResultBody.ok().data("pageInfo", customerService.selectByPage(name, pageNum, pageSize));
    }

    @PostMapping("/getByPage2")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "客户名称", dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "Integer",paramType = "query")
    })
    public ResultBody<IPage<Customer>> getByPage2(@RequestParam(value = "username", required = false) String name, @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return ResultBody.ok().data("pageInfo", customerService.selectByPage(name, pageNum, pageSize));
    }


    @GetMapping("/exportExcel")
    @ApiOperation("导出excel")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "客户名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页大小", required = false, dataType = "Integer")
    })
    public void exportExcel(@RequestParam(value = "username") String name, @RequestParam(value = "pageNum", required = false) Integer pageNum,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize, HttpServletResponse response) {

        IPage<Customer> page = customerService.selectByPage(name, pageNum, pageSize);
        List<Customer> list = page.getRecords();
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type","application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("客户信息统计.xls", "UTF-8"));
            response.setContentType("application/octet-stream");

            //标题  表名  导出类型  HSSF xls  XSSF xlsx
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("客户信息","客户信息统计", ExcelType.HSSF), Customer.class,list);
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();

            try {
                FileOutputStream fout = new FileOutputStream("D:/aa.xls");
                workbook.write(fout);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
    }


    @PostMapping("/importExcel")
    @ApiOperation("excel导入")
    public ResultBody importExcel() throws Exception {

        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        long start = new Date().getTime();
        List<Customer> list = ExcelImportUtil.importExcel(
                new File("D:\\aa.xls"),
                Customer.class, params);
        System.out.println(new Date().getTime() - start);
        System.out.println(list.size());
        System.out.println(ReflectionToStringBuilder.toString(list.get(0)));
        System.out.println(ReflectionToStringBuilder.toString(list.get(1)));

        customerService.batchSaveByExcel(list);
        return ResultBody.ok();
    }

    @PostMapping("/upload")
    @ApiOperation("excel文件导入")
    public ResultBody inputFileExcel(@RequestParam("file") MultipartFile file){
        List<Customer> list = null;

        int filed = 0;
        int succes = 0;
        //获得文件名
        String filename = file.getOriginalFilename();
        try {
            ImportParams importParams = new ImportParams();
//            importParams.setNeedVerify(true);//需要验证
            list = IOExcelUtils.importExcel(file, Customer.class);
            System.out.println(ReflectionToStringBuilder.toString(list.get(0)));
            customerService.batchSaveByExcel(list);
            if (list == null || list.size() == 0)
                return ResultBody.error().message("空表格");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.ok();
    }

    @PostMapping("/uploadMore")
    @ApiOperation("excel多表格导入")
    public ResultBody inputMoreExcel(@RequestParam("file") MultipartFile file) throws IOException {

        //获得文件名
        String filename = file.getOriginalFilename();

        String suffixName = filename.substring(filename.lastIndexOf(".") + 1);
        InputStream is = file.getInputStream();
        Workbook workbook = IOExcelUtils.getWorkbook(is, suffixName);

        Sheet sheet = workbook.getSheetAt(0);
        String trim = sheet.getSheetName().trim();
        Row row = sheet.getRow(0);

        System.out.println("sheet = " + sheet);
        System.out.println("trim = " + trim);
        System.out.println("row = " + row);

        // 获取有效单元格数
        int cellNum = row.getPhysicalNumberOfCells();
        int rowNum = sheet.getPhysicalNumberOfRows();

        // 表头集合
        List<String> headList = new ArrayList<>();
        for (int i = 0; i < rowNum; i++) {
            if (row.getCell(i)!=null){
                Cell cell = row.getCell(i);
                String val = cell.getStringCellValue();
                System.out.println("val = "+i+"" + val);
                headList.add(val);
            }
        }
        System.out.println("rowNum = " + rowNum);
        System.out.println("cellNum = " + cellNum);
        System.out.println("headList = " + headList);

        return ResultBody.ok();
    }


}
