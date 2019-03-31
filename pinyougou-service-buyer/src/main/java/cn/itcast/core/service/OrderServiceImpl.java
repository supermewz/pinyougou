package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private PayLogDao payLogDao;

    @Override
    public void add(Order order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(order.getUserId());
        long payLogTotal = 0;
        ArrayList<Long> ids = new ArrayList<>();
        for (Cart cart : cartList) {
            double total= 0;

            long id = idWorker.nextId();
            ids.add(id);
            order.setOrderId(id);
            //实付金额
            
            
            // 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            order.setPostFee("1");

            //  订单创建时间
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());

            //订单来源
            order.setSourceType("2");
            
            //商家ID
            order.setSellerId(cart.getSellerId());

            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                //订单详情表ID
                long orderItemId =  idWorker.nextId();
                orderItem.setId(orderItemId);
                //商品ID
                orderItem.setGoodsId(item.getGoodsId());

                //订单ID
                orderItem.setOrderId(id);

                //标题
                orderItem.setTitle(item.getTitle());
                //价格
                orderItem.setPrice(item.getPrice());
                //总金额  小计
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //追加总金额
                total+=orderItem.getTotalFee().doubleValue();
                //图片
                orderItem.setPicPath(item.getImage());
                //商家ID
                orderItem.setSellerId(item.getSellerId());

                //保存
                orderItemDao.insertSelective(orderItem);
            }
            order.setPayment(new BigDecimal(total));
            payLogTotal+=order.getPayment().longValue();

            orderDao.insertSelective(order);
        }


        PayLog payLog = new PayLog();
        payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
        payLog.setCreateTime(new Date());
        payLog.setTotalFee(payLogTotal*100);
        payLog.setUserId(order.getUserId());
        payLog.setTradeState("0");
        payLog.setOrderList(ids.toString().replace("[","" ).replace("]","" ));
        payLog.setPayType("1");
        payLogDao.insertSelective(payLog);

        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog );

//        redisTemplate.boundHashOps("cart").delete(order.getUserId());
    }
}
