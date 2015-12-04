package com.hfjy.framework.init;

import java.io.File;

import com.hfjy.framework.cache.CacheAccess;
import com.hfjy.framework.common.util.ConvertUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.base.DBConnectionPool;
import com.hfjy.framework.database.base.DBSessionFactory;
import com.hfjy.framework.database.nosql.DataAccess;

public class Initial {
	/** 配置文件目录 */
	public final static String SYSTEM_CONFIG_PATH = StringUtils.unite(System.getProperty("user.home"), File.separator, "configs", File.separator);

	/** 数据库配置文件目录 */
	public final static String DB_CONFIG_PATH = StringUtils.unite(SYSTEM_CONFIG_PATH, "db", File.separator);

	/** 配置文件 */
	public final static String CONFIG_DB_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "frameworkConfig.db");
	public final static String CONFIG_H2_DB_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "frameworkConfig");
	public final static String DB_CONFIG_FILE = StringUtils.unite(DB_CONFIG_PATH, "DBsConfig.properties");
	public final static String LOG_CONFIG_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "log", File.separator, "logback.xml");
	public final static String JEDIS_CONFIG_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "cache", File.separator, "jedis.properties");
	public final static String MONGODB_CONFIG_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "cache", File.separator, "mongodb.properties");
	public final static String XMEMCACHED_CONFIG_FILE = StringUtils.unite(SYSTEM_CONFIG_PATH, "cache", File.separator, "xmemcached.properties");

	/** 初始化参数 */
	public static boolean CONFIG_DB_INIT_OK = false;
	public static boolean CACHE_ACCESS_INIT_OK = false;
	public final static boolean SYSTEM_IS_DEBUG = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "isDebug"), true);
	public final static boolean SYSTEM_IS_ALL_STATIC = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "isAllStatic"), false);
	public final static boolean SYSTEM_IS_ALL_STATIC_LOCAL = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "isAllStaticLocal"), true);
	public final static String SYSTEM_DEFAULT_CHARSET = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "charset"), "UTF-8");
	public final static String SYSTEM_DEFAULT_DATE_FORMAT = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "dateFormat"), "yyyy-MM-dd HH:mm:ss");
	public final static String SYSTEM_SHOW_DATE_FORMAT = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "showDateFormat"), "yyyy年MM月dd日 HH时mm分ss秒");
	public final static String SYSTEM_DEFAULT_TIME_ZONE = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "timeZone"), "ETC/GMT-8");
	public final static Integer SYSTEM_DEFAULT_WAIT_TIMEOUT = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "timeout"), 10000);
	public final static String DB_CONFIG_DEFAULT_KEY = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "configDefaultKey"), "DEFAULT");
	public final static String SQL_DEFAULT_EXTERNAL_DB_ACHIEVE = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "sqlDefaultExternalAchieve"), "com.hfjy.framework.database.nosql.JDBCDataAccessObject");
	public final static String SQL_DEFAULT_EXTERNAL_TABLE_NAME = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getConfig("default", "sqlDefaultExternalTable"), "zoo_sql_store");

	public final static String SYSTEM_ACHIEVE_CACHE_ACCESS = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getDefaultAchieve(CacheAccess.class), "com.hfjy.framework3rd.cache.RedisAccess");
	public final static String SYSTEM_ACHIEVE_DB_CONNECTION_POOL = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getDefaultAchieve(DBConnectionPool.class), "com.hfjy.framework.database.base.SimpleKeepDBConnectionPool");
	public final static String SYSTEM_ACHIEVE_DB_SESSION_FACTORY = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getDefaultAchieve(DBSessionFactory.class), "com.hfjy.framework.database.base.SimpleDBSessionFactory");
	public final static String SYSTEM_ACHIEVE_DATA_ACCESS = initValue(ConfigDbUtil.init(CONFIG_DB_FILE).getDefaultAchieve(DataAccess.class), "com.hfjy.framework3rd.database.mongodb.MongodbDataAccess");

	@SuppressWarnings("unchecked")
	private static <T> T initValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			Object tmp = defaultValue;
			if (defaultValue instanceof Boolean) {
				if (value.toString().trim().equalsIgnoreCase("true")) {
					tmp = true;
				} else {
					tmp = false;
				}
			} else if (defaultValue instanceof Integer) {
				tmp = ConvertUtil.toIt(value, Integer.class);
			}
			return (T) tmp;
		}
	}
}
