package com.asiainfo.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommonBatchPreparedStatementsetter implements BatchPreparedStatementSetter {

    private Log logger = LogFactory.getLog(SchedulerJob.class);

    private int size;

    private List datalist;

    private SqlRowSetMetaData sqlRsmd ;

    private Map columnMappingMap;

    private Map defaultValueMap;


    private int columnCount;

    String colName;
    String colType;
    String mappingColName;

    public void setColumnCount(int columnCount) {
        //logger.info("set columnCount--->"+columnCount);
        this.columnCount = columnCount;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDatalist(List datalist) {
        this.datalist = datalist;
    }

    public void setSqlRsmd(SqlRowSetMetaData sqlRsmd) {
        this.sqlRsmd = sqlRsmd;
    }

    public void setColumnMappingMap(Map columnMappingMap) {
        //logger.info("set columnMappingMap--->"+columnMappingMap);
        this.columnMappingMap = columnMappingMap;
    }

    public void setDefaultValueMap(Map defaultValueMap) {
        this.defaultValueMap = defaultValueMap;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        LinkedCaseInsensitiveMap record = (LinkedCaseInsensitiveMap)datalist.get(i);


        for (int j = 1; j <= columnCount; j++) {
            colName=sqlRsmd.getColumnName(j);
            colType=sqlRsmd.getColumnTypeName(j);
            if(defaultValueMap.containsKey(colName)){
                if(colType.equals("TIMESTAMP")){
                    preparedStatement.setTimestamp(j,(Timestamp)record.get(mappingColName) );//SESSION_ID
                }else{
                    preparedStatement.setString(j,(String)defaultValueMap.get(colName) );//SESSION_ID

                }
            }else{
                mappingColName=(String)columnMappingMap.get(colName);
                if(i==0){
                    //logger.info("colName-->" + colName + "  colType -->" + colType + "  mappingColName-->" + mappingColName);
                }
                if(colType.equals("TIMESTAMP")){
                    preparedStatement.setTimestamp(j,(Timestamp)record.get(mappingColName) );//SESSION_ID
                }else{
                    Object o=record.get(mappingColName);
                    if(null!=o){
                        if(o.getClass().getSimpleName().equals("Timestamp")){
                            Timestamp ts=(Timestamp) o;
                            preparedStatement.setString(j,ts.toString().substring(0,19) );//SESSION_ID
                        }else{
                            preparedStatement.setString(j,(String)record.get(mappingColName) );//SESSION_ID
                        }
                    }else{
                        preparedStatement.setString(j,null );//SESSION_ID
                    }
                }
            }
        }
    }

    @Override
    public int getBatchSize() {
        //logger.info("size ----------------------------------------------------->" + size );
        return size;
    }
}
