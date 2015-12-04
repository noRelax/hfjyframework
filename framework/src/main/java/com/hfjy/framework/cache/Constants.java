/**
 * 海风在线学习平台
 * @Title: Constants.java 
 * @Package: com.hyphen.util
 * @author: cloud
 * @date: 2014年5月5日-上午11:38:16
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.cache;

import java.io.File;

import com.hfjy.framework.init.Initial;

/**
 * @ClassName: Constants
 * @Description: 常量类
 * @author cloud
 * @date 2014年5月5日 上午11:38:16
 * 
 */
public class Constants {
	/** 配置文件 */
	public final static String JOB_CONFIG_FILE = Initial.SYSTEM_CONFIG_PATH + "job" + File.separator + "jobmission.xml";
	public final static String OSS_CONFIG_FILE = Initial.SYSTEM_CONFIG_PATH + "file" + File.separator + "oss.properties";
	public final static String LQL_CONFIG_FILE = Initial.SYSTEM_CONFIG_PATH + "file" + File.separator + "lql.properties";
	public final static String IMG_FOUND_CONFIG_FILE = Initial.SYSTEM_CONFIG_PATH + "phantomjs" + File.separator + "imgfoundConfig.properties";
	public final static String HLS_CONFIG_FILE = Initial.SYSTEM_CONFIG_PATH + "nts" + File.separator + "hls.properties";

}
