package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;
import vo.Orderpp;

import java.util.List;

public interface UserService {
    void sendCode(String phone);


    void add(User user, String phone);


    List<Orderpp> findAll(String name);
}
