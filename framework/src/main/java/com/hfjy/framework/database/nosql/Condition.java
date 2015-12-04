package com.hfjy.framework.database.nosql;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.hfjy.framework.beans.entity.AbstractModel;
import com.hfjy.framework.beans.entity.Entity;
import com.hfjy.framework.common.util.StringUtils;

public class Condition implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Condition> conditions;
	private String element;
	private FactorType factor;
	private Object value;

	private Condition() {
	}

	public static Condition init() {
		Condition tmp = new Condition();
		tmp.conditions = new ArrayList<>();
		return tmp;
	}

	public Condition is(AbstractModel model) {
		modelToCondition(model, FactorType.EQUAL);
		return this;
	}

	public Condition is(String element, Object value) {
		return addCondition(element, value, FactorType.EQUAL);
	}

	public Condition notIs(AbstractModel model) {
		modelToCondition(model, FactorType.NOT_EQUAL);
		return this;
	}

	public Condition notIs(String element, Object value) {
		return addCondition(element, value, FactorType.NOT_EQUAL);
	}

	public Condition gt(String element, Object value) {
		return addCondition(element, value, FactorType.GREATER_THAN);
	}

	public Condition gte(String element, Object value) {
		return addCondition(element, value, FactorType.GREATER_THAN_EQUAL);
	}

	public Condition lt(String element, Object value) {
		return addCondition(element, value, FactorType.LESS_THAN);
	}

	public Condition lte(String element, Object value) {
		return addCondition(element, value, FactorType.LESS_THAN_EQUAL);
	}

	public Condition like(String element, String regex) {
		return addCondition(element, regex, FactorType.LIKE);
	}

	public Condition in(String element, Object... values) {
		return addCondition(element, values, FactorType.IN);
	}

	public Condition in(String element, Iterable<Object> values) {
		return addCondition(element, values, FactorType.IN);
	}

	public Condition notIn(String element, Object... values) {
		return addCondition(element, values, FactorType.NOT_IN);
	}

	public Condition notIn(String element, Iterable<Object> values) {
		return addCondition(element, values, FactorType.NOT_IN);
	}

	public Condition or(Condition condition) {
		try {
			Condition clone = condition.clone();
			clone.factor = FactorType.OR;
			conditions.add(clone);
		} catch (Exception e) {
		}
		return this;
	}

	public Condition not(Condition condition) {
		try {
			Condition clone = condition.clone();
			clone.factor = FactorType.NOT;
			conditions.add(clone);
		} catch (Exception e) {
		}
		return this;
	}

	public String getElement() {
		return element;
	}

	public FactorType getFactor() {
		return factor;
	}

	public Object getValue() {
		return value;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public String toString() {
		return StringUtils.unite(element, " [", factor, "] ", value);
	}

	private Condition addCondition(String element, Object value, FactorType factor) {
		Condition condition = new Condition();
		condition.element = element;
		condition.factor = factor;
		condition.value = value;
		condition.conditions = conditions;
		conditions.add(condition);
		return this;
	}

	private void modelToCondition(AbstractModel model, FactorType factor) {
		Class<?> classInfo = model.getClass();
		Field[] fields = classInfo.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals("serialVersionUID")) {
				continue;
			}
			fields[i].setAccessible(true);
			Object value = null;
			try {
				value = fields[i].get(model);
			} catch (Exception e) {
			}
			if (value != null) {
				Entity entity = fields[i].getAnnotation(Entity.class);
				if (entity != null) {
					addCondition(entity.columnName(), value, factor);
				} else {
					addCondition(fields[i].getName(), value, factor);
				}
			}
		}
	}

	@Override
	public Condition clone() throws CloneNotSupportedException {
		Condition condition = Condition.init();
		condition.conditions.addAll(conditions);
		condition.element = element;
		condition.factor = factor;
		condition.value = value;
		return condition;
	}
}