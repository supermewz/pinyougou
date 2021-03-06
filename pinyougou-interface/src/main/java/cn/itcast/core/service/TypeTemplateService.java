package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(TypeTemplate typeTemplate, Integer page, Integer rows);

    TypeTemplate findOne(Long id);

    void add(TypeTemplate typeTemplate);

    void update(TypeTemplate typeTemplate);


    void delete(Long[] ids);

    List<Map> findBySpecList(Long id);
}
