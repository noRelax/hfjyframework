package com.hfjy.framework.transactional;

import java.sql.SQLException;

import com.hfjy.framework.transactional.entity.TransactionalMark;

public interface TransactionManager {

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void close() throws SQLException;

	void initTransaction(TransactionalMark transactional);
}
