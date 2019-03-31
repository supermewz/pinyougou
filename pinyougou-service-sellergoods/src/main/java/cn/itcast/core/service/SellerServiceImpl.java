package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Seller seller) {
        seller.setStatus("0");
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Seller seller, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();
        if(null!=seller.getName() && !"".equals(seller.getName().trim())){
            criteria.andNameLike("%"+seller.getName().trim()+"%");
        }
        if(null!=seller.getNickName() && !"".equals(seller.getNickName())){
            criteria.andNickNameLike("%"+seller.getNickName().trim()+"%");
        }
        Page<Seller> sellers = (Page<Seller>) sellerDao.selectByExample(sellerQuery);


        return new PageResult(sellers.getTotal(),sellers.getResult());
    }

    @Override
    public Seller findOne(String sellerId){
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public Seller findSellerByUsername(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
}
