/**
 * 海风在线学习平台
 * @Title: FileUtils.java 
 * @Package: com.hyphen.util
 * @author: cloud
 * @date: 2014年5月15日-下午1:16:54
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.common.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;

import com.hfjy.framework.common.entity.FileInfo;
import com.hfjy.framework.common.entity.Folder;
import com.hfjy.framework.logging.LoggerFactory;

/**
 * @ClassName: FileUtils
 * @Description: 文件访问帮助类
 * @author cloud
 * @date 2014年5月15日 下午1:16:54
 * 
 */
public class FileUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 解压到指定目录
	 * 
	 * @param zipPath
	 *            压缩文件
	 * @param descDir
	 *            解压缩目录
	 * @author cloud
	 */
	public static void unZipFiles(String zipPath, String descDir)
			throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zipFile = new ZipFile(zipPath);
		Enumeration<? extends ZipEntry> entrys = zipFile.entries();
		while (entrys.hasMoreElements()) {
			ZipEntry entry = entrys.nextElement();
			String zipEntryName = entry.getName();
			zipEntryName = zipEntryName
					.substring(zipEntryName.lastIndexOf("/"));
			String outPath = descDir + zipEntryName;
			OutputStream out = new FileOutputStream(outPath);
			InputStream in = zipFile.getInputStream(entry);
			byte[] bt = new byte[in.available()];
			in.read(bt);
			in.close();
			out.write(bt);
			out.flush();
			out.close();
		}
		zipFile.close();
	}

	/**
	 * 解压文件获取文件二进制数据
	 * 
	 * @param zipPath
	 *            压缩文件
	 * @param serialNo
	 *            序号，配合文件名生成唯一字符串
	 * @author cloud
	 * @throws IOException
	 */
	public static List<Object[]> unZipFilesToBytes(String zipPath,
			String serialNo) throws IOException {
		ZipFile zipFile = new ZipFile(zipPath, StandardCharsets.ISO_8859_1);
		Enumeration<? extends ZipEntry> entrys = zipFile.entries();
		List<Object[]> list = new ArrayList<>();
		while (entrys.hasMoreElements()) {
			// org.apache.tools.zip.ZipEntry entry = entrys.nextElement();
			ZipEntry entry = entrys.nextElement();
			if (!entry.isDirectory()) {
				String fileName = entry.getName();
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				try (InputStream in = zipFile.getInputStream(entry)) {
					byte[] bt = readBytes(in);
					list.add(new Object[] { bt, fileName });
				} catch (IOException e) {
					zipFile.close();
					throw e;
				}
			}
		}
		zipFile.close();
		return list;
	}

	/**
	 * 根据路径与文件名以及要压缩的文件创建ZIP压缩文件
	 * 
	 * @param path
	 *            压缩文件的目录
	 * @param filename
	 *            压缩文件的名称
	 * @param paths
	 *            要压缩文件的绝对路径
	 * @return 是否成功
	 */
	public static boolean createZipFile(String path, String filename,String... paths) {
		if (createFolder(path)) {
			try {
				String allPath = StringUtils.unite(path, File.separator,filename, ".zip");
				FileOutputStream f = new FileOutputStream(allPath);
				CheckedOutputStream csum = new CheckedOutputStream(f,new Adler32());
				ZipOutputStream zos = new ZipOutputStream(csum);
				BufferedOutputStream out = new BufferedOutputStream(zos);
				zos.setComment("JAVA ZIP compressed files");
				for (int i = 0; i < paths.length; i++) {
					FileInputStream fis = new FileInputStream(paths[i]);
					byte[] bf = new byte[fis.available()];
					zos.putNextEntry(new ZipEntry(paths[i].substring(paths[i].lastIndexOf(File.separator) + 1)));
					fis.read(bf);
					out.write(bf);
					out.flush();
					fis.close();
				}
				out.close();
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return false;
	}
	
	/**
	 * @desc: createPath 创建压缩文件目录
	 * 	filename：压缩文件的文件名
	 * sourceFolder 要压缩的文件目录 或 文件
	 * @author: spinach
	 * @Date:2015年7月13日
	 * @Title:createZipFile
	 */
	public static boolean createZipFolderOrFile(String createPath, String filename,String sourceFolder) {
		File sourceFile = new File(sourceFolder);
		if (!sourceFile.exists()) {
			return false;
		}
		if (createFolder(createPath)) {
			try {
				String allPath = StringUtils.unite(createPath, File.separator,	filename, ".zip");
				FileOutputStream f = new FileOutputStream(allPath);
				CheckedOutputStream csum = new CheckedOutputStream(f,new Adler32());
				ZipOutputStream zos = new ZipOutputStream(csum);
				BufferedOutputStream out = new BufferedOutputStream(zos);
				zos.setComment("JAVA ZIP compressed files");
				zip(zos, sourceFile,"你好");
				out.close();
				zos.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return false;
	}
	
	/**
	 * @desc: zos zip输出流  sourceFile：目标文件 folderName：创建文件夹名称
	 * 
	 * @author: spinach
	 * @Date:2015年7月13日
	 * @Title:zip
	 */
	public static void zip(ZipOutputStream zos, File sourceFile,String folderName) {
			if(StringUtils.isNotEmpty(folderName)&&!folderName.endsWith(File.separator)){
				folderName += File.separator;
			}
			try {
				// 如果文件句柄是目录
				if (sourceFile.isDirectory()) {
					// 获取目录下的文件
					File[] listFiles = sourceFile.listFiles();
					// 遍历目录下文件
					for (int i = 0; i < listFiles.length; i++) {
						if(listFiles[i].isDirectory()){
							// 建立ZIP条目 
							zip(zos, listFiles[i],	folderName+listFiles[i].getName()+File.separator);
						}else{
							// 递归进入本方法
							zip(zos, listFiles[i],	folderName);
						}
					}
				} else {
					// 从文件入流读,写入ZIP 出流
					FileInputStream fis = new FileInputStream(sourceFile);
					byte[] bf = new byte[fis.available()];
					zos.putNextEntry(new ZipEntry(folderName+sourceFile.getName()));
					fis.read(bf);
					zos.write(bf);
					zos.flush();
					fis.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
	}

	public static void main(String[] args) {
		createZipFolderOrFile("F:\\workspace", "压缩测试",	"F:\\workspace\\压缩和解压缩");
	}

	/**
	 * 根据文件的Byte数组创建ZIP压缩文件并返回压缩文件的Byte数组
	 * 
	 * @param file
	 *            要压缩的文件
	 * @param name
	 *            文件的名称
	 * @return 压缩完的文件
	 */
	public static byte[] createZipFileBytes(byte[] file, String name) {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			CheckedOutputStream csum = new CheckedOutputStream(byteArray,
					new Adler32());
			ZipOutputStream zos = new ZipOutputStream(csum);
			BufferedOutputStream out = new BufferedOutputStream(zos);
			zos.setComment("JAVA ZIP compressed files");
			zos.putNextEntry(new ZipEntry(name));
			out.write(file);
			out.close();
			return byteArray.toByteArray();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 根据压缩文件Byte数组解压出文件Byte数组
	 * 
	 * @return 字符串
	 */
	public static List<byte[]> readZipFile(byte[] zipFile) {
		List<byte[]> byteList = new ArrayList<>();
		try {
			ByteArrayInputStream byteArray = new ByteArrayInputStream(zipFile);
			CheckedInputStream csumi = new CheckedInputStream(byteArray,
					new Adler32());
			ZipInputStream in2 = new ZipInputStream(csumi);
			BufferedInputStream bis = new BufferedInputStream(in2);
			while ((in2.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] bytes = new byte[10240];
				int len = 0;
				while ((len = bis.read(bytes)) != -1) {
					baos.write(bytes, 0, len);
				}
				byteList.add(baos.toByteArray());
				baos.close();
			}
			bis.close();
			return byteList;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 根据压缩文件Byte数组解压出文件Byte数组
	 * 
	 * @return 字符串
	 */
	public static List<Object[]> readZipFileInfo(byte[] zipFile) {
		List<Object[]> list = new ArrayList<>();
		try {
			ByteArrayInputStream byteArray = new ByteArrayInputStream(zipFile);
			CheckedInputStream csumi = new CheckedInputStream(byteArray,
					new Adler32());
			ZipInputStream in2 = new ZipInputStream(csumi,StandardCharsets.ISO_8859_1);
			BufferedInputStream bis = new BufferedInputStream(in2);
			ZipEntry zipEntry;
			while (( zipEntry= in2.getNextEntry()) != null) {
				if (!zipEntry.isDirectory()) {
					String fileName = zipEntry.getName();
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] bytes = new byte[10240];
					int len = 0;
					while ((len = bis.read(bytes)) != -1) {
						baos.write(bytes, 0, len);
					}
					list.add(new Object[]{baos.toByteArray(),fileName});
					baos.close();
				}
			}
			bis.close();
			return list;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 根据文件名读取压缩文件
	 * 
	 * @return 字符串
	 */
	public static List<byte[]> readZipFile(String filename) {
		try {
			List<byte[]> list = new ArrayList<>();
			FileInputStream fi = new FileInputStream(filename);
			CheckedInputStream csumi = new CheckedInputStream(fi, new Adler32());
			ZipInputStream in2 = new ZipInputStream(csumi);
			BufferedInputStream bis = new BufferedInputStream(in2);
			while ((in2.getNextEntry()) != null) {
				byte[] bf = new byte[bis.available()];
				bis.read(bf);
				list.add(bf);
			}
			bis.close();
			return list;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static byte[] objectToBytes(Serializable object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] bytes = baos.toByteArray();
		oos.close();
		baos.close();
		return bytes;
	}

	public static Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object tmpObject = ois.readObject();
		ois.close();
		bais.close();
		return tmpObject;
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
		}
		return file.exists();
	}

	public static void deleteFiles(String path) {
		File f = new File(path);
		if (f.isFile()) {
			f.delete();
		} else {
			String[] names = f.list();
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					deleteFiles(path + File.separator + names[i]);
				}
			}
			f.delete();
		}
	}

	public static boolean createFile(String path, byte[] bf) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(bf);
		fos.close();
		return true;
	}

	public static boolean createFolder(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.exists();
	}

	public static void folderCopy(String fromPath, String toPath)
			throws IOException {
		File f = new File(fromPath);
		if (f.isFile()) {
			fileCopy(fromPath, toPath);
		} else {
			File t = new File(toPath);
			if (!t.exists()) {
				t.mkdirs();
			}
			String[] names = f.list();
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					folderCopy(fromPath + File.separator + names[i], toPath
							+ File.separator + names[i]);
				}
			}
		}
	}

	public static void fileCopy(String fromPath, String toPath)
			throws IOException {
		FileInputStream fis = new FileInputStream(fromPath);
		FileOutputStream fos = new FileOutputStream(toPath);
		FileChannel from = fis.getChannel();
		FileChannel to = fos.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(fis.available());
		from.read(buffer);
		buffer.flip();
		to.write(buffer);
		from.close();
		to.close();
		fis.close();
		fos.close();
	}

	/**
	 * 获取流的所有字节数
	 * 
	 * @author cmq
	 * @date 2014年12月1日 上午11:14:10
	 * @param in
	 *            输入流
	 * @return
	 * @throws IOException
	 * @throws
	 */
	public static byte[] readBytes(InputStream in) throws IOException {
		BufferedInputStream bufin = new BufferedInputStream(in);
		int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);
		byte[] temp = new byte[buffSize];
		int size = 0;
		while ((size = bufin.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		byte[] content = out.toByteArray();
		bufin.close();
		out.close();
		return content;
	}

	/**
	 * InputStream 转string
	 * 
	 * @author cmq
	 * @date 2015年1月20日 下午6:12:55
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = in.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
	
	public static InputStream byteToInputStream(byte[] bf){
		return new ByteArrayInputStream(bf);
	}

	/**
	 * 把图片压缩成指点的宽度
	 * 
	 * @author cmq
	 * @date 2015年1月16日 下午4:37:42
	 * @param width
	 *            宽度大小 px
	 * @param bytes
	 *            被压缩文件字节数组
	 * @return
	 * @throws IOException
	 */
	public static byte[] changeImageSize(int width, byte[] bytes) throws IOException {
		Image src = javax.imageio.ImageIO.read(new ByteArrayInputStream(bytes));
		if (src.getWidth(null) > width) {
			return changeImageSize(width, src.getHeight(null), src);
		}
		return bytes;
	}

	/**
	 * 把图片压缩到指定大小
	 * 
	 * @author cmq
	 * @date 2015年2月4日 下午2:39:02
	 * @param width
	 * @param height
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] changeImageSize(int width, int height, Image src) throws IOException {
		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(src.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
		if (ImageIO.write(tag, "png", tempOutput)) {
			return tempOutput.toByteArray();
		}
		return null;
	}

	public static String getSuffix(String name) {
		return name.substring(name.lastIndexOf("."), name.length());
	}

	public static Folder readFolderOrder(File file) {
		Queue<File> fileQueue = new ArrayDeque<>();
		Queue<Folder> folderQueue = new ArrayDeque<>();
		Folder refolder = new Folder();
		refolder.setName(file.getName());
		refolder.setPath(file.getParent());
		fileQueue.offer(file);
		folderQueue.offer(refolder);
		while (!folderQueue.isEmpty()) {
			File parentFile = fileQueue.poll();
			Folder parentfolder = folderQueue.poll();
			String[] pathList = parentFile.list();
			for (int i = 0; i < pathList.length; i++) {
				String path = StringUtils.unite(parentFile.getPath(),
						File.separator, pathList[i]);
				File tmpFile = new File(path);
				if (tmpFile.isFile()) {
					parentfolder.addFile(readFileInfo(tmpFile));
				} else {
					fileQueue.offer(tmpFile);
					Folder tmpFolder = new Folder();
					tmpFolder.setName(tmpFile.getName());
					tmpFolder.setPath(tmpFile.getParent());
					parentfolder.addFolder(tmpFolder);
					folderQueue.offer(tmpFolder);
				}
			}
		}
		return refolder;
	}

	public static Folder readFolder(File file) {
		if (!file.isFile()) {
			Folder tmpfolder = new Folder();
			tmpfolder.setName(file.getName());
			tmpfolder.setPath(file.getParent());
			String[] names = file.list();
			for (int i = 0; i < names.length; i++) {
				String path = StringUtils.unite(file.getPath(), File.separator,
						names[i]);
				File tmpFile = new File(path);
				if (tmpFile.isFile()) {
					tmpfolder.addFile(readFileInfo(tmpFile));
				} else {
					tmpfolder.addFolder(readFolder(tmpFile));
				}
			}
			return tmpfolder;
		}
		return null;
	}

	public static FileInfo readFileInfo(File file) {
		if (file.isFile()) {
			FileInfo fileInfo = new FileInfo();
			try {
				FileInputStream is = new FileInputStream(file);
				byte[] bf = new byte[is.available()];
				is.read(bf);
				is.close();
				fileInfo.setName(file.getName());
				fileInfo.setPath(file.getParent());
				fileInfo.setData(bf);
				fileInfo.setChecksum(UUID.nameUUIDFromBytes(bf).toString());
				return fileInfo;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public static boolean writeFolder(String path, Folder folder) {
		try {
			String tmpPath = StringUtils.unite(path, File.separator,
					folder.getName());
			if (createFolder(tmpPath)) {
				List<FileInfo> fileInfoList = folder.getFileInfos();
				for (int i = 0; i < fileInfoList.size(); i++) {
					writeFileInfo(tmpPath, fileInfoList.get(i));
				}
				List<Folder> folderList = folder.getFolders();
				for (int i = 0; i < folderList.size(); i++) {
					writeFolder(tmpPath, folderList.get(i));
				}
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	public static boolean writeFileInfo(String path, FileInfo fileInfo) {
		try {
			String checksum = UUID.nameUUIDFromBytes(fileInfo.getData()).toString();
			if (fileInfo.getChecksum().equals(checksum)) {
				if (createFolder(path)) {
					path = StringUtils.unite(path, File.separator,fileInfo.getName());
					File file = new File(path);
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(fileInfo.getData());
					fos.close();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}
