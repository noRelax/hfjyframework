/**
 * 海风在线学习平台
 * @Title: ToolUtils.java 
 * @Package: com.hyphen.util
 * @author: cloud
 * @date: 2014年5月5日-上午11:37:04
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

/**
 * @ClassName: ToolUtils
 * @Description: TODO(工具类)
 * @author cloud
 * @date 2014年5月5日 上午11:37:04
 * 
 */
public class ToolUtils {
	private static final Logger logger = LoggerFactory.getLogger(ToolUtils.class);

	public static Process exec(String exeName) {
		Process tmp = null;
		try {
			tmp = Runtime.getRuntime().exec(exeName);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return tmp;
	}

	/**
	 * @Title cloneObject
	 * @Description 深度克隆对象(被克隆对象必须实现Serializable接口)
	 * @param obj
	 * @return object
	 * @throws Exception
	 */
	public static Object cloneObject(Object obj) throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(obj);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		return in.readObject();
	}

	/**
	 * @Title isEmpty
	 * @Description 验证Collection是否为空
	 * @param collection
	 * @return Boolean
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty() ? true : false;
	}

	/**
	 * @Title isEmpty
	 * @Description 验证map是否为空
	 * @param map
	 * @return Boolean
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map != null && !map.isEmpty() ? true : false;
	}

	/**
	 * @Title getUUID
	 * @Description 获得一个UUID (去掉“-”符号)
	 * @return UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.replace("-", "");
	}

	/**
	 * @Title verifyPhone
	 * @Description 验证手机号码是否合法
	 * @return boolean
	 */
	public static boolean verifyPhone(String phone) {
		String regExp = "^[1][0-9]{10}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phone);
		return m.find();
	}
	/**
	 * @desc: 是否是手机号码 ：比上一个做了更严格的校验
	 *  手机号格式： 13[0-9]       15[0-9]  18[0,5-9]   /
	 *  中国3G号码段:中国联通185,186;中国移动188,187;中国电信189,180共6个号段。
		 中国移动：134-139、150-152、157-159、182-184、187-188;18个号段
		 中国联通：130-132、155-156、185-186;7个号段
		 中国电信：133、153、180-181、189;5个号码段
		 移动:
		     2G号段(GSM网络)有139,138,137,136,135,134(0-8),159,158,152,151,150
		     3G号段(TD-SCDMA网络)有157,182,183,184,188,187
		     147是移动TD上网卡专用号段.
		 联通:
		     2G号段(GSM网络)有130,131,132,155,156
		     3G号段(WCDMA网络)有186,185
		     145为联通3G上网卡
		     155为联通如意通号段
		     156为世界风号段
		     186为沃品牌3G号段
		 电信:
		     2G号段(CDMA网络)有133,153
		     3G号段(CDMA网络)有180,181,189
		8-31号：陈佳要求 新增两个号码段：14 和 17开头的。
	 * @author: spinach
	 * @Date:2015年6月15日
	 * @Title:isMobileNO
	 */
	public static boolean verifyMobileNO(String mobiles){  
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");  
		Matcher m = p.matcher(mobiles);  
		return m.find();
	}

	/**
	 * @Title verifyEmail
	 * @Description 验证邮箱是否合法
	 * @return boolean
	 */
	public static boolean verifyEmail(String email) {
		if (email == null)
			return false;
		//leo-zeng 正则表达式 中添加一些特殊邮箱校验比如 ivar__zhang@163.com 这种邮箱
		String regExp = "^([a-z0-9A-Z]+[-_|\\.]*?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(email);
		return m.find();
	}
	
	/**
	 * @Title verifyQq
	 * @Description 验证QQ号是否合法
	 * @return boolean
	 */
	public static boolean verifyQq(String qq) {
		if (qq == null)
			return false;
		String regExp = "^[1-9][0-9]{4,}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(qq);
		return m.find();
	}

	/**
	 * @Title verifyMoney
	 * @Description 验证金钱是否合法
	 * @return boolean
	 */
	public static boolean verifyMoney(String money) {
		if (money == null)
			return false;
		String regExp = "^[-+]?(\\d+(.\\d*)?|/.\\d+)[dD]?$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(money);
		return m.find();
	}

	/**
	 * 
	 * @Title: verifyInt(验证整数)
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param it
	 * @return boolean
	 * @exception
	 * @since 1.0
	 */
	public static boolean verifyInt(String it) {
		if (it == null)
			return false;
		String regExp = "^\\+?[1-9][0-9]*$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(it);
		return m.find();
	}

