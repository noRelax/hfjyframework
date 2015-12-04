package com.hfjy.framework.init;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.base.SQLProvider;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.DBScriptType;
import com.hfjy.framework.database.entity.DBType;
import com.hfjy.framework.database.entity.ResultInfo;

public class ConfigDbUtil extends SQLProvider {
	private static ConfigDbUtil configDbUtil;
	private final DBConnectionInfo dbInfo;

	private ConfigDbUtil(String dbPath) {
		dbInfo = new DBConnectionInfo();
		dbInfo.setDriveClass("org.sqlite.JDBC");
		dbInfo.setDbConnectionString(StringUtils.unite("jdbc:sqlite:", dbPath));
	};

	public static ConfigDbUtil init() {
		return configDbUtil;
	}

	public static ConfigDbUtil init(String dbPath) {
		if (configDbUtil == null) {
			configDbUtil = new ConfigDbUtil(dbPath);
			Initial.CONFIG_DB_INIT_OK = true;
		}
		return configDbUtil;
	}

	public String getTemplateText(String code, String[] data) {
		select("*");
		from("esb_text_message_formats");
		and("type = 'hfjy'");
		and("code = '" + code.toUpperCase() + "'");
		String text = null;
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			String[] keywords = resultInfo.getData(0, "keywords").toString().split(",");
			text = resultInfo.getData(0, "value").toString();
			if (keywords != null && keywords.toString().length() > 0) {
				if (data != null && data.length > 0) {
					for (int i = 0; i < data.length; i++) {
						text = text.replaceAll("[{]" + keywords[i] + "[}]", data[i]);
					}
				}
			}
		}
		return text;
	}

	public Properties getTemplateProperties(String code) {
		select("*");
		from("esb_text_message_formats");
		and("type = 'hfjy'");
		and("code = '" + code.toUpperCase() + "'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			String[] names = resultInfo.getColNames();
			Properties tmp = new Properties();
			for (int n = 0; n < names.length; n++) {
				String key = names[n];
				String value = resultInfo.getDataValue(0, key);
				setProperties(tmp, key, value);
			}
			return tmp;
		}
		return null;
	}

	public List<Properties> getAllProcess() {
		select("*");
		from("esb_process_config");
		and("type = 'hfjy'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			List<Properties> re = new ArrayList<>();
			String[] names = resultInfo.getColNames();
			for (int r = 0; r < resultInfo.getRowNum(); r++) {
				Properties tmp = new Properties();
				for (int n = 0; n < names.length; n++) {
					String key = names[n];
					String value = resultInfo.getDataValue(r, key);
					setProperties(tmp, key, value);
				}
				re.add(tmp);
			}
			return re;
		}
		return null;
	}

	public String getConfig(String code, String name) {
		String reValue = null;
		select("value");
		from("sys_parameter_list");
		and("type = 'sys'");
		and("code = ?", code);
		and("name = ?", name);
		ResultInfo resultInfo = myExecuteQuery(sql(), parameters());
		if (resultInfo.getRowNum() > 0) {
			return resultInfo.getDataValue(0, 0);
		}
		return reValue;
	}

	public Properties getSendConfig(String code) {
		select("b.name,b.value");
		from("sys_send_service_config a");
		leftJoin("esb_parameter_list b on a.type = b.type and a.code = b.code");
		and("a.type = 'hfjy'");
		and("a.code='" + code.toLowerCase() + "'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			Properties re = new Properties();
			for (int r = 0; r < resultInfo.getRowNum(); r++) {
				String key = resultInfo.getDataValue(r, "name");
				String value = resultInfo.getDataValue(r, "value");
				setProperties(re, key, value);
			}
			return re;
		}
		return null;
	}

	public List<Properties> getAllSendEsbConfig() {
		select("*");
		from("sys_send_service_config");
		and("type = 'hfjy'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			List<Properties> re = new ArrayList<>();
			for (int r = 0; r < resultInfo.getRowNum(); r++) {
				String[] names = resultInfo.getColNames();
				Properties tmp = new Properties();
				for (int n = 0; n < names.length; n++) {
					String key = names[n];
					String value = resultInfo.getDataValue(r, key);
					setProperties(tmp, key, value);
				}
				re.add(tmp);
			}
			return re;
		}
		return null;
	}

	public List<Properties> getAllReceiveServiceConfig() {
		select("*");
		from("esb_receive_service_config");
		and("type = 'hfjy'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			List<Properties> re = new ArrayList<>();
			String[] names = resultInfo.getColNames();
			for (int r = 0; r < resultInfo.getRowNum(); r++) {
				Properties tmp = new Properties();
				for (int n = 0; n < names.length; n++) {
					String key = names[n];
					String value = resultInfo.getDataValue(r, key);
					setProperties(tmp, key, value);
				}
				re.add(tmp);
			}
			return re;
		}
		return null;
	}

	public String getDefaultAchieve(Class<?> classInfo) {
		select("value");
		from("sys_default_achieve");
		and("type = 'hfjy'");
		and(StringUtils.unite("code='", classInfo.getSimpleName(), "'"));
		and("status = 'true'");
		ResultInfo resultInfo = myExecuteQuery(sql());
		if (resultInfo.getRowNum() > 0) {
			return resultInfo.getData(0, 0).toString();
		}
		return null;
	}

	public DBConnectionInfo getInitDBConnectionInfo(DBType dbType) {
		select("*");
		from("sys_jdbc_init_value");
		and("database_type = ?", dbType.toString().toLowerCase());
		ResultInfo ri = myExecuteQuery(sql(), parameters());
		DBConnectionInfo dbInfo = new DBConnectionInfo();
		dbInfo.setDbType(DBType.valueOf(ri.getData(0, "database_type").toString().toUpperCase()));
		dbInfo.setDriveClass(ri.getData(0, "drive_class").toString());
		dbInfo.setDbTestSQL(ri.getData(0, "test_sql").toString());
		dbInfo.setDbAddress(ri.getData(0, "address").toString());
		dbInfo.setDbPort(ri.getData(0, "port").toString());
		dbInfo.setDbName(ri.getData(0, "database_name").toString());
		dbInfo.setDbUsername(ri.getData(0, "user_name").toString());
		dbInfo.setDbPassword(ri.getData(0, "password").toString());
		dbInfo.setMaxConnectionNum(Integer.valueOf(ri.getData(0, "max_connection_num").toString()));
		dbInfo.setMinConnectionNum(Integer.valueOf(ri.getData(0, "mix_connection_num").toString()));
		dbInfo.initConnectionString(ri.getData(0, "connection_string").toString());
		dbInfo.setDbCode(new Date().getTime() + "");
		return dbInfo;
	}

	public String getConnectionStringFormat(DBType dbType) {
		select("*");
		from("sys_jdbc_init_value");
		and("database_type = ?", dbType.toString().toLowerCase());
		ResultInfo ri = myExecuteQuery(sql(), parameters());
		return ri.getData(0, "connection_string").toString();
	}

	public String getDBScript(DBType dbType, DBScriptType scriptType) {
		select("*");
		from("sys_sql_script");
		and("database_type = ?", dbType.toString().toLowerCase());
		and("script_type = ?", scriptType);
		ResultInfo ri = myExecuteQuery(sql(), parameters());
		return ri.getData(0, "sql").toString();
	}

	public ResultInfo getSysParameterList() {
		select("*");
		from("sys_parameter_list");
		return myExecuteQuery(sql());
	}

	public ResultInfo getSysAchieveList() {
		select("*");
		from("sys_default_achieve");
		return myExecuteQuery(sql());
	}

	public ResultInfo getSysSqlScriptList() {
		select("*");
		from("sys_sql_script");
		return myExecuteQuery(sql());
	}

	public ResultInfo getSysSendServiceList() {
		select("*");
		from("sys_send_service_config");
		return myExecuteQuery(sql());
	}

	public int executeSql(String sql) {
		return myExecuteSql(sql);
	}

	private Connection getConn() throws Exception {
		Class.forName(this.dbInfo.getDriveClass());
		Connection conn = null;
		if (StringUtils.isEmpty(dbInfo.getDbUsername())) {
			conn = DriverManager.getConnection(dbInfo.getDbConnectionString());
		} else {
			conn = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getDbUsername(), dbInfo.getDbPassword());
		}
		return conn;
	}

	private ResultInfo myExecuteQuery(String sql, Object... param) {
		ResultInfo ri = new ResultInfo();
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			ResultSet rs = ps.executeQuery();
			ri.setUpdateNum(ps.getUpdateCount());
			ri.putResultSet(rs);
		} catch (Exception e) {
			ri.setResultException(e);
		}
		return ri;
	}

	private int myExecuteSql(String sql, Object... param) {
		int no = 0;
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			no = ps.executeUpdate();
		} catch (Exception e) {
		}
		return no;
	}

	private void setParam(PreparedStatement ps, Object[] param) throws SQLException {
		if (param != null) {
			for (int i = 0; i < param.length; i++) {
				ps.setObject(i + 1, param[i]);
			}
		}
	}

	private void setProperties(Properties p, String key, String value) {
		if (StringUtils.isNotEmpty(key)) {
			p.put(key.toUpperCase().trim(), value);
			p.put(key.toLowerCase().trim(), value);
			p.put(key.trim(), value);
		}
	}
}