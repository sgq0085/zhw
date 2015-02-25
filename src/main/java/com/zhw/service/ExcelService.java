package com.zhw.service;

import com.zhw.utils.Files;
import com.zhw.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "zhw-excel";

    protected static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/organizations.properties"
            , "classpath:/product.properties");

    public String process(String dataDate, List<File> files) {
        File f1 = new File("C:\\Users\\Administrator\\Desktop\\IntelliJ 快捷键.txt");
        File f2 = new File("C:\\Users\\Administrator\\Desktop\\info.txt");
        File zip = null;
        String id = null;

        try {
            id = UUID.randomUUID().toString();
            File dir = new File(TEMP_DIR + File.separator + id);
            dir.mkdirs();
            File all = new File(dir.getParent() + File.separator + "文件一");
            zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", f1, f2);
        } catch (Exception e) {
            logger.warn("解析失败", e);
            id = null;
        }
        return id;
    }

    public static void main(String[] args) {
        String baseUrl = propertiesLoader.getProperty("baseUrl");
    }

}
