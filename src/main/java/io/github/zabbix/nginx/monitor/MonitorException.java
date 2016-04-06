package io.github.zabbix.nginx.monitor;

public class MonitorException extends Exception {
	private static final long serialVersionUID = 1L;

	public MonitorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MonitorException(String message) {
		super(message);
	}
	
}
