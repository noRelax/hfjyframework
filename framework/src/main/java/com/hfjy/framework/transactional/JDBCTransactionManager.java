package com.hfjy.framework.transactional;

import com.hfjy.framework.database.base.DatabaseTools;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.transactional.entity.TransactionalMark;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

public class JDBCTransactionManager implements TransactionManager {
	private static Logger logger = LoggerFactory.getLogger(JDBCTransactionManager.class);
	private static ThreadLocal<Deque<TransactionalMark>> localTransactionalGroup = new ThreadLocal<>();
	private static ThreadLocal<Set<String>> localMarkSet = new ThreadLocal<>();
	private static Map<Connection, Savepoint> savepointMap = new HashMap<>();

	public final void commit() throws SQLException {
		if (localTransactionalGroup.get() != null) {
			if (localTransactionalGroup.get().size() == 1) {
				DatabaseTools.setMySessionAutoCommit(false);
				Iterator<String> connectionPoolNames = localMarkSet.get().iterator();
				while (connectionPoolNames.hasNext()) {
					String mark = connectionPoolNames.next();
					savepointMap.remove(DatabaseTools.getDBSession(mark).getConnection());
					DatabaseTools.getDBSession(mark).commit();
				}
			} else {
				if (localTransactionalGroup.get().peekFirst().isInsure()) {
					String[] marks = localTransactionalGroup.get().peekFirst().getMark();
					for (int i = 0; i < marks.length; i++) {
						setSavepoint(DatabaseTools.getDBSession(marks[i]).getConnection());
					}
				}
			}
		}
	}

	public final void rollback() throws SQLException {
		if (localTransactionalGroup.get() != null) {
			DatabaseTools.setMySessionAutoCommit(false);
			Iterator<String> connectionPoolNames = localMarkSet.get().iterator();
			while (connectionPoolNames.hasNext()) {
				String mark = connectionPoolNames.next();
				Connection conn = DatabaseTools.getDBSession(mark).getConnection();
				if (savepointMap.get(conn) != null) {
					conn.rollback(savepointMap.get(conn));
				} else {
					DatabaseTools.getDBSession(mark).rollback();
				}
				savepointMap.remove(DatabaseTools.getDBSession(mark).getConnection());
				DatabaseTools.getDBSession(mark).commit();
			}
			localTransactionalGroup.get().clear();
		}
	}

	public final void close() throws SQLException {
		if (localTransactionalGroup.get() != null) {
			localTransactionalGroup.get().pollFirst();
			if (localTransactionalGroup.get().isEmpty()) {
				DatabaseTools.setMySessionAutoCommit(false);
				Iterator<String> connectionPoolNames = localMarkSet.get().iterator();
				while (connectionPoolNames.hasNext()) {
					String mark = connectionPoolNames.next();
					DatabaseTools.destroyDBSession(mark);
				}
				localTransactionalGroup.remove();
				localMarkSet.remove();
			}
		}
	}

	public final void initTransaction(TransactionalMark transactional) {
		DatabaseTools.setMySessionAutoCommit(transactional.isAutoCommit());
		if (!transactional.isAutoCommit()) {
			try {
				String[] names = transactional.getMark();
				for (int i = 0; i < names.length; i++) {
					Connection conn = DatabaseTools.getDBSession(names[i]).getConnection();
					DatabaseMetaData metaData = conn.getMetaData();
					if (metaData.supportsTransactions()) {
						conn.setAutoCommit(false);
						setLevel(transactional.getLevel(), conn);
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		addTransaction(transactional);
	}

	private void addTransaction(TransactionalMark transactional) {
		if (localTransactionalGroup.get() == null) {
			localTransactionalGroup.set(new ArrayDeque<TransactionalMark>());
			localMarkSet.set(new HashSet<String>());
		}
		localTransactionalGroup.get().offerFirst(transactional);
		localMarkSet.get().addAll(Arrays.asList(transactional.getMark()));
	}

	private void setSavepoint(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		if (metaData.supportsSavepoints()) {
			if (savepointMap.get(connection) != null) {
				connection.releaseSavepoint(savepointMap.get(connection));
			}
			savepointMap.put(connection, connection.setSavepoint());
		}
	}

	private void setLevel(TransactionalLevel level, Connection connection) throws SQLException {
		int levelValue = Connection.TRANSACTION_READ_UNCOMMITTED;
		switch (level) {
		case LEVEL1:
			levelValue = Connection.TRANSACTION_SERIALIZABLE;
			break;
		case LEVEL2:
			levelValue = Connection.TRANSACTION_REPEATABLE_READ;
			break;
		case LEVEL3:
			levelValue = Connection.TRANSACTION_READ_COMMITTED;
			break;
		case LEVEL4:
			levelValue = Connection.TRANSACTION_READ_UNCOMMITTED;
			break;
		case LEVEL5:
			levelValue = Connection.TRANSACTION_NONE;
			break;
		}
		if (connection.getMetaData().supportsTransactionIsolationLevel(levelValue)) {
			connection.setTransactionIsolation(levelValue);
		}
	}
}