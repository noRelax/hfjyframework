package com.hfjy.framework.message;

import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.common.util.EncryptUtil;
import com.hfjy.framework.common.util.FileUtils;
import com.hfjy.framework.init.ConfigDbUtil;
import com.hfjy.framework.logging.LoggerFactory;

public class ESBAccess implements MessageAccess<Serializable> {
	private static final Logger log = LoggerFactory.getLogger(ESBAccess.class);
	private static MessageAccess<Serializable> esbAccess = new ESBAccess();
	private final Map<String, Properties> sendProperties = new HashMap<>();

	private final static String delimiter = "{#}";

	private ESBAccess() {
		List<Properties> tmp = ConfigDbUtil.init().getAllSendEsbConfig();
		for (int i = 0; i < tmp.size(); i++) {
			sendProperties.put(tmp.get(i).getProperty("code"), tmp.get(i));
		}
	}

	public static MessageAccess<Serializable> init() {
		return esbAccess;
	}

	public AccessResult<?> send(Serializable message, String code) throws Exception {
		Properties tmp = sendProperties.get(code);
		AccessResult<?> re = new AccessResult<>();
		if (tmp != null) {
			String ip = tmp.getProperty("ip");
			int port = Integer.valueOf(tmp.getProperty("port"));
			String name = tmp.getProperty("name");
			Socket client = new Socket(ip, port);
			byte[] data = FileUtils.objectToBytes(message);
			MessageData md = new MessageData(name, SimpleDataEncrypt(data));
			client.getOutputStream().write(EncryptUtil.base64Encrypt(FileUtils.objectToBytes(md)));
			client.getOutputStream().flush();
			InputStream is = client.getInputStream();
			for (int i = 0; i < 10; i++) {
				if (is.available() > 0) {
					byte[] bf = new byte[is.available()];
					is.read(bf);
					Object obj = FileUtils.bytesToObject(bf);
					if (obj instanceof AccessResult) {
						re = (AccessResult<?>) obj;
					}
					break;
				}
				Thread.sleep(500);
			}
			client.close();
		}
		return re;
	}

	public AccessResult<?> send(Serializable message, String ip, int port) throws Exception {
		AccessResult<?> re = new AccessResult<>();
		byte[] data = FileUtils.objectToBytes(message);
		MessageData md = new MessageData("temp", SimpleDataEncrypt(data));
		Socket client = new Socket(ip, port);
		client.getOutputStream().write(EncryptUtil.base64Encrypt(FileUtils.objectToBytes(md)));
		client.getOutputStream().flush();
		InputStream is = client.getInputStream();
		for (int i = 0; i < 10; i++) {
			if (is.available() > 0) {
				byte[] bf = new byte[is.available()];
				is.read(bf);
				Object obj = FileUtils.bytesToObject(bf);
				if (obj instanceof AccessResult) {
					re = (AccessResult<?>) obj;
				}
				break;
			}
			Thread.sleep(500);
		}
		client.close();
		return re;
	}

	public static String getExecKey(String key, String name) {
		StringBuilder sb = new StringBuilder();
		sb.append("KEY=");
		sb.append(key.toUpperCase());
		sb.append("#NAME=");
		sb.append(name.toUpperCase());
		sb.append("#");
		return sb.toString().toUpperCase();
	}

	public static byte[] mergeBytes(byte[] a, byte[] b) {
		byte[] tmp = new byte[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			tmp[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			tmp[i + a.length] = b[i];
		}
		return tmp;
	}

	public static MessageData handleData(byte[] data) {
		try {
			byte[] objectBytes = EncryptUtil.base64Decrypt(data);
			Object javaObject = FileUtils.bytesToObject(objectBytes);
			if (javaObject instanceof MessageData) {
				return (MessageData) javaObject;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] SimpleDataEncrypt(byte[] data) {
		return EncryptUtil.base64Encrypt(data);
	}

	public static byte[] SimpleDataDecrypt(byte[] data) {
		return EncryptUtil.base64Decrypt(data);
	}

	public static byte[] dataEncrypt1(byte[] data, String key) {
		byte[] reData = null;
		try {
			byte[] execKey = EncryptUtil.sha(key).getBytes("UTF-8");
			String sign = EncryptUtil.sha(new String(data, "UTF-8"));
			byte[] execData = dataMerger((sign + delimiter).getBytes("UTF-8"), data);
			byte[] aesData = EncryptUtil.AESEncrypt(execData, execKey);
			reData = EncryptUtil.base64Encrypt(aesData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return reData;
	}

	public static byte[] dataDecrypt1(byte[] data, String key) {
		byte[] reData = null;
		try {
			byte[] execKey = EncryptUtil.sha(key).getBytes("UTF-8");
			byte[] aesData = EncryptUtil.base64Decrypt(data);
			byte[] execData = EncryptUtil.AESDecrypt(aesData, execKey);
			reData = verificationSign(execData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return reData;
	}

	private static byte[] verificationSign(byte[] data) throws Exception {
		byte[] bf = delimiter.getBytes();
		int no = bf.length;
		for (int i = 0; i < data.length; i++) {
			for (int b = 0; b < bf.length; b++) {
				if (bf[b] == data[i + b]) {
					no--;
				}
			}
			if (no == 0) {
				byte[] reData = new byte[data.length - i - bf.length];
				for (int z = 0; z < reData.length; z++) {
					reData[z] = data[z + i + bf.length];
				}
				byte[] signBytes = new byte[i];
				for (int z = 0; z < i; z++) {
					signBytes[z] = data[z];
				}
				String sign = EncryptUtil.sha(new String(reData, "UTF-8"));
				if (Arrays.equals(sign.getBytes("UTF-8"), signBytes)) {
					return reData;
				}
			} else {
				no = bf.length;
			}
		}
		return null;
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
