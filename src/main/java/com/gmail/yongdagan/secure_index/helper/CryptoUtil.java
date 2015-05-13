package com.gmail.yongdagan.secure_index.helper;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CryptoUtil {
	
	public static final String AES = "AES";
	public static final String MAC = "HmacSHA256";
	private static final String AES_PADDING = "AES/ECB/PKCS5Padding";
	
	public static byte[] initKey(String algorithm) throws NoSuchAlgorithmException {
		// HMAC, AES
		KeyGenerator generator = KeyGenerator.getInstance(algorithm);
		SecureRandom random = new SecureRandom();
		generator.init(random);
		SecretKey key = generator.generateKey();
		return key.getEncoded();
	}
	
	public static byte[] generateHMAC(byte[] key, byte[] data) {
		SecretKey secretKey = new SecretKeySpec(key, MAC);
		Mac mac = null;
		try {
			mac = Mac.getInstance(MAC);
			mac.init(secretKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mac.doFinal(data);
	}
	
	public static byte[] encryptAES(byte[] key, byte[] data) throws Exception {
		return doAES(key, data, Cipher.ENCRYPT_MODE);
	}
	
	public static byte[] decryptAES(byte[] key, byte[] data) throws Exception {
		return doAES(key, data, Cipher.DECRYPT_MODE);
	}
	
	public static void encryptStreamAES(InputStream in, OutputStream out, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		cryptAES(in, out, cipher);
	}
	
	public static void decryptStreamAES(InputStream in, OutputStream out, byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		cryptAES(in, out, cipher);
	}
	
	private static void cryptAES(InputStream in, OutputStream out, Cipher cipher) throws Exception {
		final int blockSize = cipher.getBlockSize();
		final int outputSize = cipher.getOutputSize(blockSize);
		byte[] inBytes = new byte[blockSize];
		byte[] outBytes = new byte[outputSize];
		
		int inLength = 0;
		while(true) {
			inLength = in.read(inBytes);
			if(inLength == blockSize) {
				int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
				out.write(outBytes, 0, outLength);
			} else {
				break;
			}
		}
		if(inLength > 0) {
			outBytes = cipher.doFinal(inBytes, 0, inLength);
		} else {
			outBytes = cipher.doFinal();
		}
		out.write(outBytes);
	}
	
	private static byte[] doAES(byte[] key, byte[] data, int mode) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES_PADDING);
		cipher.init(mode, secretKey);
		return cipher.doFinal(data);
	}
	
	public static String encodeBASE64(byte[] data) {
		return new Base64().encodeToString(data);
	}
	
	public static byte[] decodeBASE64(String text) {
		return new Base64().decode(text);
	}
	
}
