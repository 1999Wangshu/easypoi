package com.example.easypoi.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * EasyPoi Excel导入导出工具类
 */

public class IOExcelUtils {

    /**
     * @param num 生成位数
     * @return
     */
    public static String getUUID(int num){

        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < num; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    public static String getUUID(){

        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /**
     * excel 导出
     *
     * @param list     数据列表
     * @param fileName 导出时的excel名称
     * @param response
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        defaultExport(list, fileName, response);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list     数据列表
     * @param fileName 导出时的excel名称
     * @param response
     */
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel(fileName, response, workbook);
    }


    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param fileName     导出时的excel名称
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     * @param response
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams, HttpServletResponse response) throws IOException {
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list      数据列表
     * @param title     表格内数据标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param fileName  导出时的excel名称
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) throws IOException {

        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }


    /**
     * excel 导出
     *
     * @param list           数据列表
     * @param title          表格内数据标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param fileName       导出时的excel名称
     * @param isCreateHeader 是否创建表头
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param fileName     导出时的excel名称
     * @param response
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (fileName.contains(".xls") || fileName.contains(".xlsx")){
            downLoadExcel(response,fileName,workbook);
        }else {
            downLoadExcel(fileName, response, workbook);
        }
    }

    /**
     * excel下载
     *
     * @param fileName 下载时的文件名称
     * @param response
     * @param workbook excel数据
     */
    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) throws IOException {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel下载
     *
     * @param fileName 下载时的文件名称(不需要后缀)
     * @param response
     * @param workbook excel数据
     */
    public static void downLoadExcel(HttpServletResponse response,String fileName, Workbook workbook) throws IOException {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * 根据excel文件格式获知excel版本信息
     * @param is
     * @param str
     * @return
     */
    public static Workbook getWorkbook(InputStream is, String str) {
        Workbook book = null;
        try {
            if ("xls".equals(str)) {
                // 2003
                book = new HSSFWorkbook(is);
            } else {
                // 2007
                book = new XSSFWorkbook(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    /**
     * 得到错误信息
     * @param sb
     * @param list
     * @param i
     * @param obj
     * @param name  用哪个属性去表明不合规定的数据
     * @param msg
     * @throws Exception
     */
    public static void getWrongInfo(StringBuilder sb,List list,int i,Object obj,String name,String msg) throws Exception{
        Class clazz=obj.getClass();
        Object str=null;
        //得到属性名数组
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            if(f.getName().equals(name)){
                //用来得到属性的get和set方法
                PropertyDescriptor pd = new PropertyDescriptor(f.getName(), clazz);
                //得到get方法
                Method getMethod=pd.getReadMethod();
                str = getMethod.invoke(obj);
            }
        }
        if(i==0) {
            sb.append(msg + str + ";");
        }
        else if(i==(list.size()-1)) {
            sb.append(str + "</br>");
        }
        else {
            sb.append(str + ";");
        }
    }

    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws IOException {
        return importExcel(file, 1, 1, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param filePath   excel文件路径
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl("/excel/");
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("模板不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * excel 导入
     *
     * @param file       上传的文件
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (file == null) {
            return null;
        }
        try {
            return importExcel(file.getInputStream(), titleRows, headerRows, pojoClass);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param titleRows   表格内数据标题行
     * @param headerRows  表头行
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl("/excel/");
        params.setNeedSave(true);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 根据接收的Excel文件来导入多个sheet,根据索引可返回一个集合
     * @param file   导入文件
     * @param sheetIndex  导入sheet索引
     * @param titleRows  表标题的行数
     * @param headerRows 表头行数
     * @param pojoClass  Excel实体类
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file,int sheetIndex,Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        // 根据file得到Workbook,主要是要根据这个对象获取,传过来的excel有几个sheet页
        ImportParams params = new ImportParams();
        // 第几个sheet页
        params.setStartSheetIndex(sheetIndex);
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            throw new RuntimeException("导入失败！请检查导入文档的格式是否正确");
        }
        return list;
    }


}
