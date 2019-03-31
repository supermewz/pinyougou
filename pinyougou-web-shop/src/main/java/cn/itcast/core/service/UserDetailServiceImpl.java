package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 安全框架 加载数据库中用户信息的实现类
 * 授权 类
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService{

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    private SellerService sellerService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller seller = sellerService.findSellerByUsername(username);
        if(null!=seller){
            if("1".equals(seller.getStatus())){
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                return new User(seller.getSellerId(),seller.getPassword(),authorities);
            }
        }
        return null;
    }
}
