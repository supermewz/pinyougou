package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
	@Reference
	private ContentService contentService;

	@RequestMapping("/findByCategoryId")
	public List<Content> findByCategoryId(Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
}
