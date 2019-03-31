package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(TypeTemplate typeTemplate, Integer page, Integer rows) {
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate template : typeTemplates) {
            List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList );

            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(), specList);


        }



        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if(null!=typeTemplate.getName()){
            typeTemplateQuery.createCriteria().andNameLike("%"+typeTemplate.getName()+"%");
        }
        PageHelper.startPage(page,rows);
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public TypeTemplate findOne(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        return typeTemplate;
    }

    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> listmap = JSON.parseArray(specIds, Map.class);
        for (Map map : listmap) {
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));

            map.put("options", specificationOptionDao.selectByExample(query));
        }
        return listmap;
    }


}
