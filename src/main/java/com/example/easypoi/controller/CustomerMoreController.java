package com.example.easypoi.controller;


import com.example.easypoi.utils.ExcelMoreUtils;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/more")
@Api(tags = "多表格")
public class CustomerMoreController {


    @GetMapping("/inputFile")
    @ApiOperation("根据列/行获取")
    public ResultBody inputFile(){

//        String filename = file.getOriginalFilename();

        List<String> set = ExcelMoreUtils.getColumnSet("D:/aa.xls", 1, 1, 24);
        System.out.println("set = " + set);


        return ResultBody.ok();
    }


    @GetMapping("/inputFile2")
    @ApiOperation("根据列/行获取集合数据")
    public ResultBody inputFile2(){

//        String filename = file.getOriginalFilename();

        List<String> set = ExcelMoreUtils.getColumnSet("D:/aa.xls", 1, 1);
        System.out.println("set = " + set);


        return ResultBody.ok();
    }


}
