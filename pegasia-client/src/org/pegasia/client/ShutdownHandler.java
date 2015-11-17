package org.pegasia.client;

import org.pegasia.util.SleepTicker;

public final class ShutdownHandler {
	final int STOP_TIME_LIMIT = 20,
			ABORT_TIME_LIMIT = 120;
	
	private final Pegasia client;
	private final Thread hook;
	private volatile boolean started = false, forced = false;
	
	public ShutdownHandler(Pegasia client) throws InstantiationException {
		if (client == null)
			throw new InstantiationException();
		this.client = client;
		
		hook = null;
	}
	
	public void start() {
		
	}
	
	public boolean hasStarted() {
		return started;
	}
	
	public void forceExit() {
		forced = true;
	}
	
	private class ShutdownTicker extends SleepTicker implements Runnable {
		
				
		@Override
		public void run() {
			if (started)
				return;
			started = true;
			
			if (!forced)
				Runtime.getRuntime().removeShutdownHook(hook);
		}

		@Override
		protected boolean tick(int ticksRemaining) {
			return true;
		}
	}
	
	private class ShutdownProcess implements Runnable {
		@Override
		public void run() {
			client.getUIManager().shutdown();
			client.getPluginManager().shutdown();
		}
	}
}