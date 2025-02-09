package cn.jianyun.worktime.util;


import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MyEncryptTool {

    public static final String MD5 = "MD5";
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    public static String uid(String s){
        String kk = md5(s);
        if(kk.length() < 10){
            return kk;
        }
        return kk.substring(0, 10);
    }

    public static String md5(String s){
        if(MyStringTool.isBlank(s)){
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md.update(s.getBytes(UTF8));
        return parseByte2HexStr(md.digest());
    }

    /**将二进制转换成16进制 */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }





    /**
     * AES加密为base 64 code
     *
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    private static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }



    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }



    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception 抛出异常
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return MyStringTool.isBlank(base64Code) ? null :  Base64.decode(base64Code, Base64.DEFAULT);
    }

    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     */
    private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }



    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     */
    private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    private static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return MyStringTool.isBlank(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }


    private static String makeKey(String k){
        return md5(k).substring(0, 16).toUpperCase();
    }

    public static String encrypt(String content){
        try {
            return cbcEncrypt(content, "qiangzi", "plan");
        } catch (Exception e) {
            return content;
        }
    }

    public static String decrypt(String content){
        String kk =  cbcDecrypt(content, "qiangzi", "plan");
        if(kk == null){
            return content;
        }
        return kk;
    }

    //加密
    private static String cbcEncrypt(String content,String iv, String key) throws Exception {
        String mkey = md5(key).toUpperCase().substring(0,16);
        byte[] raw = mkey.getBytes("utf-8");
        String miv = md5(iv).toUpperCase().substring(0,16);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ips = new IvParameterSpec(miv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    //解密
    private static String cbcDecrypt(String content,String iv, String key)  {
        try {
            String mkey = md5(key).toUpperCase().substring(0,16);
            byte[] raw = mkey.getBytes("utf-8");
            String miv = md5(iv).toUpperCase().substring(0,16);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(miv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);
            byte[] encrypted1 = Base64.decode(content, Base64.DEFAULT);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }


//
//    public static void main(String[] args) throws Exception {
//
//
//        String IV = "qsfty";
//        String KEY = "123";
//        String content = "aa";
//
//        System.out.println("加密前数据：" + content);
//        System.out.println("加密密钥和解密密钥：" + KEY);
//
//
//        String encrypt = aesEncrypt(content, makeKey(KEY));
//        System.out.println("ECB加密后：" + encrypt);
//        String decrypt = aesDecrypt(encrypt, makeKey(KEY));
//        System.out.println("ECB解密后：" + decrypt);
//
//
//
//        String s1 = cbcEncrypt(content,IV, KEY);
//
//        System.out.println("CBC加密后:" + s1);
//
//        String s2 = cbcDecrypt(s1,IV, KEY);
//
//        System.out.println("CBC解密后:" + s2);
//
//    }
}
