package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/showName")
    public Map<String,Object> showName(HttpServletRequest request){
        SecurityContext spring_cecurity_context = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        User user = (User) spring_cecurity_context.getAuthentication().getPrincipal();
        String username = user.getUsername();
        Map<String, Object> map = new HashMap<>();
        map.put("username",username );
        map.put("curTime", new Date());
        System.out.println(map.toString());
        return map;
    }
}
