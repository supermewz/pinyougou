package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        for (ItemCat itemCat : itemCats) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
        }

        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public ItemCat findOne(Long id) {

        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
