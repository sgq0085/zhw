package com.zhw.service;

import com.zhw.utils.PropertiesLoader;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    protected static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/organizations.properties"
            , "classpath:/product.properties");

    public static void main(String[] args) {
        String baseUrl = propertiesLoader.getProperty("baseUrl");
    }

}
