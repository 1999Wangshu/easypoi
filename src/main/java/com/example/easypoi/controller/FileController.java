package com.example.easypoi.controller;


import com.example.easypoi.utils.UploadUtil;
import com.example.easypoi.vo.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@Api("文件上传下载")
public class FileController {

    /*
     * 采用file.Transto 来保存上传的文件
     */
    @PostMapping("/upload")
    @ApiOperation("一般上传")
    public ResultBody upload(@RequestParam("file") MultipartFile file){

        long startTime = System.currentTimeMillis();
        //getName()获取标签的name属性值
        //getOriginalFilename()获取原生的文件名
        String filename = file.getOriginalFilename();
        //String suffixName = filename.substring(filename.lastIndexOf("."));
        String realPath = new Date().getTime()+filename;

        try {
            file.transferTo(new File(UploadUtil.getSavePath(),realPath));
        } catch (IOException e) {
            throw new RuntimeException("上传文件错误");
        }
        long endTime = System.currentTimeMillis();

        System.out.println(String.valueOf(endTime-startTime));
        return ResultBody.ok();
    }


    /**
     * 使用spring提供的上传文件
     * @param request
     * @return
     */
    @PostMapping("/springUpload")
    @ApiOperation("使用spring提供的上传")
    public ResultBody springUpload(HttpServletRequest request){

        long startTime = System.currentTimeMillis();
        //将当前上下文初始化给CommonsMultipartResolver
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext()
        );
        //检查form中是否有enctype="multipart/from-data"
        if (multipartResolver.isMultipart(request)){
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //获取multirequest的所有文件名
            Iterator iter = multiRequest.getFileNames();

            while (iter.hasNext()){
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file !=null){
                    String path = new Date().getTime() + file.getOriginalFilename();
                    try {
                        file.transferTo(new File(UploadUtil.getSavePath(),path));
                    } catch (IOException e) {
                        throw new RuntimeException("上传文件失败");
                    }
                }
            }

        }

        long endTime = System.currentTimeMillis();
        System.out.println("spring上传文件"+ (endTime-startTime));

        return ResultBody.ok();
    }

    @PostMapping("/fileUpload")
    public ResultBody fileUpload(HttpServletRequest request, @RequestParam MultipartFile[] upload) throws IOException {
        String path = request.getServletContext().getRealPath("/");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (upload != null && upload.length > 0) {
            for (int i = 0; i < upload.length; i++) {
                String filename = upload[i].getOriginalFilename();
                String uuid = UUID.randomUUID().toString().toUpperCase();
                filename = uuid + "_" + filename;
                upload[i].transferTo(new File(file, filename));
            }
        }
        return ResultBody.ok();
    }


    @GetMapping("download")
    @ApiOperation("一般下载")
    public ResponseEntity<byte[]> download(HttpServletRequest request,@RequestParam("filename") String filename) throws IOException{

        String downloadPath = UploadUtil.getSavePath();
        // 使用URLEncoding.decode对文件名进行解码
        filename = URLEncoder.encode(filename, "UTF-8");

        File file = new File(downloadPath + File.separator + filename);//从上传文件夹中去取
        HttpHeaders headers = new HttpHeaders();
        //设置编码
        String downloadFileName = new String(filename.getBytes("UTF-8"), "UTF-8");
        headers.setContentDispositionFormData("attachment",downloadFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //MediaType:互联网媒介类型  contentType：具体请求中的媒体类型信息

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
    }


    @GetMapping("fileDownload")
    @ApiOperation("文件下载")
    public ResponseEntity<byte[]> downFile(HttpServletRequest request, String filename) throws IOException {

        //指定要下载的文件所在路径
        String path = UploadUtil.getSavePath();
        //创建该文件对象
        File file = new File(path + File.separator + filename);

        //对文件名进行编码，防止中文文件名乱码
        filename = this.getFilename(request, filename);

        //设置响应头
        HttpHeaders headers = new HttpHeaders();
        //通知浏览器以下载的方式打开文件
        headers.setContentDispositionFormData("attachment", filename);
        //定义以流的形式下载返回文件数据
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        //使用springmvc框架的ResponseEntity对象封装返回数据
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    /**
     * 根据浏览器的不同进行编码设置
     * @param request  请求对象
     * @param filename 需要转码的文件名
     * @return 返回编码后的文件名
     * @throws IOException
     */
    public String getFilename(HttpServletRequest request, String filename) throws IOException {

        //IE不同版本User-Agent中出现的关键词
        String[] IEBrowserKeyWords = {"MSIE", "Trident", "Edge"};

        //获取请求头代理信息
        String userAgent = request.getHeader("User-Agent");
        for (String keyWord : IEBrowserKeyWords) {
            if (userAgent.contains(keyWord)) {
                //IE内核浏览器，统一为utf-8编码显示
                return URLEncoder.encode(filename, "UTF-8");
            }
        }
        //火狐等其他浏览器统一为ISO-8859-1编码显示
        return new String(filename.getBytes("UTF-8"), "ISO-8859-1");
    }


    @PostMapping("bacthUpdoad")
    @ApiOperation("多文件上传")
    public ResultBody batchFilesUpload(@RequestParam("files") MultipartFile[] files,HttpServletRequest request){


        try {
            File dir = new File("D:" + File.separatorChar + "temp" + File.separatorChar+"context");
            if (!dir.exists()){
                dir.mkdirs();
            }
            boolean flag = true;

            for (int i = 0; i < files.length; i++) {
                if (!files[i].isEmpty()){
                    MultipartFile file = files[i];
                    String filename = file.getOriginalFilename();
                    if (filename.endsWith("jpf")||filename.endsWith(".xls")|| filename.endsWith(".xlsx")){
                        System.out.println(dir.getAbsoluteFile());
                        File mbfile = new File(dir.getAbsolutePath() + File.separatorChar, filename);
                        file.transferTo(mbfile);
                    }
                }
            }
            if (flag){
                return ResultBody.error().message("没有上传文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultBody.ok().message("上传成功");

    }












}
