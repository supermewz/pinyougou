package cn.itcast.core.service;


import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.opensaml.xml.signature.Q;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware{

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public void index(Long id){
        Configuration conf = freeMarkerConfigurer.getConfiguration();
        String path = getpath("/"+id+".html");
        Writer out = null;
        try {
            Template template = conf.getTemplate("item.ftl");
            Map<String, Object> root = new HashMap<>();
            out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");

            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(id);
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            root.put("itemList", itemList);


            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc", goodsDesc);

            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("goods",goods );

            root.put("itemCat1",itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            root.put("itemCat2",itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            root.put("itemCat3",itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());

            template.process(root,out );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServletContext servletContext;

    public String getpath(String path){
        return servletContext.getRealPath(path);
    }
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}

