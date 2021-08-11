package com.example.easypoi.interceptor;

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import com.example.easypoi.pojo.User;
import com.example.easypoi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义验证
 */
@Component
public class UserExcelVerifyHandler implements IExcelVerifyHandler<User> {

    @Autowired
    private UserService userService;
    /**
     *
     * ExcelVerifyHandlerResult
     *   suceess :代表验证成功还是失败(如果用户名重复，就代表失败)
     *   msg:失败的原因
     */
    @Override
    public ExcelVerifyHandlerResult verifyHandler(User user) {

        ExcelVerifyHandlerResult result = new ExcelVerifyHandlerResult(true);
        //如果根据用户名获取到用户，代表这个用户已经存在
        List<User> tempEmp = userService.findByUsername(user.getUserName());
        if(tempEmp!=null){
            result.setSuccess(false);
            result.setMsg("用户名重复");
        }
        return  result;
    }
}
