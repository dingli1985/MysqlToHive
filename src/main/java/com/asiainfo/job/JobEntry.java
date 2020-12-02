package com.asiainfo.job;

import com.asiainfo.config.ListConfig;
import com.asiainfo.config.OriMapConfig;
import com.asiainfo.config.SftpInfo;
import com.asiainfo.config.StdMapConfig;
import com.asiainfo.util.SftpUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JobEntry {


    private Log logger = LogFactory.getLog(JobEntry.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Resource
    private JdbcTemplate mysqlJdbcTemplate;

    @Resource
    private JdbcTemplate oracleJdbcTemplate;

    @Resource
    private ListConfig listConfig;

    @Resource
    private StdMapConfig stdMapConfig;

    @Resource
    private OriMapConfig oriMapConfig;

    @Resource
    private SftpInfo sftpInfo;

    @Resource
    private Environment env;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat ymFormat = new SimpleDateFormat("yyyyMM");

    SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyyMMdd");

    SimpleDateFormat ymdHourFormat = new SimpleDateFormat("yyyyMMddHH");
    SimpleDateFormat ymdHourMinFormat = new SimpleDateFormat("yyyyMMddHHmm");



    public void sftp(String host, String user, String pwd, String privateKey, String passphrase, String ftpRootDir, String uploadFile, int port)
            throws JSchException {
        SftpUtils mySftp = new SftpUtils(host, user, pwd, privateKey, passphrase, port);

        ChannelSftp sftp = mySftp.connectSFTP();
        mySftp.upload(ftpRootDir, uploadFile, sftp);
        mySftp.disconnected(sftp);
    }


    //@Scheduled(cron = "${sync-table.cron}")
    public void execute() {
        Date now = new Date();

        List<String> tableNameList = listConfig.getTableNameList();
        List<String> rangCol = listConfig.getRangeCol();

        String targetTablePrefixStd = env.getProperty("target.table.prefix.std");
        String targetTablePrefixOri = env.getProperty("target.table.prefix.ori");
        String targetTableSuffix = ymFormat.format(now);
        String targetTableRangeColStd = env.getProperty("target.table.rangeCol.std");
        String targetTableRangeColOri = env.getProperty("target.table.rangeCol.ori");
        String targetStdTable=targetTablePrefixStd + targetTableSuffix;
        String targetOriTable=targetTablePrefixOri + targetTableSuffix;
        String targetStdTableFilter=env.getProperty("target.table.filter.std");
        String targetOriTableFilter=env.getProperty("target.table.filter.std");

        logger.info("targetTablePrefixOri=====" + targetTablePrefixOri);

        logger.info("targetTableSuffix=====" + targetTableSuffix);


        SqlRowSet sqlRowSet = oracleJdbcTemplate.queryForRowSet("select * from " + targetStdTable+ " where 1=0");
        SqlRowSetMetaData sqlRsmd = sqlRowSet.getMetaData();
        int columnCount = sqlRsmd.getColumnCount();
        StringBuffer selCol = new StringBuffer();
        StringBuffer questionMark = new StringBuffer();
        String[] header = new String[columnCount];
        for (int j = 1; j <= columnCount; j++) {
            logger.info(sqlRsmd.getColumnName(j)+"=====" + sqlRsmd.getColumnTypeName(j));
            header[j - 1] = sqlRsmd.getColumnName(j);
            selCol.append(sqlRsmd.getColumnName(j) + ",");
            questionMark.append("?,");

        }
        selCol.setLength(selCol.length() - 1);
        questionMark.setLength(questionMark.length() - 1);

        Date end = DateUtils.truncate(now, Calendar.DATE);
        Date begin = DateUtils.addDays(end, -1);
        Date tempDate = null;
        Map stdColumnMappingMap = stdMapConfig.getColumnMappingMap();
        Map defaultValueMap = stdMapConfig.getDefaultValueMap();


        String cron = listConfig.getCron();
        logger.info("Cron Expression   " + cron);

        String rangeType = env.getProperty("sync-table.range-type");
        String rangeInterval = env.getProperty("sync-table.range-interval");


        logger.info("rangeType=====" + rangeType);
        logger.info("rangeInterval=====" + rangeInterval);


        int interval=0;
        try {
            interval = Integer.valueOf(rangeInterval);
        }catch(NumberFormatException e){
            logger.info("Parse rangeInterval Error=====" + e.getMessage());

        }


        String startTime = env.getProperty("sync-table.start");
        String endTime = env.getProperty("sync-table.end");

        String syncMysqlSwitch=env.getProperty("sync-table.switch");
        logger.info("syncMysqlSwitch=====" + syncMysqlSwitch);

        if (!"".equals(startTime) && !"".equals(endTime)) {
            try {
                begin = DateUtils.parseDate(startTime, "yyyyMMddHHmmss".intern());
                end = DateUtils.parseDate(endTime, "yyyyMMddHHmmss".intern());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if ("".equals(startTime) && !"".equals(endTime)) {
            try {
                end = DateUtils.parseDate(endTime, "yyyyMMddHHmmss".intern());
            } catch (ParseException e) {
                logger.info("Parse endTime Error=====" + e.getMessage());

                e.printStackTrace();
            }
            if ("day".equals(rangeType)) {
                begin = DateUtils.truncate(now, Calendar.DATE);
            }
            if ("hour".equals(rangeType)) {
                begin = DateUtils.truncate(now, Calendar.HOUR);
            }
            if ("min".equals(rangeType)) {
                begin = DateUtils.truncate(now, Calendar.MINUTE);
            }

        } else if (!"".equals(startTime) && "".equals(endTime)) {
            try {
                begin = DateUtils.parseDate(startTime, "yyyyMMddHHmmss".intern());
            } catch (ParseException e) {
                logger.info("Parse startTime Error=====" + e.getMessage());
                e.printStackTrace();
            }
            if ("day".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.DATE);
            }
            if ("hour".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.HOUR);
            }
            if ("min".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.MINUTE);
            }
        } else {
            if ("day".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.DATE);
                begin = DateUtils.addDays(end, interval);
            }
            if ("hour".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.HOUR);
                begin = DateUtils.addHours(end, interval);
            }
            if ("min".equals(rangeType)) {
                end = DateUtils.truncate(now, Calendar.MINUTE);
                begin = DateUtils.addMinutes(end, interval);
            }
        }

        String oriInsertSql = "",stdDatalistSql="",deleteStdSql="",deleteOriSql="";

        logger.info("GetData  Range from =====" + simpleDateFormat.format(begin) + "=======to ======" + simpleDateFormat.format(end));
        String csvFileName="";
        List csvDataList=null;
        String csvFilePath=env.getProperty("csv-path");
        String sftpSwitch = env.getProperty("sftp-switch");
        String csvDataFilter=env.getProperty("csv-data-filter");
        //文件扩展
        String csvFileExt=env.getProperty("csv-file-ext");
        while (begin.before(end)) {
            if ("day".equals(rangeType)) {
                tempDate = DateUtils.addDays(begin, -interval);
            }
            if ("hour".equals(rangeType)) {
                tempDate = DateUtils.addHours(begin, -interval);
            }
            if ("min".equals(rangeType)) {
                tempDate = DateUtils.addMinutes(begin, -interval);
            }
            targetTableSuffix=ymFormat.format(begin);
            targetStdTable=targetTablePrefixStd + targetTableSuffix;
            targetOriTable=targetTablePrefixOri + targetTableSuffix;
            deleteStdSql = "delete " + targetStdTable + " where " + targetTableRangeColStd + " >? and " + targetTableRangeColStd + " <? and "+targetStdTableFilter;
            deleteOriSql = "delete " + targetOriTable + " where " + targetTableRangeColOri + " >? and " + targetTableRangeColOri + " <? and "+targetOriTableFilter;
            //String updateStdSql = "update " + targetStdTable + " set device_type= ? where " + targetTableRangeColStd + " >? and " + targetTableRangeColStd + " <? and "+targetStdTableFilter;;

            if("true".equals( syncMysqlSwitch)||"on".equals(syncMysqlSwitch)) {
                String insertStdSql = "INSERT INTO " + targetStdTable + "(" + selCol.toString() + ") values (" + questionMark.toString() + ")";
                logger.info("insertStdSql=====" + insertStdSql);
                importData(begin, tempDate, deleteStdSql, insertStdSql, tableNameList, rangCol, columnCount, stdColumnMappingMap,defaultValueMap, sqlRsmd, targetStdTable);
                //logger.info("updateStdSql   =====" + updateStdSql);
                //oracleJdbcTemplate.update(updateStdSql,"1",begin, end);
            }
            if ("day".equals(rangeType)) {
                csvFileName = targetTablePrefixStd +(csvFileExt==null?"":csvFileExt)+ ymdFormat.format(begin);
            }
            if ("hour".equals(rangeType)) {
                csvFileName = targetTablePrefixStd +(csvFileExt==null?"":csvFileExt)+ ymdHourFormat.format(begin);
            }
            if ("min".equals(rangeType)) {
                csvFileName = targetTablePrefixStd +(csvFileExt==null?"":csvFileExt)+ ymdHourMinFormat.format(begin);
            }



            oriInsertSql = "insert into " + targetOriTable +
                    "  select * from " + targetStdTable +
                    " where " + targetTableRangeColStd + ">= to_date('" + simpleDateFormat.format(begin) + "','yyyy-mm-dd hh24:mi:ss') " +
                    " and " + targetTableRangeColStd + " < to_date('" + simpleDateFormat.format(tempDate) + "','yyyy-mm-dd hh24:mi:ss')  ";

            stdDatalistSql = "select * from " + targetStdTable +
                    " where " + targetTableRangeColStd + ">= to_date('" + simpleDateFormat.format(begin) + "','yyyy-mm-dd hh24:mi:ss') " +
                    " and " + targetTableRangeColStd + " < to_date('" + simpleDateFormat.format(tempDate) + "','yyyy-mm-dd hh24:mi:ss') and "+csvDataFilter;
            logger.info("deleteOriSql=====" + deleteOriSql);
            oracleJdbcTemplate.update(deleteOriSql, begin, tempDate);
            logger.info("oriInsertSql=====" + oriInsertSql);
            try {
                oracleJdbcTemplate.execute(oriInsertSql);
            } catch (Exception e) {
                logger.warn("Import Data to [" + targetOriTable + "] Exception " + e.getMessage());
                e.printStackTrace();

            }
            String pageQuerySwitch=env.getProperty("page-query.switch");
            csvFilePath = env.getProperty("csv-path");
            logger.info("stdDatalistSql ["+stdDatalistSql+"]");

            if("true".equals(pageQuerySwitch)||"on".equals(pageQuerySwitch)){
                createCsvFileByPage(selCol.toString(),targetStdTable,targetTableRangeColStd,begin,tempDate,csvDataFilter,header,csvFileName,csvFilePath);
            }else{
                csvDataList = oracleJdbcTemplate.queryForList(stdDatalistSql);
                createCsvFile(csvDataList,header,csvFileName,csvFilePath);
            }
            logger.info("sftpSwitch [ "+ sftpSwitch+" ]");
            if("true".equals( sftpSwitch)||"on".equals(sftpSwitch)){
                logger.info("Start Send File "+csvFileName+" To HiveNode " + sftpInfo.getHost());
                sendFile(csvFileName,csvFilePath);
            }
            begin = tempDate;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String generateDayCsvFile = env.getProperty("csv-day-generate-switch");
        logger.info("generateDayCsvFile switch [ "+generateDayCsvFile+" ]");
        if("true".equals( generateDayCsvFile)||"on".equals(generateDayCsvFile)){
            logger.info("Start Generate Day File ");

            end = DateUtils.truncate(now, Calendar.DATE);
            begin = DateUtils.addDays(end, interval);
            targetStdTable=targetTablePrefixStd + ymFormat.format(begin);

            stdDatalistSql = "select * from " + targetStdTable +
                    " where " + targetTableRangeColStd + ">= to_date('" + simpleDateFormat.format(begin) + "','yyyy-mm-dd hh24:mi:ss') " +
                    " and " + targetTableRangeColStd + " < to_date('" + simpleDateFormat.format(end) + "','yyyy-mm-dd hh24:mi:ss')  and "+csvDataFilter;
            logger.info("stdDatalistSql ["+stdDatalistSql+"]");

            csvDataList = oracleJdbcTemplate.queryForList(stdDatalistSql);
            csvFileName = targetTablePrefixStd + ymdFormat.format(begin);

            createCsvFile(csvDataList,header,csvFileName,csvFilePath);
            if("true".equals(sftpSwitch)){
                sendFile(csvFileName,csvFilePath);
            }

        }

    }

    public void createCsvFile(List csvDataList,String[] header,String csvFileName,String csvFilePath){
        int csvDataSize = csvDataList.size();
        logger.info("Start Generate Csv File " + csvFilePath + File.separator + csvFileName);

        ICsvMapWriter writer = null;
        try {
            writer = new CsvMapWriter(new FileWriter(csvFilePath + File.separator + csvFileName), CsvPreference.STANDARD_PREFERENCE);
            // set up some data to write
            LinkedCaseInsensitiveMap data = null;
            writer.writeHeader(header);
            for (int i = 0; i < csvDataSize; i++) {
                data = (LinkedCaseInsensitiveMap) csvDataList.get(i);
                writer.write(data, header);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info(" Generate Csv File  Over");
    }


    public void createCsvFileByPage(String selCol,String targetStdTable,String targetTableRangeColStd,Date begin,Date end,String csvDataFilter,String[] header,String csvFileName,String csvFilePath ){
        String pageSql="select "+selCol+" from " +
                "( select rownum as r,a.* from "+targetStdTable+" a " +
                "where "+targetTableRangeColStd+">=? and "+targetTableRangeColStd+" <? and "+csvDataFilter+ ") " +
                "where r>=? and r<? ";
        String pageSizeStr=env.getProperty("page-query.size");
        logger.info("pageSql=====" + pageSql);

        int i=0;
        int pageSize=0;
        try {
            pageSize = Integer.parseInt(pageSizeStr);
        }catch (Exception e){
            logger.info("parse PageSize Error=====" + e.getMessage());

        }
        boolean flag=true;
        List  csvPageDataList=null;
        int pageStart=0,pageEnd=0,csvDataSize=0;
        ICsvMapWriter writer = null;
        try {
            writer = new CsvMapWriter(new FileWriter(csvFilePath + File.separator + csvFileName), CsvPreference.STANDARD_PREFERENCE);
            writer.writeHeader(header);
        }catch (IOException e) {
            logger.info("Generate csv Error=====" + e.getMessage());
            e.printStackTrace();
        }
        LinkedCaseInsensitiveMap data = null;
        while(flag){
            pageStart=i*pageSize;
            pageEnd=(i+1)*pageSize;
            csvPageDataList=oracleJdbcTemplate.queryForList(pageSql,begin,end,pageStart,pageEnd);
            csvDataSize=csvPageDataList.size();
            try {
                // set up some data to write
                for (int j = 0; j < csvDataSize; j++) {
                    data = (LinkedCaseInsensitiveMap) csvPageDataList.get(i);
                    writer.write(data, header);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(csvPageDataList.size()==0){
                flag=false;
            }
            i++;
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void importData(Date begin, Date end, String deleteStdSql, String insertStdSql, List<String> tableNameList, List<String> rangCol,
                           int columnCount, Map<String, String> stdColumnMappingMap,Map<String,Object> defaultValueMap ,SqlRowSetMetaData sqlRsmd,
                           String targetStdTable) {
        logger.info("deleteStdSql=====" + deleteStdSql);
        oracleJdbcTemplate.update(deleteStdSql, begin, end);
        logger.info("Start Iterator  tableNameList");
        int tableCount = tableNameList.size();
        String tb = "", rangeColName = "", tableSql = "";
        List dataList = null;
        long starQuery = 0l, endQuery = 0l;
        long queryTime=0l;
        for (int i = 0; i < tableCount; i++) {
            tb = tableNameList.get(i);
            rangeColName = rangCol.get(i);
            logger.info("Sync Table Name =====" + tb);
            logger.info("Get TableData "+tb+"  Range from =====" + simpleDateFormat.format(begin) + "=======to ======" + simpleDateFormat.format(end));
            tableSql = "select * from " + tb + " where " + rangeColName + ">=? and " + rangeColName + "<?";
            logger.info("tableSql=====" + tableSql);
            starQuery = System.currentTimeMillis();
            dataList = mysqlJdbcTemplate.queryForList(tableSql, begin, end);
            endQuery = System.currentTimeMillis();
            queryTime=endQuery - starQuery;
            logger.info("DataList From Mysql Total [" + dataList.size() + "]  Query Time  [" + queryTime + "] milliseconds ["+queryTime/1000+"] Seconds ["+queryTime/60000+"] minute");
            int dataSize = dataList.size();
            CommonBatchPreparedStatementsetter batchPreparedStatementSetter = new CommonBatchPreparedStatementsetter();
            batchPreparedStatementSetter.setColumnCount(columnCount);
            batchPreparedStatementSetter.setColumnMappingMap(stdColumnMappingMap);
            batchPreparedStatementSetter.setDefaultValueMap(defaultValueMap);
            batchPreparedStatementSetter.setDatalist(dataList);
            batchPreparedStatementSetter.setSqlRsmd(sqlRsmd);
            batchPreparedStatementSetter.setSize(dataSize);
            try {
                oracleJdbcTemplate.batchUpdate(insertStdSql, batchPreparedStatementSetter);
            } catch (Exception e) {
                logger.warn("Import Data to [" + targetStdTable+ "] Exception " + e.getMessage());
                e.printStackTrace();

            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFile(String csvFileName,String csvFilePath){
        try {

            sftp(sftpInfo.getHost(), sftpInfo.getUser(), sftpInfo.getPwd(), sftpInfo.getPrivateKey(), sftpInfo.getPassphrase(), sftpInfo.getRoot(), csvFilePath + File.separator + csvFileName, sftpInfo.getPort());
            logger.info("SEND FILE [" + csvFileName + " ] TO " + sftpInfo.getHost() + "  OVER");

        } catch (JSchException e) {
            logger.info("Start Send File To HiveNode [ " + sftpInfo.getHost() + " Exception [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }

    public Date getTime(String time) {
        try {
            Date date = DateUtils.parseDate(time);
            return date;
        } catch (ParseException e) {
            logger.info("ParseDate Error startTime=====[" + time + "]");
            e.printStackTrace();
            return null;
        }
    }


}
