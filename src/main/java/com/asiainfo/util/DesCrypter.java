package com.asiainfo.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
/**
 * 4a2期 密码加密算法类
 * @author 丁立
 *
 */
public class DesCrypter {
	private Cipher ecipher;
	private Cipher dcipher;
    /**
     * 构造算法类
     * @param key 
     * @throws Exception
     */
	public DesCrypter(SecretKey key) throws Exception
	{
		ecipher = Cipher.getInstance("DES");
		dcipher = Cipher.getInstance("DES");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
	}
   /**
    * 加密字符串
    * @param str
    * @return
    * @throws Exception
    */
	public String encrypt(String str) throws Exception
	{
		byte[] utf8 = str.getBytes("UTF8");

		byte[] enc = ecipher.doFinal(utf8);

		return new sun.misc.BASE64Encoder().encode(enc);
	}
    /**
     * 解密字符串
     * @param str
     * @return
     * @throws Exception
     */
	public String decrypt(String str) throws Exception
	{
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		byte[] utf8 = dcipher.doFinal(dec);

		return new String(utf8, "UTF8");
	}

}
