package io.github.zabbix.nginx.monitor.test;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import io.github.zabbix.nginx.monitor.Monitor;

public class MonitorTest {
	
	@Test
	public void doTest() throws Exception{
		URL url = MonitorTest.class.getResource("/nginx_status.html");
		Monitor monitor = new Monitor(url.toString());
		
		Assert.assertEquals("Wrong active connections", 111, monitor.getActiveConnections());
		Assert.assertEquals("Wrong accepts connections", 29, monitor.getAcceptedConnections());
		Assert.assertEquals("Wrong handled connections", 30, monitor.getHandledConnections());
		Assert.assertEquals("Wrong requests", 2009, monitor.getRequests());
		Assert.assertEquals("Wrong reading connections", 9, monitor.getReadingConnections());
		Assert.assertEquals("Wrong writing connections", 10, monitor.getWritingConnections());
		Assert.assertEquals("Wrong waiting connections", 11, monitor.getWaitingConnections());
	}

}
