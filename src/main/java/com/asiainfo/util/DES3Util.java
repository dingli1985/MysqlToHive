package com.asiainfo.util;

import com.asiainfo.uap.util.des.EncryptData;
import com.asiainfo.uap.util.des.UnEncryptData;
import com.asiainfo.uap.util.des.Util;

import javax.crypto.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.StringTokenizer;

public class DES3Util {

    public static String desEncryptData(String str) {
        StringBuffer sb = new StringBuffer();
        String result = "";
        byte[] date_byte = str.getBytes();

        try {
            byte[] bss = createEncryptData(date_byte, "DES");
            sb.append(bss.length + "|");

            for(int i = 0; i < bss.length; ++i) {
                sb.append(bss[i] + "|");
            }

            result = sb.toString();
            result = result.substring(0, result.length() - 1);
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (IllegalStateException var8) {
            var8.printStackTrace();
        } catch (IllegalBlockSizeException var9) {
            var9.printStackTrace();
        } catch (URISyntaxException var10) {
            var10.printStackTrace();
        } catch (BadPaddingException var11) {
            var11.printStackTrace();
        } catch (NoSuchPaddingException var12) {
            var12.printStackTrace();
        } catch (InvalidKeySpecException var13) {
            var13.printStackTrace();
        } catch (NoSuchAlgorithmException var14) {
            var14.printStackTrace();
        } catch (IllegalArgumentException var15) {
            var15.printStackTrace();
        } catch (SecurityException var16) {
            var16.printStackTrace();
        } catch (InstantiationException var17) {
            var17.printStackTrace();
        } catch (IllegalAccessException var18) {
            var18.printStackTrace();
        } catch (InvocationTargetException var19) {
            var19.printStackTrace();
        } catch (NoSuchMethodException var20) {
            var20.printStackTrace();
        } catch (ClassNotFoundException var21) {
            var21.printStackTrace();
        } catch (IOException var22) {
            var22.printStackTrace();
        }

        return result;
    }

    public static String desUnEncryptData(String str) {
        UnEncryptData ud = new UnEncryptData();
        String ss = "";

        try {
            int byte_i = Integer.parseInt(str.substring(0, str.indexOf("|")));
            byte[] date_byte = new byte[byte_i];
            str = str.substring(str.indexOf("|") + 1);
            StringTokenizer st = new StringTokenizer(str, "|");

            for(int i = 0; st.hasMoreTokens(); ++i) {
                byte_i = Integer.parseInt(st.nextToken());
                date_byte[i] = (byte)byte_i;
            }

            byte[] bss = createUnEncryptData(date_byte, "DES");
            ss = new String(bss);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return ss;
    }

    public static byte[] createEncryptData(byte[] bytes, String algorithm) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, URISyntaxException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
        bytes = getencryptData(bytes, algorithm);
        return bytes;
    }

    public static byte[] createUnEncryptData(byte[] bytes, String algorithm) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, URISyntaxException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
        bytes = getunEncryptData(bytes, algorithm);
        return bytes;
    }

    private static byte[] getencryptData(byte[] bytes, String algorithm) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, InvocationTargetException, IllegalArgumentException, URISyntaxException, IllegalAccessException, InstantiationException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalStateException {
        SecureRandom sr = new SecureRandom();
        byte[] rawKeyData = (byte[])null;
        if (Util.getBufferFinal() == null) {
            rawKeyData = readFile("DES.properties");
            Util.setBufferFinal(rawKeyData);
        } else {
            rawKeyData = Util.getBufferFinal();
        }

        Class classkeyspec;
        if (algorithm.equals("DES")) {
            classkeyspec = Class.forName("javax.crypto.spec.DESKeySpec");
        } else if (algorithm.equals("PBE")) {
            classkeyspec = Class.forName("javax.crypto.spec.PBEKeySpec");
        } else {
            classkeyspec = Class.forName("javax.crypto.spec.DESKeySpec");
        }

        Class[] var10001 = new Class[1];
        Class var10004 = null;
        if (var10004 == null) {
            try {
                var10004 = Class.forName("[B");
            } catch (ClassNotFoundException var11) {
                throw new NoClassDefFoundError(var11.getMessage());
            }

        }

        var10001[0] = var10004;
        Constructor constructor = classkeyspec.getConstructor(var10001);
        KeySpec dks = (KeySpec)constructor.newInstance(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(1, key, sr);
        bytes = cipher.doFinal(bytes);
        return bytes;
    }

    private static byte[] getunEncryptData(byte[] bytes, String algorithm) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, URISyntaxException, InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalStateException {
        SecureRandom sr = new SecureRandom();
        byte[] rawKeyData = (byte[])null;
        if (Util.getBufferFinal() == null) {
            rawKeyData = readFile("DES.properties");
            Util.setBufferFinal(rawKeyData);
        } else {
            rawKeyData = Util.getBufferFinal();
        }

        Class classkeyspec;
        if (algorithm.equals("DES")) {
            classkeyspec = Class.forName("javax.crypto.spec.DESKeySpec");
        } else if (algorithm.equals("PBE")) {
            classkeyspec = Class.forName("javax.crypto.spec.PBEKeySpec");
        } else {
            classkeyspec = Class.forName("javax.crypto.spec.DESKeySpec");
        }

        Class[] var10001 = new Class[1];
        Class var10004 = null;
        if (var10004 == null) {
            try {
                var10004 = Class.forName("[B");
            } catch (ClassNotFoundException var11) {
                throw new NoClassDefFoundError(var11.getMessage());
            }
        }

        var10001[0] = var10004;
        Constructor constructor = classkeyspec.getConstructor(var10001);
        KeySpec dks = (KeySpec)constructor.newInstance(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(2, key, sr);
        bytes = cipher.doFinal(bytes);
        return bytes;
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

    public static void main(String args[]){
        System.out.println(desEncryptData("aiuap20"));
        System.out.println(desUnEncryptData("16|17|-62|65|-8|115|81|-119|77|-48|15|-111|49|-15|-57|42|20"));

    }
}
