package io.github.zabbix.nginx.monitor;

public class Main {

	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			throw new RuntimeException("Required parameter (0) url and (1) data value");
		}

		String url = args[0];
		String value = args[1];
		Monitor monitor = new Monitor(url);
		
		//Print to system out the requested value
		try {
			switch (value) {
			case Monitor.VALUE_ACTIVE_CONNECTIONS:
				System.out.print(monitor.getActiveConnections());
				break;
			case Monitor.VALUE_REQUESTS:
				System.out.print(monitor.getRequests());
				break;
			case Monitor.VALUE_ACCEPTED_CONNECTIONS:
				System.out.print(monitor.getAcceptedConnections());
				break;
			case Monitor.VALUE_HANDLED_CONNECTIONS:
				System.out.print(monitor.getHandledConnections());
				break;
			case Monitor.VALUE_READING_CONNECTIONS:
				System.out.print(monitor.getReadingConnections());
				break;
			case Monitor.VALUE_WAITING_CONNECTIONS:
				System.out.print(monitor.getWaitingConnections());
				break;
			case Monitor.VALUE_WRITING_CONNECTIONS:
				System.out.print(monitor.getWritingConnections());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			//By convenience, returns a numeric value
			System.out.print("0");
		}
	}
}
