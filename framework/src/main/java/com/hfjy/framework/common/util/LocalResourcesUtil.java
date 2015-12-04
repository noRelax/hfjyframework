package com.hfjy.framework.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

public class LocalResourcesUtil {
	private final static String KEY = "GCBACVICAEADADIGBEVIMSEG64GQCAIBAUAAJAQBH4YIEAJ3AIAQAASBADGG7LUNYTEPKP2CA6Y2DLEML5L4Y5ZBIQTO4PF5J6AVTLBUH75PJ7DDPESFRGTNJ53VATUAKA5PWC6WGPODR47POCPWEZ4C7HM33BFZAIBQCAABAJADHOOWJKOI3VBPVM7VGHD3CIIVLH5QJTCHPLJTYUNINOPPVZILT2BKI5B7HR6MIQWLBMUEMIG5UO5KG3DXBZKLTPDAWN5LRICJJYQSXEBCCAH52OJ6ONWQ7ESKXRJBMIOVRH2XUGGREMKLSEEQLANOFXSQ4TROR4BCCAGOF7KYDHXGGNLYLLT3SE3B6MLNG2BGXWAZPQWOHDPOOIBM66B4G4BCCAF6W4WJQO6NYSCVGMXJERUVYX2NL2VTVERDN42FEGHAEDD4Y6O55UBCA7KSQG4WS5MODNJHXPKLQPJ63XHN23J6TY4DUBOSO5MZAKYEX2T5AIQQBXPIUOUUILBQGWZTJV5INSIW43STSOK6EFLREEOPKCUNMFPBJBNA";
	private final static int CIPHER_TEXT_NUM = 104;
	private final static Logger logger = LoggerFactory.getLogger(LocalResourcesUtil.class);

	public static InputStream getResourceAsStream(String path) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		return fis;
	}

	public static InputStream getOwnResourceAsStream(String fileName) {
		return new LocalResourcesUtil().getClass().getClassLoader().getResourceAsStream(fileName);
	}

	public static Properties getOwnProperties(String fileName) {
		Properties tmpProperties = new Properties();
		try {
			tmpProperties.load(getOwnResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return trim(tmpProperties);
	}

	public static Properties getProperties(String path) {
		Properties tmpProperties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(path);
			tmpProperties.load(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return trim(tmpProperties);
	}

	private static Properties trim(Properties p) {
		boolean isEncrypt = false;
		if (StringUtils.isNotEmpty(p.getProperty("isEncrypt"))) {
			if (p.getProperty("isEncrypt").trim().toUpperCase().equals("Y")) {
				isEncrypt = true;
				p.remove("isEncrypt");
			}
		}
		Iterator<String> iterator = p.stringPropertyNames().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			String value = p.getProperty(name);
			if (name != null && value != null) {
				String newName = name.trim();
				String newValue = value.trim();
				if (isEncrypt) {
					p.put(newName, valueDecrypt(newValue));
				} else {
					p.put(newName, newValue);
				}
			}
		}
		return p;
	}

	private static String valueDecrypt(String value) {
		byte[] valueBytes = value.getBytes();
		try {
			int no = value.length() / CIPHER_TEXT_NUM;
			for (int i = 0; i < no; i++) {
				char[] chars = value.toCharArray();
				int a = i * CIPHER_TEXT_NUM;
				int b = (i + 1) * CIPHER_TEXT_NUM;
				String col = new String(Arrays.copyOfRange(chars, a, b));
				byte[] bytes = EncryptUtil.base32Decrypt(col.getBytes());
				byte[] re = EncryptUtil.RSADecrypt(bytes, EncryptUtil.getPrivateKey(KEY));
				if (i == 0) {
					valueBytes = re;
				} else {
					valueBytes = dataMerger(valueBytes, re);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new String(valueBytes);
	}

	private static byte[] dataMerger(byte[] sign, byte[] data) {
		byte[] newData = new byte[sign.length + data.length];
		for (int i = 0; i < sign.length; i++) {
			newData[i] = sign[i];
		}
		for (int i = sign.length; i < newData.length; i++) {
			newData[i] = data[i - sign.length];
		}
		return newData;
	}
}