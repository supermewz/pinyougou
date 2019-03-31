package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);

        //PageInfo<Brand> pageInfo = new PageInfo(brandList);

        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {

        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        //条件对象
        BrandQuery brandQuery = new BrandQuery();
        //创建内部条件对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //判断名称是否有值
        if(null != brand.getName() && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        //判断首字母
        if(null != brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }

        //查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        //总条数
        //结果集 select * from tb_brand  limit 开始行,每页数
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {

        return brandDao.selectOptionList();
    }


}
