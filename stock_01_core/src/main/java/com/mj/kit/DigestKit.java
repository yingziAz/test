package com.mj.kit;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 加密解密
 * 
 * @author frank.zong
 * 		
 */
public class DigestKit {
	
	/**
	 * PBE加密
	 */
	public static String pbeEncrypt(String str) {
		return PBEEncryption.encode(str);
	}
	
	/**
	 * PBE解密
	 */
	public static String pbeDecrypt(String str) {
		return PBEEncryption.decode(str);
	}
	

    /**
     * PBE解密后返回类型为Integer
     */
    public static Integer pbeDecryptToInt(String str) {
        String decodeStr = PBEEncryption.decode(str);
        Integer returnVal =null;
        try{
            returnVal = Integer.parseInt(decodeStr);
        }catch(NumberFormatException e){}
        return returnVal;
    }
    
    
	/**
	 * PBE加密,主要用于URL参数加密
	 * 
	 * @author frank.zong
	 */
	private static class PBEEncryption {
		
		// 随机盐
		private static final byte[] SALT = { (byte) 0x21, (byte) 0x21,
				(byte) 0xF0,
				(byte) 0x55, (byte) 0xC3, (byte) 0x9F, (byte) 0x5A,
				(byte) 0x75 };
				
		// 加密深度
		private final static int ITERATION_COUNT = 21;
		
		private PBEEncryption() {
		}
		
		public static String encode(String input) {
			if (input == null || input.length() == 0) {
				throw new IllegalArgumentException();
			}
			
			try {
				KeySpec keySpec = new PBEKeySpec(null, SALT, ITERATION_COUNT);
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT,
						ITERATION_COUNT);
						
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
						.generateSecret(keySpec);
				Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
				ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
				
				byte[] enc = ecipher.doFinal(input.getBytes());
				
				String res = new String(Base64.encodeBase64URLSafeString(enc));
				// escapes for url
				res = res.replace('-', '$')
				        .replace('+', '-')
				        .replace('/', '_')
						.replace("%", "%25")
						.replace("\n", "%0A");
						
				return res;
			} catch (Exception e) {
				return null;
			}
		}
		
		public static String decode(String token) {
			if (token == null || token.length() == 0) {
				return null;
			}
			
			try {
				String input = token.replace("%0A", "\n")
				        .replace("%25", "%")
						.replace('_', '/')
						.replace('-', '+')
						.replace('$', '-');
				byte[] dec = Base64.decodeBase64(input.getBytes());
				KeySpec keySpec = new PBEKeySpec(null, SALT, ITERATION_COUNT);
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT,
						ITERATION_COUNT);
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
						.generateSecret(keySpec);
				Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
				dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
				byte[] decoded = dcipher.doFinal(dec);
				String result = new String(decoded);
				return result;
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public static void main(String a[]){
	   System.out.println("--->"+DigestKit.pbeEncrypt("4")); 
	   System.out.println("=======>"+DigestKit.pbeDecrypt("wjZ$f210UZc"));
	}
}
