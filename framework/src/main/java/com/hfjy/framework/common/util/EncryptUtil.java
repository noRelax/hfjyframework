package com.hfjy.framework.common.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

public class EncryptUtil {

	public static KeyPair getKeyPair(int keysize) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(keysize);
		return keyPairGen.generateKeyPair();
	}

	public static byte[] RSAEncrypt(byte[] bytes, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(bytes);
	}

	public static byte[] RSADecrypt(byte[] bytes, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(bytes);
	}

	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = base32Decrypt(key.getBytes());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes = base32Decrypt(key.getBytes());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = base32Encrypt(key.getEncoded());
		return new String(keyBytes);
	}

	public static byte[] AESEncrypt(byte[] bytes, byte[] key) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(key));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return cipher.doFinal(bytes);
	}

	public static byte[] AESDecrypt(byte[] content, byte[] key) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(key));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(content);
	}

	public static byte[] DESEncrypt(byte[] bytes, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return cipher.doFinal(bytes);
	}

	public static byte[] DESDecrypt(byte[] bytes, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		return cipher.doFinal(bytes);
	}

	public static String md5(String inputText) throws Exception {
		MessageDigest m = MessageDigest.getInstance("md5");
		m.update(inputText.getBytes());
		return hex(m.digest());
	}

	public static byte[] md5(byte[] bytes) throws Exception {
		MessageDigest m = MessageDigest.getInstance("md5");
		m.update(bytes);
		return m.digest();
	}

	public static String md5String(byte[] bytes) throws Exception {
		MessageDigest m = MessageDigest.getInstance("md5");
		m.update(bytes);
		return hex(m.digest());
	}

	public static String sha(String inputText) throws Exception {
		MessageDigest m = MessageDigest.getInstance("sha-1");
		m.update(inputText.getBytes());
		return hex(m.digest());
	}

	public static byte[] sha(byte[] bytes) throws Exception {
		MessageDigest m = MessageDigest.getInstance("sha-1");
		m.update(bytes);
		return m.digest();
	}

	public static String shaString(byte[] bytes) throws Exception {
		MessageDigest m = MessageDigest.getInstance("sha-1");
		m.update(bytes);
		return hex(m.digest());
	}

	public static byte[] base32Encrypt(byte[] bytes) {
		Base32 base32 = new Base32();
		return base32.encode(bytes);
	}

	public static byte[] base32Decrypt(byte[] bytes) {
		Base32 base32 = new Base32();
		return base32.decode(bytes);
	}

	public static byte[] base64Encrypt(byte[] bytes) {
		return Base64.encodeBase64(bytes);
	}

	public static byte[] base64Decrypt(byte[] bytes) {
		return Base64.decodeBase64(bytes);
	}

	private static String hex(byte[] arr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}
}