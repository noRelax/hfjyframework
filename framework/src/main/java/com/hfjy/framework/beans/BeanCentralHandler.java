package com.hfjy.framework.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;

import com.hfjy.framework.cache.CacheHandleResultManager;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.transactional.JDBCTransactionManager;
import com.hfjy.framework.transactional.TransactionManager;
import com.hfjy.framework.transactional.Transactional;
import com.hfjy.framework.transactional.entity.TransactionalMark;

public class BeanCentralHandler implements InvocationHandler {
	private static Logger logger = LoggerFactory.getLogger(BeanCentralHandler.class);
	private TransactionManager jdbcTransactionManager = new JDBCTransactionManager();
	private CacheHandleResultManager cacheHandleResultManager = new CacheHandleResultManager();
	private BeanInfo execBean;

	public BeanCentralHandler(BeanInfo object) {
		this.execBean = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		execBean.initFields();
		logger.debug(StringUtils.unite(execBean.getObject().getClass().getName(), " the ", method.getName(), " method begin "));
		Method originalMethod = execBean.getMethod(ClassUtil.getMethodKey(method));
		Object result = cacheHandleResultManager.getHandleResult(originalMethod, args);
		if (result == null) {
			Transactional transactional = originalMethod.getAnnotation(Transactional.class);
			TransactionalMark transactionalMark = new TransactionalMark(transactional);
			if (execBean.getDbNames().length > 0) {
				transactionalMark.setMark(execBean.getDbNames());
			}
			jdbcTransactionManager.initTransaction(transactionalMark);
			long begin = System.currentTimeMillis();
			try {
				result = method.invoke(execBean.getObject(), args);
				jdbcTransactionManager.commit();
			} catch (Throwable e) {
				jdbcTransactionManager.rollback();
				throw e;
			} finally {
				logger.debug(StringUtils.unite(execBean.getObject().getClass().getName(), " the ", method.getName(), " method end ", " with time ", (System.currentTimeMillis() - begin), " ms"));
				jdbcTransactionManager.close();
				cacheHandleResultManager.setHandleResult(originalMethod, args, result);
			}
		}
		return result;
	}
}
