package com.hfjy.framework.biz;

import com.hfjy.framework.beans.BeanManager;

public class BaseBean {

	public BaseBean() {
		BeanManager.registrationClass(this);
	}
}
