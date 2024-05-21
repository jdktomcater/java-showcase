package com.jdktomcat.showcase.fastdfs.controller;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * upload file controller
 */
@Controller
@Slf4j
public class UploadFileController {

    public static final String UPLOAD_PATH = "https://txfile-gofast.fbnma.com/group/upload";

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String  upload(MultipartFile file) {
        String result = "";
        try {
            InputStreamResource isr = new InputStreamResource(file.getInputStream(), file.getOriginalFilename());
            Map<String, Object> params = new HashMap<>();
            params.put("file", isr);
            params.put("path", "/timmy/");
            params.put("output", "json");
            String resp = HttpUtil.post(UPLOAD_PATH, params);
            log.info("resp: {}", resp);
            result = resp;
        } catch (IOException e) {
            log.error("upload file error", e);
        }
        return result;
    }

}
