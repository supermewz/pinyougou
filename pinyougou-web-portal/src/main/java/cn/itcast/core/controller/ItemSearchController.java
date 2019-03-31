package cn.itcast.core.controller;

import cn.itcast.core.service.ItemsearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private ItemsearchService itemsearchService;

    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String,String> searchMap ){
        return  itemsearchService.search(searchMap);
    }
}
