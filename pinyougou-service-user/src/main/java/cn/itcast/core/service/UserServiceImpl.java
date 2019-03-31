package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {
        System.out.println("进来sendCode");
        redisTemplate.boundValueOps("iphone").set(phone);
        final String random = RandomStringUtils.randomNumeric(6);
//      redisTemplate.boundValueOps(phone).expire(1,TimeUnit.MINUTES );
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage map = session.createMapMessage();
                //手机号
                map.setString("iphone",phone);//"17801040609"
                //验证码
                map.setString("templateParam","{'code':'"+random+"'}");
                //签名
                map.setString("signName","品优购");
                //模板ID
                map.setString("templateCode","SMS_161365127");
                System.out.println("sendCode出来了");
                return map;
            }
        });
    }

    @Override
    public void add(User user, String phone) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if(null==code){
            throw new RuntimeException("验证码失败");
        }
        if(code.equals(user)){
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else {
            throw new RuntimeException("验证码不正确");
        }
    }


}
