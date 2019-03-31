package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {


        SpecificationQuery specificationQuery  = new SpecificationQuery();
        if(null!=specification.getSpecName()){
            specificationQuery.createCriteria().andSpecNameLike("%"+specification.getSpecName()+"%");
        }
        PageHelper.startPage(page,rows);
        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void add(SpecificationVo vo) {
        specificationDao.insertSelective(vo.getSpecification());


        //规格选项表
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo vo = new SpecificationVo();
        vo.setSpecification(specificationDao.selectByPrimaryKey(id));
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        vo.setSpecificationOptionList(specificationOptionDao.selectByExample(query));
        return vo;
    }

    //修改
    @Override
    public void update(SpecificationVo vo) {
        specificationDao.updateByPrimaryKeySelective(vo.getSpecification());
        //删除
        SpecificationOptionQuery query = new SpecificationOptionQuery();

        query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);
        //添加
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
