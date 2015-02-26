package com.zhw.controller;

import com.google.common.collect.Maps;
import com.zhw.service.ExcelService;
import com.zhw.utils.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/excel")
@SuppressWarnings("unchecked")
public class ExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);

    @Autowired
    private ExcelService excelService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> upload(HttpServletRequest request) {
        Map<String, Object> res = Maps.newHashMap();
        res.put("success", true);

        Map<String, Object> multipartFormData = Files.enctypeEqualsMultipartFormData(request, null);
        Map<String, String> params = (Map<String, String>) multipartFormData.get("params");
        String recordDay = params.get("recordDay");
        String combineToExcel = params.get("combineToExcel");
        String processResult = params.get("processResult");
        List<File> files = (List<File>) multipartFormData.get("files");
        if (StringUtils.isBlank(recordDay) || CollectionUtils.isEmpty(files)) {
            res.put("success", false);
            res.put("msg", "没有选择日期或源文件");
            return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
        }
        logger.info("启动任务 记账日期{},文件数{}", recordDay, files.size());

        String id = excelService.process(recordDay, StringUtils.isNoneBlank(combineToExcel), StringUtils.isNoneBlank(processResult), files);
        if (StringUtils.isBlank(id)) {
            return new ResponseEntity<Map<String, Object>>(res, HttpStatus.FORBIDDEN);
        }
        res.put("id", id);
        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response, String id) {
        File file = new File(excelService.TEMP_DIR + File.separator + id + File.separator + "结果.zip");
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            Files.download(request, response, file, null, false);
        }
    }

    @RequestMapping(value = "/flush", method = RequestMethod.POST)
    @ResponseBody
    public long flush(HttpServletRequest request, HttpServletResponse response, String id) {
        return excelService.flushTemp();
    }
}
