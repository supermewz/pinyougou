package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public Map<String,Object> showName(HttpServletRequest request) throws UnsupportedEncodingException {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> map = new HashMap<>();
        name = new String(name.getBytes("GBK"), "UTF-8");
        map.put("loginName",name );
        System.out.println(map.get("loginName"));
        //map.put("curTime", new Date());
        return map;
    }
}
