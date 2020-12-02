package com.asiainfo.job;

import com.asiainfo.config.*;
import com.asiainfo.util.SftpUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


@Component
public class SchedulerJob {

    private Log logger = LogFactory.getLog(SchedulerJob.class);

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

    private static final CsvPreference csvPreferenceown = new CsvPreference.Builder('"', '\001', "\r\n").build();



    public void sftp(String host, String user, String pwd, String privateKey, String passphrase, String ftpRootDir, String uploadFile, int port)
            throws JSchException {
        SftpUtils mySftp = new SftpUtils(host, user, pwd, privateKey, passphrase, port);

        ChannelSftp sftp = mySftp.connectSFTP();
        mySftp.upload(ftpRootDir, uploadFile, sftp);
        mySftp.disconnected(sftp);
    }


    @Scheduled(cron = "${sync-table.cron}")
    public void reportCurrentTime() {
        Date now = new Date();

        List<String> tableNameList = listConfig.getTableNameList();
        List<String> rangCol = listConfig.getRangeCol();

        String targetTablePrefixStd = env.getProperty("target.table.prefix.std");
        String targetTablePrefixOri = env.getProperty("target.table.prefix.ori");
        String targetTableSuffix = ymFormat.format(now);
        String targetTableRangeColStd = env.getProperty("target.table.rangeCol.std");
        String targetStdTable=targetTablePrefixStd + targetTableSuffix;
        String targetStdTableFilter=env.getProperty("target.table.filter.std");

        logger.info("targetTablePrefixOri=====" + targetTablePrefixOri);

        logger.info("targetTableSuffix=====" + targetTableSuffix);


        SqlRowSet sqlRowSet = oracleJdbcTemplate.queryForRowSet("select * from " + targetStdTable+ " where 1=0");
        SqlRowSetMetaData sqlRsmd = sqlRowSet.getMetaData();
        int columnCount = sqlRsmd.getColumnCount();
        StringBuffer selCol = new StringBuffer();
        StringBuffer questionMark = new StringBuffer();
        String[] header = new String[columnCount];
        String rangeColType="";
        for (int j = 1; j <= columnCount; j++) {
            logger.info(sqlRsmd.getColumnName(j)+"=====" + sqlRsmd.getColumnTypeName(j));

            if(targetTableRangeColStd.equalsIgnoreCase(sqlRsmd.getColumnName(j))){
                logger.info("RangeCol ========"+sqlRsmd.getColumnName(j)+"=====" + sqlRsmd.getColumnTypeName(j));
                rangeColType=sqlRsmd.getColumnTypeName(j);
            }
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
        boolean endLoopExit=false;

        if (!"".equals(startTime) && !"".equals(endTime)) {
            try {
                begin = DateUtils.parseDate(startTime, "yyyyMMddHHmmss".intern());
                end = DateUtils.parseDate(endTime, "yyyyMMddHHmmss".intern());
                endLoopExit=true;
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
            endLoopExit=true;

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
            endLoopExit=true;
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

        String stdDatalistSql="",deleteStdSql="";

        logger.info("GetData  Range from =====" + simpleDateFormat.format(begin) + "=======to ======" + simpleDateFormat.format(end));
        String csvFileName="";
        List csvDataList=null;
        String csvFilePath=env.getProperty("csv-path");
        String sftpSwitch = env.getProperty("sftp-switch");
        String csvDataFilter=env.getProperty("csv-data-filter");
        //文件扩展
        String csvFileExt=env.getProperty("csv-file-ext");
        String pageQuerySwitch=env.getProperty("page-query.switch");
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
            deleteStdSql = "delete " + targetStdTable + " where " + targetTableRangeColStd + " >? and " + targetTableRangeColStd + " <? and "+targetStdTableFilter;
            //String updateStdSql = "update " + targetStdTable + " set device_type= ? where " + targetTableRangeColStd + " >? and " + targetTableRangeColStd + " <? and "+targetStdTableFilter;;

            if("true".equals( syncMysqlSwitch)||"on".equals(syncMysqlSwitch)) {
                String insertStdSql = "INSERT INTO " + targetStdTable + "(" + selCol.toString() + ") values (" + questionMark.toString() + ")";
                logger.info("deleteStdSql=====" + deleteStdSql);
                if("TIMESTAMP".equals(rangeColType)) {
                    oracleJdbcTemplate.update(deleteStdSql, begin, tempDate);
                }else{
                    oracleJdbcTemplate.update(deleteStdSql, simpleDateFormat.format(begin), simpleDateFormat.format(tempDate));
                }
                logger.info("insertStdSql=====" + insertStdSql);
                if("true".equals(pageQuerySwitch)||"on".equals(pageQuerySwitch)){
                    String pageSizeStr=env.getProperty("page-query.size");
                    importDataByPage(begin, tempDate, insertStdSql, tableNameList, rangCol, columnCount, stdColumnMappingMap,defaultValueMap, sqlRsmd, targetStdTable,pageSizeStr);
                }else{
                    importData(begin, tempDate, insertStdSql, tableNameList, rangCol, columnCount, stdColumnMappingMap,defaultValueMap, sqlRsmd, targetStdTable);
                }

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

            stdDatalistSql = "select * from " + targetStdTable +
                    " where " + targetTableRangeColStd + ">= ? " +
                    " and " + targetTableRangeColStd + " < ? and "+csvDataFilter;

            csvFilePath = env.getProperty("csv-path");
            logger.info("stdDatalistSql ["+stdDatalistSql+"]");
            logger.info("rangeColType ["+rangeColType+"]");
            logger.info("Get TableData "+targetStdTable+"  Range from =====" + simpleDateFormat.format(begin) + "=======to ======" + simpleDateFormat.format(tempDate));
            if("true".equals(pageQuerySwitch)||"on".equals(pageQuerySwitch)){
                if("TIMESTAMP".equals(rangeColType)) {
                    createCsvFileByPage(selCol.toString(), targetStdTable, targetTableRangeColStd, begin, tempDate, csvDataFilter, header, csvFileName, csvFilePath);
                }else{
                    createCsvFileByPage(selCol.toString(), targetStdTable, targetTableRangeColStd, simpleDateFormat.format(begin), simpleDateFormat.format(tempDate), csvDataFilter, header, csvFileName, csvFilePath);
                }
            }else{
                if("TIMESTAMP".equals(rangeColType)) {
                    createCsvFile(stdDatalistSql, begin, tempDate, header, csvFileName, csvFilePath);
                }else{
                    createCsvFile(stdDatalistSql, simpleDateFormat.format(begin), simpleDateFormat.format(tempDate), header, csvFileName, csvFilePath);

                }
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
                    " where " + targetTableRangeColStd + ">= ?" +
                    " and " + targetTableRangeColStd + " < ?  and "+csvDataFilter;
            logger.info("stdDatalistSql ["+stdDatalistSql+"]");

            csvDataList = oracleJdbcTemplate.queryForList(stdDatalistSql,begin,end);
            csvFileName = targetTablePrefixStd + ymdFormat.format(begin);

            createCsvFile(stdDatalistSql,begin,end,header,csvFileName,csvFilePath);
            if("true".equals(sftpSwitch)){
                sendFile(csvFileName,csvFilePath);
            }

        }
        if (endLoopExit) {
            logger.info("loop end program will exit");
            System.exit(0);
        }

    }

    public void createCsvFile(String stdDatalistSql,Object begin,Object end ,String[] header,String csvFileName,String csvFilePath){
        List csvDataList = oracleJdbcTemplate.queryForList(stdDatalistSql,begin,end);
        int csvDataSize = csvDataList.size();
        logger.info("Start Generate Csv File " + csvFilePath + File.separator + csvFileName);

        ICsvMapWriter writer = null;
        try {
            writer = new CsvMapWriter(new FileWriter(csvFilePath + File.separator + csvFileName), csvPreferenceown);

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


    public void createCsvFileByPage(String selCol,String targetStdTable,String targetTableRangeColStd,Object begin,Object end,String csvDataFilter,String[] header,String csvFileName,String csvFilePath ){
        String pageSql="select "+selCol+" from " +
                "( select rownum as r,a.* from "+targetStdTable+" a " +
                "where "+targetTableRangeColStd+">=? and "+targetTableRangeColStd+" <? and "+csvDataFilter+ ") " +
                "where r>? and r<=? ";
        String pageSizeStr=env.getProperty("page-query.size");
        logger.info("pageSize=====" + pageSizeStr);

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
            writer = new CsvMapWriter(new FileWriter(csvFilePath + File.separator + csvFileName), csvPreferenceown);
            writer.writeHeader(header);
        }catch (IOException e) {
            logger.info("Generate csv Error=====" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("Generate csv begin=====" + begin +"  begin===="+end);
        LinkedCaseInsensitiveMap data = null;
        while(flag){
            pageStart=i*pageSize;
            pageEnd=(i+1)*pageSize;
            //oracleJdbcTemplate.queryForList(pageSql,"1","2","3","4");
            logger.info("generate csv page "+(i+1)+" record from  "+pageStart+" to "+pageEnd);

            csvPageDataList=oracleJdbcTemplate.queryForList(pageSql,begin,end,pageStart,pageEnd);
            csvDataSize=csvPageDataList.size();
            try {
                // set up some data to write
                for (int j = 0; j < csvDataSize; j++) {
                    data = (LinkedCaseInsensitiveMap) csvPageDataList.get(j);
                    writer.write(data, header);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(csvDataSize<pageSize){
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

    public void importData(Date begin, Date end, String insertStdSql, List<String> tableNameList, List<String> rangCol,
                           int columnCount, Map<String, String> stdColumnMappingMap,Map<String,Object> defaultValueMap ,SqlRowSetMetaData sqlRsmd,
                           String targetStdTable) {
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

    public void importDataByPage(Date begin, Date end, String insertStdSql, List<String> tableNameList, List<String> rangCol,
                           int columnCount, Map<String, String> stdColumnMappingMap,Map<String,Object> defaultValueMap ,SqlRowSetMetaData sqlRsmd,
                           String targetStdTable,String pageSizeStr) {
        logger.info("Start Iterator  tableNameList");
        int tableCount = tableNameList.size();
        String tb = "", rangeColName = "", tablePageSql = "";
        List dataList = null;
        long starQuery = 0l, endQuery = 0l;
        long queryTime=0l;
        for (int i = 0; i < tableCount; i++) {
            tb = tableNameList.get(i);
            rangeColName = rangCol.get(i);
            logger.info("Sync Table Name =====" + tb);
            logger.info("Get TableData "+tb+"  Range from =====" + simpleDateFormat.format(begin) + "=======to ======" + simpleDateFormat.format(end));
            tablePageSql = "select * from " + tb + " where " + rangeColName + ">=? and " + rangeColName + "< ? limit ? ,?";
            logger.info("tablePageSql=====" + tablePageSql);
            int j=0;
            int pageSize=0;
            try {
                pageSize = Integer.parseInt(pageSizeStr);
            }catch (Exception e){
                logger.info("parse PageSize Error=====" + e.getMessage());

            }
            boolean flag=true;
            int pageStart=0,pageEnd=0;
            while(flag) {
                pageStart = j * pageSize;
                starQuery = System.currentTimeMillis();
                dataList = mysqlJdbcTemplate.queryForList(tablePageSql, begin, end,pageStart,pageSize);
                endQuery = System.currentTimeMillis();
                queryTime=endQuery - starQuery;
                logger.info("DataList From Mysql page "+(j+1)+" record "+pageStart+" to "+pageEnd+" Total [" + dataList.size() + "]  Query Time  [" + queryTime + "] milliseconds ["+queryTime/1000+"] Seconds ["+queryTime/60000+"] minute");
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
                if(dataSize<pageSize){
                    flag=false;
                }
                j++;
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
