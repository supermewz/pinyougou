package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.Cart;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CatController {

    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9103")


    public Result addToCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        try {
            List<Cart> cartList = null;
            //1:获取Cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if ("CART".equals(cookie.getName())) {
                        //2：获取Cookie中购物车
                        String decode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                        //购物车对象  Cookie只能保存String类型 不能保存对象 将对象转成JSon格式字符串 取出串换回对象
                        cartList = JSON.parseArray(decode, Cart.class);
                    }
                }
            }
            //3:没有 创建购物车
            if (null == cartList) {
                cartList = new ArrayList<>();
            }
            //          4：追加当前款
            Cart newCart = new Cart();
            Item item = cartService.findItemById(itemId);
            //商家Id
            newCart.setSellerId(item.getSellerId());
            //商家名称 不写 浪费
            //商家里商品结果集
            OrderItem newOrderItem = new OrderItem();
            //库存ID
            newOrderItem.setItemId(itemId);
            //数量
            newOrderItem.setNum(num);
            //创建集合
            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            newCart.setOrderItemList(newOrderItemList);


            //1)判断当前款商品的商家  是否在购物车集合中 众多商家中已存在
            int newIndexOf = cartList.indexOf(newCart);//indexOf  -1 不存在  >=0 存在同时 存在的角标位置
            if (newIndexOf != -1) {
                //--1:存在
                //2)判断当前款商品在此商家下众多商品中已存在
                Cart oldCart = cartList.get(newIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int indexOf = oldOrderItemList.indexOf(newOrderItem);
                //--1:存在  追加商品的数量
                if (indexOf != -1) {
                    OrderItem orderItem = oldOrderItemList.get(indexOf);
                    orderItem.setNum(orderItem.getNum() + newOrderItem.getNum());
                } else {
                    //--2：不存在 新建一个商品并放到此商家下
                    oldOrderItemList.add(newOrderItem);
                }

            } else {
                //--2:不存在  直接创建新的购物车（因为一个购物车对应一个商家，并在此商家下创建新商品）
                cartList.add(newCart);
            }

            String name = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!"anonymousUser".equals(name)) {

                    cartService.addCartListToRedis(cartList, name);
                    String s = JSON.toJSONString(cartList);
                    String trim = s.trim();
                    String encode = URLEncoder.encode(trim, "UTF-8");
                    Cookie cookie = new Cookie("CART", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);

            } else {
                //未登陆
//           5:创建Cookie 保存购物车到Cookie 回写Cookie到浏览器
                String s = JSON.toJSONString(cartList);
                String trim = s.trim();
                String encode = URLEncoder.encode(trim, "UTF-8");
                Cookie cookie = new Cookie("CART", encode);
                cookie.setMaxAge(60 * 60 * 24);
                cookie.setPath("/");
                response.addCookie(cookie);
            }


//                获取缓存中数据 合并之前 再保存到缓存中替换到之前缓存中的数据
//                        清空Cookie


            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }

    }


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        List<Cart> cartList = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("CART".equals(cookie.getName())) {
                    String decode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    cartList = JSON.parseArray(decode, Cart.class);
                }
            }
        }

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)) {
            if (cartList != null) {
                cartService.addCartListToRedis(cartList, name);
                //清空cookie
                Cookie cookie = new Cookie("CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            cartList = cartService.findCartListFromRedis(name);
        }


        if (cartList != null) {
            cartList = cartService.findCartList(cartList);
        }
        return cartList;
    }
}
