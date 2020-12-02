package com.asiainfo.config;

import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "sync-table") // 不同的配置类，其前缀不能相同
@EnableConfigurationProperties(ListConfig.class) // 必须标明这个类是允许配置的
public class ListConfig {

    private List<String> tableNameList = new ArrayList<String>();
    private List<String> rangeCol = new ArrayList<String>();

    private String cron;



    public List<String> getTableNameList() {
        return tableNameList;
    }

    public void setTableNameList(List<String> tableNameList) {
        this.tableNameList = tableNameList;
    }

    public List<String> getRangeCol() {
        return rangeCol;
    }

    public void setRangeCol(List<String> rangeCol) {
        this.rangeCol = rangeCol;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
