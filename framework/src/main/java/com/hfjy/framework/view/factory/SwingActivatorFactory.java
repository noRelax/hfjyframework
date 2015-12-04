package com.hfjy.framework.view.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hfjy.framework.view.entity.ExecuteRunnable;

public class SwingActivatorFactory {
	private static final ExecutorService eventConveyor = Executors.newCachedThreadPool();

	public static void run(ExecuteRunnable runnable) {
		eventConveyor.execute(runnable);
	}
}
