package com.asiainfo;

import org.apache.commons.lang3.time.DateUtils;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Test {

    public static void main(String args[]){
//        Calendar cal = Calendar.getInstance();
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
//
//        Date now=new Date();
//        Date end =DateUtils.truncate(now,Calendar.DATE);
//        Date begin=DateUtils.addDays(end,-1);
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(begin));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end));
//          Date now =new Date();
//          Date end = DateUtils.truncate(now, Calendar.HOUR);
//
//          String mappingColumnName="DEVICE_TYPE";
//          String[] ar=mappingColumnName.split(":");
//
//        System.out.println(ar.length);
//        System.out.println(Timestamp.valueOf("2020-09-09 00:00:00.0"));
//
//        System.out.println("2020-09-09 00:00:00.0".substring(0,19));
        System.out.println(Pattern.matches("[0-9]{1,}_senderror", "202009160098787889_senderror"));



        //System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end));
//

//        ICsvMapWriter writer = null;
//        try {
//            writer = new CsvMapWriter(new FileWriter("1.csv"), CsvPreference.STANDARD_PREFERENCE);
//            final String[] header = new String[] { "name", "city", "zip" };
//            // set up some data to write
//            final HashMap<String, ? super Object> data1 = new HashMap<String, Object>();
//            data1.put(header[0], "Karl");
//            data1.put(header[1], "Tent city");
//            data1.put(header[2], 5565);
//            final HashMap<String, ? super Object> data2 = new HashMap<String, Object>();
//            data2.put(header[0], "Banjo");
//            data2.put(header[1], "River side");
//            data2.put(header[2], 5551);
//            // the actual writing
//            writer.writeHeader(header);
//            writer.write(data1, header);
//            writer.write(data2, header);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }



    }
}
