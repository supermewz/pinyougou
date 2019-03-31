package cn.itcast.core.controller;


import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference
    private AddressService addressService;

    @RequestMapping("/findListByLoginUser")
    public  List<Address> findListByLoginUser (){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return  addressService.findListByLoginUser(name);
    }
}
