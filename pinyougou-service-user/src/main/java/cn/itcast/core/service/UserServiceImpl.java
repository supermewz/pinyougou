package cn.itcast.core.service;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import vo.Orderpp;

import javax.jms.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;

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
                map.setString("iphone", phone);//"17801040609"
                //验证码
                map.setString("templateParam", "{'code':'" + random + "'}");
                //签名
                map.setString("signName", "品优购");
                //模板ID
                map.setString("templateCode", "SMS_161365127");
                System.out.println("sendCode出来了");
                return map;
            }
        });
    }

    @Override
    public void add(User user, String phone) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (null == code) {
            throw new RuntimeException("验证码失败");
        }
        if (code.equals(user)) {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        } else {
            throw new RuntimeException("验证码不正确");
        }
    }

//    @Override
//    public Orderpp findAll(String name) {
//
//        Orderpp orderpp = new Orderpp();
//        List<OrderItem> orderItemList = new ArrayList<>();
//        List<Order> orderArrayList = new ArrayList<>();
//        OrderItem ArderItem = new OrderItem();
//
//        //条件对象
//        PayLogQuery query = new PayLogQuery();
//        //添加userId等于name的字段
//        PayLogQuery.Criteria criteria = query.createCriteria().andUserIdEqualTo(name);
//        //根据条件查询结果集
//        List<PayLog> payLogs = payLogDao.selectByExample(query);
//        //遍历结果集 拿出order id
//
//
//        for (PayLog payLog : payLogs) {
//            String orderList = payLog.getOrderList();
//            String[] split = orderList.split(",");
//
//            for (String s : split) {
//                //根据拿出的结果查询orderitem表
//
//
//                Order order = orderDao.selectByPrimaryKey(Long.parseLong(s.trim()));
//
//                orderArrayList.add(order);
//
//
//                OrderItemQuery query1 = new OrderItemQuery();
//
//                query1.createCriteria().andOrderIdEqualTo(order.getOrderId());
//
//                List<OrderItem> orderItemList1 = orderItemDao.selectByExample(query1);
//
//                for (OrderItem orderItem : orderItemList1) {
//                    orderItemList.add(orderItem);
//                }
//            }
//            orderpp.setOrderList(orderArrayList);
//            orderpp.setOrderItemList(orderItemList);
//        }
//        return orderpp;
//    }

    @Override
    public List<Orderpp> findAll(String name) {
        List<Orderpp> orderppList = new ArrayList<>();



        //根据用户名查询订单
        PayLogQuery query = new PayLogQuery();
        query.createCriteria().andUserIdEqualTo(name);
        //获取该用户多个订单
        List<PayLog> logList = payLogDao.selectByExample(query);

        for (PayLog payLog : logList) {
            String orderList = payLog.getOrderList();
            String[] orderList_Order_Id = orderList.split(",");
            //根据订单号查询order表
            for (String s : orderList_Order_Id) {
                Orderpp orderpp = new Orderpp();
                Order order = new Order();

                order = orderDao.selectByPrimaryKey(Long.parseLong(s.trim()));
                orderpp.setOrder(order);
                //根据order表order-id查询商品结果集
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
                List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                List<OrderItem> orderItemList = new ArrayList<>();

                for (OrderItem item : orderItemList1) {
                    orderItemList.add(item);
                }


                orderpp.setOrderitemList(orderItemList);
                orderppList.add(orderpp);
            }
        }


        return orderppList;
    }

}
