package cn.itcast.core.controller;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;


    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        System.out.println("controller进来了");
        if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            try {
                userService.sendCode(phone);
                return new Result(true, "发送成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "发送失败");
            }
        } else {
            return new Result(false, "手机号不合法");
        }
    }

    //注册
    @RequestMapping("/add")
    public Result add(@RequestBody User user,String phone) {
        try {
            userService.add(user,phone);
            return new Result(true, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }

    }
}
