package com.hfjy.framework.view.entity;

import java.util.EventObject;

public abstract class ExecuteRunnable implements Runnable {

	protected final EventObject eventObject;

	public ExecuteRunnable(final EventObject eventObject) {
		this.eventObject = eventObject;
	}

	@Override
	public void run() {
		process();
	}

	public abstract void process();
}
