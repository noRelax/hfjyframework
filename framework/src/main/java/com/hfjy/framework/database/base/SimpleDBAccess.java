package com.hfjy.framework.database.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.ResultInfo;
import com.hfjy.framework.logging.LoggerFactory;

public class SimpleDBAccess implements DBAccess {
	private static final Logger logger = LoggerFactory.getLogger(SimpleDBAccess.class);
	private DBConnectionInfo dbInfo = null;

	public SimpleDBAccess(DBConnectionInfo dbInfo) {
		this.dbInfo = dbInfo;
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

	@Override
	public boolean testExecute() {
		try (Connection conn = getConn()) {
			return !conn.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean execute(String sql) {
		return execute(sql, new Object[0]);
	}

	public boolean execute(String sql, Object... param) {
		boolean bl = false;
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			bl = ps.execute();
			showSQL(sql);
		} catch (Exception e) {
			showSQL(sql, e);
		}
		return bl;
	}

	public int executeSql(String sql) {
		return executeSql(sql, new Object[0]);
	}

	public int executeSql(String sql, Object... param) {
		int no = 0;
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			no = ps.executeUpdate();
			showSQL(RestoreSql(sql, param));
		} catch (Exception e) {
			showSQL(RestoreSql(sql, param), e);
		}
		return no;
	}

	public int[] executeBatchSql(String sql, List<Object[]> paramList) {
		int[] nos = new int[0];
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			if (paramList != null) {
				for (int i = 0; i < paramList.size(); i++) {
					setParam(ps, paramList.get(i));
					ps.addBatch();
				}
			}
			nos = ps.executeBatch();
			showSQL(sql);
		} catch (Exception e) {
			showSQL(sql, e);
		}
		return nos;
	}

	public int executeSqlGetId(String sql) {
		return executeSqlGetId(sql, new Object[0]);
	}

	public int executeSqlGetId(String sql, Object... param) {
		int no = -1;
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
			ps.executeUpdate();
			setParam(ps, param);
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				no = rs.getInt(1);
			}
			showSQL(sql);
		} catch (Exception e) {
			showSQL(sql, e);
		}
		return no;
	}

	public List<ResultInfo> executeListQuery(String sql) {
		return executeListQuery(sql, new Object[0]);
	}

	public List<ResultInfo> executeListQuery(String sql, Object... param) {
		List<ResultInfo> lr = new ArrayList<ResultInfo>();
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			ps.execute(sql);
			setResultInfo(lr, ps);
			showSQL(sql);
		} catch (Exception e) {
			showSQL(sql, e);
		}
		return lr;
	}

	public ResultInfo executeQuery(String sql) {
		return executeQuery(sql, new Object[0]);
	}

	public ResultInfo executeQuery(String sql, Object... param) {
		ResultInfo ri = new ResultInfo();
		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);) {
			setParam(ps, param);
			ResultSet rs = ps.executeQuery();
			ri.setUpdateNum(ps.getUpdateCount());
			ri.putResultSet(rs);
			showSQL(RestoreSql(sql, param));
		} catch (Exception e) {
			ri.setResultException(e);
			showSQL(RestoreSql(sql, param), e);
		}
		return ri;
	}

	public List<ResultInfo> executeProcedure(String spName) {
		return executeProcedure(spName, null, null);
	}

	public List<ResultInfo> executeProcedure(String spName, Object... in) {
		return executeProcedure(spName, in, null);
	}

	public List<ResultInfo> executeProcedure(String spName, Object[] in, Object[] out) {
		String sql = combineSpParam(spName, in, out);
		List<ResultInfo> lr = null;
		try (Connection conn = getConn(); CallableStatement cs = conn.prepareCall(sql);) {
			conn.setAutoCommit(false);
			cs.clearParameters();
			setSpParam(cs, in, out);
			cs.execute();
			setResultInfo(lr, cs);
			conn.commit();
			showSQL(RestoreSql(sql, in));
		} catch (Exception e) {
			showSQL(RestoreSql(sql, in), e);
		}
		return lr;
	}

	private String combineSpParam(String spName, Object[] spInParam, Object[] spOutParam) {
		int size = spInParam == null ? 0 : spInParam.length;
		size = spOutParam == null ? size : spInParam.length + spOutParam.length;
		StringBuilder sb = new StringBuilder();
		sb.append("{call ");
		sb.append(spName);
		for (int n = 0; n < size; n++) {
			if (n == 0) {
				sb.append("(");
			}
			sb.append("?");
			if (n < size - 1) {
				sb.append(",");
			} else {
				sb.append(")");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private String RestoreSql(String sql, Object[] pa) {
		if (pa == null || pa.length < 1) {
			return sql;
		}
		for (int i = 0; i < pa.length; i++) {
			sql = sql.replaceFirst("[?]", pa[i] == null ? "null" : "'" + pa[i] + "'");
		}
		return sql;
	}

	private void setParam(PreparedStatement ps, Object[] param) throws SQLException {
		if (param != null) {
			for (int i = 0; i < param.length; i++) {
				ps.setObject(i + 1, param[i]);
			}
		}
	}

	private void setSpParam(CallableStatement cs, Object[] spInParam, Object[] spOutParam) throws SQLException {
		int spInParamSize = spInParam == null ? 0 : spInParam.length;
		int spOutParamSize = spOutParam == null ? 0 : spOutParam.length;
		int size = spInParamSize + spOutParamSize;
		if (size > 0) {
			for (int i = 0; i < spInParamSize; i++) {
				cs.setObject(i + 1, spInParam[i]);
			}
			for (int i = spInParamSize; i < size; i++) {
				cs.registerOutParameter(i + 1, Types.OTHER);
			}
		}
	}

	private void setResultInfo(List<ResultInfo> lr, Statement st) throws SQLException {
		while (st.getMoreResults()) {
			ResultSet rs = st.getResultSet();
			if (rs != null) {
				ResultInfo ri = new ResultInfo();
				ri.putResultSet(rs);
				lr.add(ri);
			}
		}
	}

	private void showSQL(String sql, Exception e, Object... param) {
		logger.error(sql, e, param);
	}

	private void showSQL(String sql, Object... param) {
		logger.debug(sql, param);
	}
}