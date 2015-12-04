package com.hfjy.framework.database.entity;

import java.io.Serializable;
import java.util.Properties;

import com.hfjy.framework.common.util.StringUtils;

public class DBConnectionInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String dbCode; // 标记
	private DBType dbType;
	private String dbAddress;
	private String dbPort;
	private String dbName;
	private String driveClass; // 驱动类路径
	private String dbUsername; // 用户名
	private String dbPassword; // 密码
	private Integer maxConnectionNum; // 最大连接数
	private Integer minConnectionNum; // 最小连接数
	private String dbTestSQL; // 测试连接用SQL
	private String dbConnectionString; // 连接字符串

	public DBConnectionInfo() {
	};

	public DBConnectionInfo(String dbCode, Properties dbConfig) {
		this.dbCode = dbCode;
		this.dbType = DBType.valueOf(StringUtils.isEmpty(dbConfig.getProperty("dbType")) ? "MYSQL" : dbConfig.getProperty("dbType"));
		this.dbAddress = dbConfig.getProperty("dbAddress");
		this.dbPort = dbConfig.getProperty("dbPort");
		this.dbName = dbConfig.getProperty("dbName");
		this.driveClass = dbConfig.getProperty("driveClass");
		this.dbConnectionString = dbConfig.getProperty("dbConnectionString");
		this.dbConnectionString = dbConnectionString == null ? dbConfig.getProperty("dbconnectionString") : dbConnectionString;
		this.dbUsername = dbConfig.getProperty("dbUsername");
		this.dbPassword = dbConfig.getProperty("dbPassword");
		this.maxConnectionNum = Integer.valueOf(dbConfig.getProperty("maxConnectionNum"));
		this.minConnectionNum = Integer.valueOf(dbConfig.getProperty("minConnectionNum"));
		this.dbTestSQL = dbConfig.getProperty("dbTestSQL");
	}

	public String getDbCode() {
		return dbCode;
	}

	public void setDbCode(String dbCode) {
		this.dbCode = dbCode;
	}

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(DBType dbType) {
		this.dbType = dbType;
	}

	public String getDbAddress() {
		return dbAddress;
	}

	public void setDbAddress(String dbAddress) {
		this.dbAddress = dbAddress;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDriveClass() {
		return driveClass;
	}

	public void setDriveClass(String driveClass) {
		this.driveClass = driveClass;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public Integer getMaxConnectionNum() {
		return maxConnectionNum;
	}

	public void setMaxConnectionNum(Integer maxConnectionNum) {
		this.maxConnectionNum = maxConnectionNum;
	}

	public Integer getMinConnectionNum() {
		return minConnectionNum;
	}

	public void setMinConnectionNum(Integer minConnectionNum) {
		this.minConnectionNum = minConnectionNum;
	}

	public String getDbTestSQL() {
		return dbTestSQL;
	}

	public void setDbTestSQL(String dbTestSQL) {
		this.dbTestSQL = dbTestSQL;
	}

	public String getDbConnectionString() {
		return dbConnectionString;
	}

	public void setDbConnectionString(String dbConnectionString) {
		this.dbConnectionString = dbConnectionString;
	}

	public Properties toProperties() {
		Properties properties = new Properties();
		properties.put("dbCode", dbCode == null ? "" : dbCode);
		properties.put("dbType", dbType == null ? "" : dbType.toString());
		properties.put("dbAddress", dbAddress == null ? "" : dbAddress);
		properties.put("dbPort", dbPort == null ? "" : dbPort);
		properties.put("dbName", dbName == null ? "" : dbName);
		properties.put("driveClass", driveClass == null ? "" : driveClass);
		properties.put("dbUsername", dbUsername == null ? "" : dbUsername);
		properties.put("dbPassword", dbPassword == null ? "" : dbPassword);
		properties.put("maxConnectionNum", maxConnectionNum == null ? "" : maxConnectionNum.toString());
		properties.put("minConnectionNum", minConnectionNum == null ? "" : minConnectionNum.toString());
		properties.put("dbTestSQL", dbTestSQL == null ? "" : dbTestSQL);
		properties.put("dbConnectionString", dbConnectionString == null ? "" : dbConnectionString);
		return properties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.unite("dbCode", "=", dbCode == null ? "" : dbCode, "\r\n"));
		sb.append(StringUtils.unite("dbType", "=", dbType == null ? "" : dbType.toString(), "\r\n"));
		sb.append(StringUtils.unite("dbAddress", "=", dbAddress == null ? "" : dbAddress, "\r\n"));
		sb.append(StringUtils.unite("dbPort", "=", dbPort == null ? "" : dbPort, "\r\n"));
		sb.append(StringUtils.unite("dbName", "=", dbName == null ? "" : dbName, "\r\n"));
		sb.append(StringUtils.unite("driveClass", "=", driveClass == null ? "" : driveClass, "\r\n"));
		sb.append(StringUtils.unite("dbUsername", "=", dbUsername == null ? "" : dbUsername, "\r\n"));
		sb.append(StringUtils.unite("dbPassword", "=", dbPassword == null ? "" : dbPassword, "\r\n"));
		sb.append(StringUtils.unite("maxConnectionNum", "=", maxConnectionNum == null ? "" : maxConnectionNum, "\r\n"));
		sb.append(StringUtils.unite("minConnectionNum", "=", minConnectionNum == null ? "" : minConnectionNum, "\r\n"));
		sb.append(StringUtils.unite("dbTestSQL", "=", dbTestSQL == null ? "" : dbTestSQL, "\r\n"));
		sb.append(StringUtils.unite("dbConnectionString", "=", dbConnectionString == null ? "" : dbConnectionString));
		return sb.toString();
	}

	public void initConnectionString(String format) {
		if (StringUtils.isNotEmpty(dbAddress) && StringUtils.isNotEmpty(dbPort) && StringUtils.isNotEmpty(dbName)) {
			this.dbConnectionString = String.format(format, dbAddress, dbPort, dbName);
		}
	}

	@Override
	protected DBConnectionInfo clone() throws CloneNotSupportedException {
		return (DBConnectionInfo) super.clone();
	}
}
