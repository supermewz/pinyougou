package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

public interface SellerService {

    void add(Seller seller);

    PageResult search(Seller seller, Integer page, Integer rows);

    Seller findOne(String  sellerId);

    void updateStatus(String sellerId, String status);

    Seller findSellerByUsername(String username);
}
