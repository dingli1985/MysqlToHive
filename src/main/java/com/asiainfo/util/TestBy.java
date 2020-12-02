package com.asiainfo.util;

import java.io.*;
import java.net.URISyntaxException;

public class TestBy {

    public static void main(String args[]){
        try {
          System.out.println(Class.forName("[B"));
          byte[] bytes=readFile("DES.properties");
          System.out.println(bytes);
            try {
                System.out.println(new String(bytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static byte[] readFile(String filename) {
        byte[] buffer = new byte[1024];
        InputStream is = null;

        try {
            try {
                is = new FileInputStream(new File("/Users/alee/工作/MysqlToHive/DES.properties"));
                try {
                    is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer;
    }
}
