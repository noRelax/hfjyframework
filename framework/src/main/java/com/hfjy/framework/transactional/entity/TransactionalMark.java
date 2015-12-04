package com.hfjy.framework.transactional.entity;

import com.hfjy.framework.init.Initial;
import com.hfjy.framework.transactional.Transactional;
import com.hfjy.framework.transactional.TransactionalLevel;

public class TransactionalMark {
	private String[] mark;
	private TransactionalLevel level;
	private boolean autoCommit;
	private boolean insure;

	public TransactionalMark(Transactional transactional) {
		if (transactional == null) {
			this.mark = new String[] { Initial.DB_CONFIG_DEFAULT_KEY };
			this.level = TransactionalLevel.LEVEL4;
			this.autoCommit = false;
			this.insure = false;
		} else {
			this.mark = transactional.mark();
			this.level = transactional.level();
			this.autoCommit = transactional.autoCommit();
			this.insure = transactional.insure();
		}
	}

	public String[] getMark() {
		return mark;
	}

	public void setMark(String[] mark) {
		this.mark = mark;
	}

	public TransactionalLevel getLevel() {
		return level;
	}

	public void setLevel(TransactionalLevel level) {
		this.level = level;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isInsure() {
		return insure;
	}

	public void setInsure(boolean insure) {
		this.insure = insure;
	}
}
