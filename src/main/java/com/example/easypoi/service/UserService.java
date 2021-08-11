package com.example.easypoi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.easypoi.pojo.User;
import com.example.easypoi.vo.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService extends IService<User> {


    List<User> selectUser();

    List<User> findByUsername(String userName);

    void exportUser(HttpServletResponse response);

    void exportSheets(HttpServletResponse response);

    void importExcel(List<User> users);

    ResultBody importSheet(MultipartFile file,HttpServletResponse response) throws Exception;
}
