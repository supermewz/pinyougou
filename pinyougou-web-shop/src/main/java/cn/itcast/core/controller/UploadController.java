package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传图片管理
 */

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String fsu;

    @RequestMapping("/uploadFile")
    public Result upload(MultipartFile file) throws Exception {
        //取文件的完整名
        try {
            String fileName = file.getOriginalFilename();
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String path = fastDFSClient.uploadFile(file.getBytes(),ext,null);

            return new Result(true,fsu+path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }


    }
}
