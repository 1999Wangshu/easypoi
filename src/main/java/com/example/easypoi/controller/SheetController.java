package com.example.easypoi.controller;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.example.easypoi.interceptor.UserExcelVerifyHandler;
import com.example.easypoi.pojo.User;
import com.example.easypoi.service.UserService;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/sheet")
@Api("MoreSheet")
public class SheetController {


    @Resource
    private UserService userService;

    @Resource
    private UserExcelVerifyHandler userExcelVerifyHandler;


    @GetMapping(value = "/excel")
    @ApiOperation("单sheet导出")
    public void exportSheetExcel(HttpServletResponse response){

        userService.exportUser(response);
    }


    @GetMapping(value = "exportSheets")
    @ApiOperation("多sheet导出")
    public void exportSheets(HttpServletResponse response){

        userService.exportSheets(response);
    }



    @PostMapping(value = "/importExcel")
    @ApiOperation("单sheet导入")
    public ResultBody exportSheetExcel(@RequestParam("file")  MultipartFile file,HttpServletResponse response) throws Exception{

        ImportParams importParams = new ImportParams();
        // 表头在第几行
        importParams.setTitleRows(1);
        // 距离表头中间有几行不要的数据
        importParams.setHeadRows(1);
        // 需要验证
        importParams.setNeedVerify(true);
        importParams.setVerifyHandler(userExcelVerifyHandler);
//        importParams.setVerifyGroup(new Class[]{User.class});

        ExcelImportResult<User> result = ExcelImportUtil.importExcelMore(file.getInputStream(), User.class,
                importParams);
        List<User> successList = result.getList();
        for (User user : successList) {
            System.out.println(user);
        }
        userService.importExcel(successList);

        //如果有错误，把错误数据返回到前台(让前台下载一个错误的excel)
        System.out.println(">>>>>>>>>>>"+result.isVerfiyFail());
        if(result.isVerfiyFail()){//校验数据是否有误
            //拿到错误的文件薄
            Workbook failWorkbook = result.getFailWorkbook();
            //把这个文件导出
            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); //mime类型
            //告诉浏览下载的是一个附件
            response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("error.xlsx","UTF-8"));
            response.setHeader("Pragma", "No-cache");//设置不要缓存
            //response.setHeader("content-Type", "application/vnd.ms-excel;charset=utf-8");
            response.setContentType("application/octet-stream");
            OutputStream ouputStream = response.getOutputStream();
            failWorkbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        }

        return ResultBody.ok().message("成功导入");
    }


    @PostMapping(value = "/importSheets")
    @ApiOperation("多sheet导入")
    public ResultBody exportSheets(@RequestParam("file") MultipartFile file,HttpServletResponse response){

        try {
            return userService.importSheet(file,response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }




}
