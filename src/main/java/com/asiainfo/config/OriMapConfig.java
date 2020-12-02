package com.asiainfo.config;
/**
 * 配置类
 * 从配置文件中读取数据映射到map
 * 注意：必须实现set方法
 * @author eknows
 * @version 1.0
 * @since 2019/2/13 9:23
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "column-mapping-ori")
@EnableConfigurationProperties(OriMapConfig.class)
public class OriMapConfig {

    /**
     * 从配置文件中读取的limitSizeMap开头的数据
     * 注意：名称必须与配置文件中保持一致
     */
    private Map<String, String> columnMappingMap = new HashMap<String, String> ();

    public Map<String, String> getColumnMappingMap() {
        return columnMappingMap;
    }

    public void setColumnMappingMap(Map<String, String> columnMappingMap) {
        this.columnMappingMap = columnMappingMap;
    }
}
