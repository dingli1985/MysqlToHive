package com.asiainfo.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesUtil {

    /**
     * 通过keyString 得到 SecretKey 对象
     * @param keyString="h@9^ds7MrU$lp17Y"
     * @return SecretKey
     */
    private static SecretKey getSecretKey(String keyString) {
        SecretKey key = null;
        DESKeySpec dks = null;
        try {
            dks = new DESKeySpec(keyString.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(dks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private static SecretKey se= getSecretKey("h@9^ds7MrU$lp17Y");

    public static  void main(String args[]){
        DesCrypter desCrypter = null;
        try {
            desCrypter = new DesCrypter(se);
            System.out.println(desCrypter.encrypt("vfr4321`"));
            System.out.println(desCrypter.decrypt("f/vYedSYq4ULG+Nr54LkZQ=="));
            System.out.println(desCrypter.encrypt("aiuap20"));
            System.out.println(desCrypter.decrypt("oRYVV41h4V4="));
            System.out.println(desCrypter.encrypt("加密啊"));
            System.out.println(desCrypter.decrypt("LgLtJyA8jCASXA2PbOUIkQ=="));





        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
