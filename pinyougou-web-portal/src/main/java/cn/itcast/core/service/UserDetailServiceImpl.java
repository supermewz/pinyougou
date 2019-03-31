package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;

public class UserDetailServiceImpl implements UserDetailsService{
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorities);
    }
}
