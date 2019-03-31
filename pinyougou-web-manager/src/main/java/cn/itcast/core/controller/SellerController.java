package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.PageRanges;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/add")
    public Result add(Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller,Integer page,Integer rows){
        return sellerService.search(seller,page,rows);
    }

    @RequestMapping("/findOne")
    public Seller findOne(String id){
        System.out.println("conlloter");
        System.out.println(sellerService.findOne(id).toString());
        return sellerService.findOne(id);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

}
