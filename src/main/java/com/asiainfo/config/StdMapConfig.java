package com.asiainfo.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "column-mapping-std")
@EnableConfigurationProperties(StdMapConfig.class)
public class StdMapConfig {
    /**
     * 从配置文件中读取的limitSizeMap开头的数据
     * 注意：名称必须与配置文件中保持一致
     */
    private Map<String, String> columnMappingMap = new HashMap<String, String>();

    private Map<String, String> defaultValueMap = new HashMap<String, String>();


    public Map<String, String> getColumnMappingMap() {
        return columnMappingMap;
    }

    public void setColumnMappingMap(Map<String, String> columnMappingMap) {
        this.columnMappingMap = columnMappingMap;
    }

    public Map<String, String> getDefaultValueMap() {
        return defaultValueMap;
    }

    public void setDefaultValueMap(Map<String, String> defaultValueMap) {
        this.defaultValueMap = defaultValueMap;
    }
}
