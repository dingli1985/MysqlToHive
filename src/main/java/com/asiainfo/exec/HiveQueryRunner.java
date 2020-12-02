package com.asiainfo.exec;

import com.asiainfo.config.StdMapConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;


@Component
public class HiveQueryRunner implements CommandLineRunner {

    private Log logger = LogFactory.getLog(HiveQueryRunner.class);

    @Resource
    JdbcTemplate hiveJdbcTemplate;

    @Resource
    private Environment env;

    @Resource
    private StdMapConfig stdMapConfig;




    @Override
    public void run(String... args) throws Exception {
        String startTime = env.getProperty("sync-table.start");
        String endTime = env.getProperty("sync-table.end");
        String type = env.getProperty("sync-table.range-type");
        String interval = env.getProperty("sync-table.range-interval");
        String cron = env.getProperty("sync-table.cron");
        String syncTableSwitch=env.getProperty("sync-table.switch");
        String pageQuerySwitch=env.getProperty("page-query.switch");
        String csvFileExt=env.getProperty("csv-file-suffix");
        String sftpSwitch = env.getProperty("sftp-switch");
        String generateDayCsvFile = env.getProperty("csv-day-generate-switch");
        String targetStdTableFilter=env.getProperty("target.table.filter.std");
        String targetOriTableFilter=env.getProperty("target.table.filter.std");
        String rangeType = env.getProperty("sync-table.range-type");
        String rangeInterval = env.getProperty("sync-table.range-interval");

        logger.info("sync-table.start=====" + startTime);
        logger.info("sync-table.end=====" + endTime);
        logger.info("sync-table.range-type=====" + type);
        logger.info("sync-table.range-interval=====" + interval);
        logger.info("sync-table.cron=====" + cron);
        logger.info("sync-table.switch=====" + syncTableSwitch);
        logger.info("sftpSwitch=====" + sftpSwitch);
        logger.info("generateDayCsvFile=====" + generateDayCsvFile);
        logger.info("page-query.switch=====" + pageQuerySwitch);
        logger.info("csvFileExt=====" + csvFileExt);
        logger.info("targetStdTableFilter=====" + targetStdTableFilter);
        logger.info("targetOriTableFilter=====" + targetOriTableFilter);
        logger.info("rangeType=====" + rangeType);
        logger.info("rangeInterval=====" + rangeInterval);

        Map<String,String> map=stdMapConfig.getDefaultValueMap();
        for(Map.Entry<String,String> entry : map.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            logger.info("mapKey : "+mapKey+" type : "+mapValue.getClass()+"value : "+mapValue);
        }




//        RestTemplate restTemplate=restTemplateBuilder.build();

//        int count=hiveJdbcTemplate.queryForObject("select count(1) from hn_dev_session",Integer.class);
//        logger.info("hn_dev_session   count===="+count );



    }
}
