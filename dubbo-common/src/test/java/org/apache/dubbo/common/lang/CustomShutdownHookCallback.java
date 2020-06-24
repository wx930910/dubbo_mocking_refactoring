package org.apache.dubbo.common.lang;

public class CustomShutdownHookCallback implements ShutdownHookCallback {

	private boolean executed = false;

	@Override
	public void callback() throws Throwable {
		executed = true;
	}

	public boolean isExecuted() {
		return executed;
	}

}