	/**
	 * 
	 * @Title: 创建试卷序号（日期+序号，16位字符）
	 * @param sequence
	 *            数据库序列
	 * @return String 试卷序号
	 * @since 1.0
	 */
	public static String createTestPaperNo(String sequence) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return appendZero(sequence, 6).insert(0, sdf.format(new Date())).toString();
	}

	/**
	 * 
	 * @Title: 字符位数不够前面补零
	 * @param text
	 *            字符串
	 * @param len
	 *            长度
	 * @since 1.0
	 */
	public static StringBuffer appendZero(String text, int len) {
		StringBuffer sb = new StringBuffer(text);
		while (sb.length() < len) {
			sb.insert(0, "0");
		}
		return sb;
	}

	/**
	 * 
	 * @Title: randoms
	 * @Description: 将一个整型数组 重新排序后输出
	 * @param old
	 * @return int[]
	 * @exception
	 * @since 1.0
	 */
	public static int[] randoms(int[] old) {
		Random r = new Random();
		int temp1, temp2;
		int len = old.length;
		int returnValue[] = new int[old.length];
		for (int i = 0; i < old.length; i++) {
			temp1 = Math.abs(r.nextInt()) % len;
			returnValue[i] = old[temp1];
			temp2 = old[temp1];
			old[temp1] = old[len - 1];
			old[len - 1] = temp2;
			len--;
		}
		return returnValue;
	}

	public static String safeHtmlValue(String str) {

		return StringUtils.isEmpty(str) ? str : str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;").replace("=", "&3D;");
	}

	/**
	 * @Title: contains(判断数组中是否包含某个元素
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param array
	 * @param a
	 * @return boolean
	 * @exception
	 * @since 1.0
	 */
	public static boolean contains(int[] array, int a) {
		for (int e : array)
			if (e == a)
				return true;

		return false;
	}

	/**
	 * @Title: htmlTagEscape
	 * @Description:对html标签进行转义
	 * @param src
	 * @return String
	 * @exception
	 * @since 1.0
	 */
	public static String htmlTagEscape(String src) {
		if (src == null) {
			return "";
		}
		char content[] = new char[src.length()];
		src.getChars(0, src.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '“':
				result.append("&quot;");
				break;
			default:
				result.append(content[i]);
				break;
			}
		}
		return result.toString();
	}

	/**
	 * @Title: checkArraysIsHas(比较两个字符串数据是否相等)
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param str
	 * @param tempStr
	 * @param tag
	 *            字符串分隔符
	 * @return boolean
	 * @exception
	 * @since 1.0
	 */
	public static boolean checkArraysIsHas(String str, String tempStr, String tag) {
		String[] split1 = str.split(tag);
		String[] split2 = tempStr.split(tag);
		List<String> strList = Arrays.asList(split1);
		List<String> tempStrList = Arrays.asList(split2);
		return strList.containsAll(tempStrList);
	}

	/**
	 * @Title: strArrayToIntArray(字符串数组转换成Integer数组)
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param strs
	 * @return Integer[]
	 * @exception
	 * @since 1.0
	 */
	public static Integer[] strArrayToIntArray(String[] strs) {
		Integer[] data = new Integer[strs.length];
		for (int i = 0; i < strs.length; i++) {
			data[i] = Integer.parseInt(strs[i]);
		}
		return data;
	}

	/**
	 * 验证年份
	 * 
	 * @param years
	 * @return
	 */
	public static boolean verifyYear(String years) {
		if (years == null)
			return false;
		String regExp = "^[1-9][0-9][0-9][0-9]$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(years);
		return m.find();
	}
	
	/**
	 * @desc: 决断字符串str是否全部是中文
	 * @author: spinach
	 * @Date:2015年6月15日
	 * @Title:checkIsChinese
	 */
	public static boolean verifyChinese(String str) {
			if(StringUtils.isEmpty(str)) return false;
	        String s=new String(str.getBytes());//用GBK编码
	        String pattern="^[\u4e00-\u9fa5]+$";   // 包含用这个：[\u4e00-\u9fa5]+
	        Pattern p=Pattern.compile(pattern);  
	        Matcher result=p.matcher(s);                  
	        return  result.find();
	}
	public static void main(String[] args) {
		boolean isphone = verifyMobileNO("1220000000");
		System.out.println(isphone);
		boolean aa = verifyDigital("111111");
		System.out.println(aa);
	}

	public static boolean verifyDigital(String digital) {
		if(StringUtils.isEmpty(digital)) return false;
        String regExp = "^[1-9][0-9]+$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(digital);
		return m.find();
	}
	
	
	/**
	 * 验证ADID_no
	 * @author yjzhan@hfjy.com
	 * @date 2015年8月6日 上午11:17:45 
	 * @param adid_no
	 * @return
	 */
	public static boolean verifyAdid(String adid_no) {
		if (adid_no == null)
			return false;
		String regExp = "^[A-Za-z0-9]{0,50}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(adid_no);
		return m.find();
	}
}
