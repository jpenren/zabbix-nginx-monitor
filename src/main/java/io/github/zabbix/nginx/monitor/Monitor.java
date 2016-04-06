package io.github.zabbix.nginx.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Monitor {
	public static final String VALUE_ACTIVE_CONNECTIONS = "connections";
	public static final String VALUE_REQUESTS = "requests";
	public static final String VALUE_ACCEPTED_CONNECTIONS = "accepts";
	public static final String VALUE_HANDLED_CONNECTIONS = "handled";
	public static final String VALUE_READING_CONNECTIONS = "reading";
	public static final String VALUE_WRITING_CONNECTIONS = "writing";
	public static final String VALUE_WAITING_CONNECTIONS = "waiting";

	private static final int ACCEPTS = 1;
	private static final int HANDLED = 2;
	private static final int REQUESTS = 3;
	private static final int READING = 1;
	private static final int WRITING = 2;
	private static final int WAITING = 3;
	private static final String TEMP_FILE = (System.getProperty("java.io.tmpdir") + File.separator + "nginx-status.tmp").replace("//", "/");
	private final String statusUrl;

	public Monitor(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public int getActiveConnections() throws MonitorException {
		String status = getStatus();
		String regex = "Active connections: (\\d*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(status);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}

		throw new MonitorException("Error parsing active connections data");
	}

	public long getRequests() throws MonitorException {
		return parseConnectionsData(REQUESTS);
	}

	public long getAcceptedConnections() throws MonitorException {
		return parseConnectionsData(ACCEPTS);
	}

	public long getHandledConnections() throws MonitorException {
		return parseConnectionsData(HANDLED);
	}

	public long getReadingConnections() throws MonitorException {
		return parseConnectionsStatusData(READING);
	}

	public long getWritingConnections() throws MonitorException {
		return parseConnectionsStatusData(WRITING);
	}

	public long getWaitingConnections() throws MonitorException {
		return parseConnectionsStatusData(WAITING);
	}

	/**
	 * Download Nginx status page and store it into tmp file (1 second) to avoid
	 * multiple requests to the status page
	 * 
	 * @return status page content
	 * @throws MonitorException
	 */
	private String getStatus() throws MonitorException {
		Path path = Paths.get(TEMP_FILE);
		try {
			if (!isTempFileUpdated()) {
				String status = readUrl(statusUrl);
				try {
					Files.deleteIfExists(path);
					Files.write(path, status.getBytes(), StandardOpenOption.CREATE);
				} catch (Exception e) {
					// Unable to store temp file, returns downloaded data
					return status;
				}
			}

			return new String(Files.readAllBytes(path));
		} catch (Exception e) {
			throw new MonitorException("Error reading status data", e);
		}
	}

	private boolean isTempFileUpdated() {
		File tmpFile = new File(TEMP_FILE);
		long lastModified = tmpFile.lastModified();
		long now = System.currentTimeMillis();

		// data older than 1s discarded
		return tmpFile.exists() && (now - lastModified < 1000);
	}

	private String readUrl(String targetUrl) throws MonitorException {
		try {
			URL url = new URL(targetUrl);
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();

			return response.toString();
		} catch (Exception e) {
			throw new MonitorException("Error reading URL: " + targetUrl, e);
		}
	}

	private long parseConnectionsData(int index) throws MonitorException {
		String status = getStatus();
		String regex = " (\\d*) (\\d*) (\\d*) ";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(status);
		if (m.find()) {
			return Long.parseLong(m.group(index));
		}

		throw new MonitorException("Error parsing requests data");
	}

	private long parseConnectionsStatusData(int index) throws MonitorException {
		String status = getStatus();
		String regex = "Reading: (\\d*) Writing: (\\d*) Waiting: (\\d*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(status);
		if (m.find()) {
			return Long.parseLong(m.group(index));
		}

		throw new MonitorException("Error parsing requests status data");
	}

}
