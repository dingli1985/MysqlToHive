package com.asiainfo.util;

public class Des {

    public static void main(String args[]){
        //0A90312F2304E084
        try{
//          byte[] encBytes = ByteUtil.hex2byte("BDC502A7F749BF4A7111E1F204D6E1CA");
//          System.out.println(encBytes);
//          System.out.println(new String(CryptoUtil.desDecrypt(encBytes, "./config/gather20.key", KeyUtil.READ_KEY_FROM_FILESYSTEM)) );
            //System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("aiaup20".getBytes(), "./config/gather20.key", 2)) );
            //System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("Cbytt@48yx".getBytes(), "./config/lusm.key", 2)) );
//            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("Cbytt@48yx".getBytes(), "./config/lusm.key", 2)) );
//
//            byte[] encBytes = ByteUtil.hex2byte("529CCE1FE28E7CEFDD90DDBE515621B7");
//            System.out.println(encBytes);
//            System.out.println(new String(CryptoUtil.desDecrypt(encBytes, "./config/lusm.key", KeyUtil.READ_KEY_FROM_FILESYSTEM)) );
//
//            System.out.println("529CCE1FE28E7CEFDD90DDBE515621B7".length());
//            System.out.println("0A90312F2304E084".length());

            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("iapweb_321".getBytes(), "./config/lusm.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("uapweb_321".getBytes(), "./config/lusm.key", 2)) );


            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("iapweb_321".getBytes(), "./config/gather20.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("uapweb_321".getBytes(), "./config/gather20.key", 2)) );


            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("Brance_731".getBytes(), "./config/gather20.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("Brance_731".getBytes(), "./config/gather20.key", 2)) );

            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("audit30dev1".getBytes(), "./config/lusm.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("audit30dev1".getBytes(), "./config/gather20.key", 2)) );

            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("123qwe".getBytes(), "./config/lusm.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("123qwe".getBytes(), "./config/gather20.key", 2)) );

            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("123456".getBytes(), "./config/lusm.key", 2)) );
            System.out.println(ByteUtil.byte2hex(CryptoUtil.desEncrypt("123456".getBytes(), "./config/gather20.key", 2)) );



        }catch ( Exception e){
            e.printStackTrace();
        }

    }
}
