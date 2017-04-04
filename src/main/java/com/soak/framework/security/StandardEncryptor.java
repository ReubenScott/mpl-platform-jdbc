package com.soak.framework.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.soak.common.metic.Base64;

/**
 * 功能描述 加密常用类
 */
public class StandardEncryptor {

  // 密钥是16位长度的byte[]进行Base64转换后得到的字符串
  public static String key = "LmMGStGtOpF4xNyvYt54EQ==";

  /**
   * MD5加码。32位
   */
  public static String MD5(String inStr) {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return "";
    }
    char[] charArray = inStr.toCharArray();
    byte[] byteArray = new byte[charArray.length];

    for (int i = 0; i < charArray.length; i++)
      byteArray[i] = (byte) charArray[i];

    byte[] md5Bytes = md5.digest(byteArray);

    StringBuffer hexValue = new StringBuffer();

    for (int i = 0; i < md5Bytes.length; i++) {
      int val = ((int) md5Bytes[i]) & 0xff;
      if (val < 16)
        hexValue.append("0");
      hexValue.append(Integer.toHexString(val));
    }

    return hexValue.toString();
  }

  /**
   * <li>
   * 方法名称:encrypt</li> <li>
   * 加密方法
   * 
   * @param xmlStr
   *          需要加密的消息字符串
   * @return 加密后的字符串
   */
  public static String encrypt(String xmlStr) {
    byte[] encrypt = null;

    try {
      // 取需要加密内容的utf-8编码。
      encrypt = xmlStr.getBytes("utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    // 取MD5Hash码，并组合加密数组
    byte[] md5Hasn = null;
    try {
      md5Hasn = StandardEncryptor.MD5Hash(encrypt, 0, encrypt.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 组合消息体
    byte[] totalByte = StandardEncryptor.addMD5(md5Hasn, encrypt);

    // 取密钥和偏转向量
    byte[] key = new byte[8];
    byte[] iv = new byte[8];
    getKeyIV(StandardEncryptor.key, key, iv);
    SecretKeySpec deskey = new SecretKeySpec(key, "DES");
    IvParameterSpec ivParam = new IvParameterSpec(iv);

    // 使用DES算法使用加密消息体
    byte[] temp = null;
    try {
      temp = StandardEncryptor.DES_CBC_Encrypt(totalByte, deskey, ivParam);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 使用Base64加密后返回
    return Base64.encode(temp);
  }

  /**
   * <li>
   * 方法名称:encrypt</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 解密方法
   * </pre>
   * 
   * </li>
   * 
   * @param xmlStr
   *          需要解密的消息字符串
   * @return 解密后的字符串
   * @throws Exception
   */
  public static String decrypt(String xmlStr) throws Exception {
    // base64解码
    byte[] encBuf = Base64.decode(xmlStr);
    // 取密钥和偏转向量
    byte[] key = new byte[8];
    byte[] iv = new byte[8];
    getKeyIV(StandardEncryptor.key, key, iv);

    SecretKeySpec deskey = new SecretKeySpec(key, "DES");
    IvParameterSpec ivParam = new IvParameterSpec(iv);

    // 使用DES算法解密
    byte[] temp = null;
    try {
      temp = StandardEncryptor.DES_CBC_Decrypt(encBuf, deskey, ivParam);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 进行解密后的md5Hash校验
    byte[] md5Hash = null;
    try {
      md5Hash = StandardEncryptor.MD5Hash(temp, 16, temp.length - 16);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 进行解密校检
    for (int i = 0; i < md5Hash.length; i++) {
      if (md5Hash[i] != temp[i]) {
        // System.out.println(md5Hash[i] + "MD5校验错误。" + temp[i]);
        throw new Exception("MD5校验错误。");
      }
    }

    // 返回解密后的数组，其中前16位MD5Hash码要除去。
    return new String(temp, 16, temp.length - 16, "utf-8");
  }

  /**
   * <li>
   * 方法名称:TripleDES_CBC_Encrypt</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 经过封装的三重DES/CBC加密算法，如果包含中文，请注意编码。
   * </pre>
   * 
   * </li>
   * 
   * @param sourceBuf
   *          需要加密内容的字节数组。
   * @param deskey
   *          KEY 由24位字节数组通过SecretKeySpec类转换而成。
   * @param ivParam
   *          IV偏转向量，由8位字节数组通过IvParameterSpec类转换而成。
   * @return 加密后的字节数组
   * @throws Exception
   */
  public static byte[] TripleDES_CBC_Encrypt(byte[] sourceBuf, SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {
    byte[] cipherByte;
    // 使用DES对称加密算法的CBC模式加密
    Cipher encrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");

    encrypt.init(Cipher.ENCRYPT_MODE, deskey, ivParam);

    cipherByte = encrypt.doFinal(sourceBuf, 0, sourceBuf.length);
    // 返回加密后的字节数组
    return cipherByte;
  }

  /**
   * <li>
   * 方法名称:TripleDES_CBC_Decrypt</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 经过封装的三重DES / CBC解密算法
   * </pre>
   * 
   * </li>
   * 
   * @param sourceBuf
   *          需要解密内容的字节数组
   * @param deskey
   *          KEY 由24位字节数组通过SecretKeySpec类转换而成。
   * @param ivParam
   *          IV偏转向量，由6位字节数组通过IvParameterSpec类转换而成。
   * @return 解密后的字节数组
   * @throws Exception
   */
  public static byte[] TripleDES_CBC_Decrypt(byte[] sourceBuf, SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {

    byte[] cipherByte;
    // 获得Cipher实例，使用CBC模式。
    Cipher decrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
    // 初始化加密实例，定义为解密功能，并传入密钥，偏转向量
    decrypt.init(Cipher.DECRYPT_MODE, deskey, ivParam);

    cipherByte = decrypt.doFinal(sourceBuf, 0, sourceBuf.length);
    // 返回解密后的字节数组
    return cipherByte;
  }

  /**
   * <li>
   * 方法名称:DES_CBC_Encrypt</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 经过封装的DES/CBC加密算法，如果包含中文，请注意编码。
   * </pre>
   * 
   * </li>
   * 
   * @param sourceBuf
   *          需要加密内容的字节数组。
   * @param deskey
   *          KEY 由8位字节数组通过SecretKeySpec类转换而成。
   * @param ivParam
   *          IV偏转向量，由8位字节数组通过IvParameterSpec类转换而成。
   * @return 加密后的字节数组
   * @throws Exception
   */
  public static byte[] DES_CBC_Encrypt(byte[] sourceBuf, SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {
    byte[] cipherByte;
    // 使用DES对称加密算法的CBC模式加密
    Cipher encrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");

    encrypt.init(Cipher.ENCRYPT_MODE, deskey, ivParam);

    cipherByte = encrypt.doFinal(sourceBuf, 0, sourceBuf.length);
    // 返回加密后的字节数组
    return cipherByte;
  }

  /**
   * <li>
   * 方法名称:DES_CBC_Decrypt</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 经过封装的DES/CBC解密算法。
   * </pre>
   * 
   * </li>
   * 
   * @param sourceBuf
   *          需要解密内容的字节数组
   * @param deskey
   *          KEY 由8位字节数组通过SecretKeySpec类转换而成。
   * @param ivParam
   *          IV偏转向量，由6位字节数组通过IvParameterSpec类转换而成。
   * @return 解密后的字节数组
   * @throws Exception
   */
  public static byte[] DES_CBC_Decrypt(byte[] sourceBuf, SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {

    byte[] cipherByte;
    // 获得Cipher实例，使用CBC模式。
    Cipher decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
    // 初始化加密实例，定义为解密功能，并传入密钥，偏转向量
    decrypt.init(Cipher.DECRYPT_MODE, deskey, ivParam);

    cipherByte = decrypt.doFinal(sourceBuf, 0, sourceBuf.length);
    // 返回解密后的字节数组
    return cipherByte;
  }

  /**
   * <li>
   * 方法名称:MD5Hash</li> <li>
   * 功能描述:
   * 
   * <pre>
   * MD5，进行了简单的封装，以适用于加，解密字符串的校验。
   * </pre>
   * 
   * </li>
   * 
   * @param buf
   *          需要MD5加密字节数组。
   * @param offset
   *          加密数据起始位置。
   * @param length
   *          需要加密的数组长度。
   * @return
   * @throws Exception
   */
  public static byte[] MD5Hash(byte[] buf, int offset, int length) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(buf, offset, length);
    return md.digest();
  }

  /**
   * <li>
   * 方法名称:addMD5</li> <li>
   * 功能描述:
   * 
   * <pre>
   * MD校验码 组合方法，前16位放MD5Hash码。 把MD5验证码byte[]，加密内容byte[]组合的方法。
   * </pre>
   * 
   * </li>
   * 
   * @param md5Byte
   *          加密内容的MD5Hash字节数组。
   * @param bodyByte
   *          加密内容字节数组
   * @return 组合后的字节数组，比加密内容长16个字节。
   */
  public static byte[] addMD5(byte[] md5Byte, byte[] bodyByte) {
    int length = bodyByte.length + md5Byte.length;
    byte[] resutlByte = new byte[length];

    // 前16位放MD5Hash码
    for (int i = 0; i < length; i++) {
      if (i < md5Byte.length) {
        resutlByte[i] = md5Byte[i];
      } else {
        resutlByte[i] = bodyByte[i - md5Byte.length];
      }
    }

    return resutlByte;
  }

  /**
   * <li>
   * 方法名称:getKeyIV</li> <li>
   * 功能描述:
   * 
   * <pre>
   * 
   * </pre>
   * </li>
   * 
   * @param encryptKey
   * @param key
   * @param iv
   */
  public static void getKeyIV(String encryptKey, byte[] key, byte[] iv) {
    // 密钥Base64解密
    byte[] buf = Base64.decode(encryptKey);
    // 前8位为key
    int i;
    for (i = 0; i < key.length; i++) {
      key[i] = buf[i];
    }
    // 后8位为iv向量
    for (i = 0; i < iv.length; i++) {
      iv[i] = buf[i + 8];
    }
  }

  /**
   * DES算法，加密
   * 
   * @param data
   *          待加密字符串
   * @param key
   *          加密私钥，长度不能够小于8位
   * @return 加密后的字节数组，一般结合Base64编码使用
   * @throws InvalidAlgorithmParameterException
   * @throws Exception
   */
  public static String encode(String key, String data) {
    if (data == null)
      return null;
    try {
      DESKeySpec dks = new DESKeySpec(key.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      // key的长度不能够小于8位字节
      Key secretKey = keyFactory.generateSecret(dks);
      Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      AlgorithmParameterSpec paramSpec = new IvParameterSpec(key.getBytes());
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
      byte[] bytes = cipher.doFinal(data.getBytes());
      return byte2hex(bytes);
    } catch (Exception e) {
      e.printStackTrace();
      return data;
    }
  }

  /**
   * DES算法，解密
   * 
   * @param data
   *          待解密字符串
   * @param key
   *          解密私钥，长度不能够小于8位
   * @return 解密后的字节数组
   * @throws Exception
   *           异常
   */
  public static String decode(String key, String data) {
    if (data == null)
      return null;
    try {
      DESKeySpec dks = new DESKeySpec(key.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      // key的长度不能够小于8位字节
      Key secretKey = keyFactory.generateSecret(dks);
      Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      AlgorithmParameterSpec paramSpec = new IvParameterSpec(key.getBytes());
      cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
      return new String(cipher.doFinal(hex2byte(data.getBytes())));
    } catch (Exception e) {
      e.printStackTrace();
      return data;
    }
  }

  /**
   * 二行制转字符串
   * 
   * @param b
   * @return
   */
  public static String byte2hex(byte[] b) {
    StringBuilder hs = new StringBuilder();
    String stmp;
    for (int n = 0; b != null && n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0XFF);
      if (stmp.length() == 1)
        hs.append('0');
      hs.append(stmp);
    }
    return hs.toString().toUpperCase();
  }

  private static byte[] hex2byte(byte[] b) {
    if ((b.length % 2) != 0)
      throw new IllegalArgumentException();
    byte[] b2 = new byte[b.length / 2];
    for (int n = 0; n < b.length; n += 2) {
      String item = new String(b, n, 2);
      b2[n / 2] = (byte) Integer.parseInt(item, 16);
    }
    return b2;
  }

  /**
   * 功能：编码字符串
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param data
   *          源字符串
   * @return String
   */
  public static String encode(String data) {
    return new String(encode(data.getBytes()));
  }

  /**
   * 功能：解码字符串
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param data
   *          源字符串
   * @return String
   */
  public static String decode(String data) {
    return new String(decode(data.toCharArray()));
  }

  /**
   * 功能：编码byte[]
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param data
   *          源
   * @return char[]
   */
  public static char[] encode(byte[] data) {
    char[] out = new char[((data.length + 2) / 3) * 4];
    for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
      boolean quad = false;
      boolean trip = false;

      int val = (0xFF & (int) data[i]);
      val <<= 8;
      if ((i + 1) < data.length) {
        val |= (0xFF & (int) data[i + 1]);
        trip = true;
      }
      val <<= 8;
      if ((i + 2) < data.length) {
        val |= (0xFF & (int) data[i + 2]);
        quad = true;
      }
      out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
      val >>= 6;
      out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
      val >>= 6;
      out[index + 1] = alphabet[val & 0x3F];
      val >>= 6;
      out[index + 0] = alphabet[val & 0x3F];
    }
    return out;
  }

  /**
   * 功能：解码
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param data
   *          编码后的字符数组
   * @return byte[]
   */
  public static byte[] decode(char[] data) {

    int tempLen = data.length;
    for (int ix = 0; ix < data.length; ix++) {
      if ((data[ix] > 255) || codes[data[ix]] < 0) {
        --tempLen; // ignore non-valid chars and padding
      }
    }
    // calculate required length:
    // -- 3 bytes for every 4 valid base64 chars
    // -- plus 2 bytes if there are 3 extra base64 chars,
    // or plus 1 byte if there are 2 extra.

    int len = (tempLen / 4) * 3;
    if ((tempLen % 4) == 3) {
      len += 2;
    }
    if ((tempLen % 4) == 2) {
      len += 1;

    }
    byte[] out = new byte[len];

    int shift = 0; // # of excess bits stored in accum
    int accum = 0; // excess bits
    int index = 0;

    // we now go through the entire array (NOT using the 'tempLen' value)
    for (int ix = 0; ix < data.length; ix++) {
      int value = (data[ix] > 255) ? -1 : codes[data[ix]];

      if (value >= 0) { // skip over non-code
        accum <<= 6; // bits shift up by 6 each time thru
        shift += 6; // loop, with new bits being put in
        accum |= value; // at the bottom.
        if (shift >= 8) { // whenever there are 8 or more shifted in,
          shift -= 8; // write them out (from the top, leaving any
          out[index++] = // excess at the bottom for next iteration.
          (byte) ((accum >> shift) & 0xff);
        }
      }
    }

    // if there is STILL something wrong we just have to throw up now!
    if (index != out.length) {
      throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
    }

    return out;
  }

  /**
   * 功能：编码文件
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param file
   *          源文件
   */
  public static void encode(File file) throws IOException {
    if (!file.exists()) {
      System.exit(0);
    }

    else {
      byte[] decoded = readBytes(file);
      char[] encoded = encode(decoded);
      writeChars(file, encoded);
    }
    file = null;
  }

  /**
   * 功能：解码文件。
   * 
   * @author 宋立君
   * @date 2014年07月03日
   * @param file
   *          源文件
   * @throws IOException
   */
  public static void decode(File file) throws IOException {
    if (!file.exists()) {
      System.exit(0);
    } else {
      char[] encoded = readChars(file);
      byte[] decoded = decode(encoded);
      writeBytes(file, decoded);
    }
    file = null;
  }

  //
  // code characters for values 0..63
  //
  private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

  //
  // lookup table for converting base64 characters to value in range 0..63
  //
  private static byte[] codes = new byte[256];
  static {
    for (int i = 0; i < 256; i++) {
      codes[i] = -1;
      // LoggerUtil.debug(i + "&" + codes[i] + " ");
    }
    for (int i = 'A'; i <= 'Z'; i++) {
      codes[i] = (byte) (i - 'A');
      // LoggerUtil.debug(i + "&" + codes[i] + " ");
    }

    for (int i = 'a'; i <= 'z'; i++) {
      codes[i] = (byte) (26 + i - 'a');
      // LoggerUtil.debug(i + "&" + codes[i] + " ");
    }
    for (int i = '0'; i <= '9'; i++) {
      codes[i] = (byte) (52 + i - '0');
      // LoggerUtil.debug(i + "&" + codes[i] + " ");
    }
    codes['+'] = 62;
    codes['/'] = 63;
  }

  private static byte[] readBytes(File file) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] b = null;
    InputStream fis = null;
    InputStream is = null;
    try {
      fis = new FileInputStream(file);
      is = new BufferedInputStream(fis);
      int count = 0;
      byte[] buf = new byte[16384];
      while ((count = is.read(buf)) != -1) {
        if (count > 0) {
          baos.write(buf, 0, count);
        }
      }
      b = baos.toByteArray();

    } finally {
      try {
        if (fis != null)
          fis.close();
        if (is != null)
          is.close();
        if (baos != null)
          baos.close();
      } catch (Exception e) {
        System.out.println(e);
      }
    }

    return b;
  }

  private static char[] readChars(File file) throws IOException {
    CharArrayWriter caw = new CharArrayWriter();
    Reader fr = null;
    Reader in = null;
    try {
      fr = new FileReader(file);
      in = new BufferedReader(fr);
      int count = 0;
      char[] buf = new char[16384];
      while ((count = in.read(buf)) != -1) {
        if (count > 0) {
          caw.write(buf, 0, count);
        }
      }

    } finally {
      try {
        if (caw != null)
          caw.close();
        if (in != null)
          in.close();
        if (fr != null)
          fr.close();
      } catch (Exception e) {
        System.out.println(e);
      }
    }

    return caw.toCharArray();
  }

  private static void writeBytes(File file, byte[] data) throws IOException {
    OutputStream fos = null;
    OutputStream os = null;
    try {
      fos = new FileOutputStream(file);
      os = new BufferedOutputStream(fos);
      os.write(data);

    } finally {
      try {
        if (os != null)
          os.close();
        if (fos != null)
          fos.close();
      } catch (Exception e) {
        System.out.println(e);
      }
    }
  }

  private static void writeChars(File file, char[] data) throws IOException {
    Writer fos = null;
    Writer os = null;
    try {
      fos = new FileWriter(file);
      os = new BufferedWriter(fos);
      os.write(data);

    } finally {
      try {
        if (os != null)
          os.close();
        if (fos != null)
          fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println(encrypt("123456"));
    System.out.println(decrypt("oqGiG3w2C/s4l945xI++My4Wpv2cCyLi"));
  }
}
