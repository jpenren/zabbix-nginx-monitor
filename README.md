# zabbix-nginx-monitor
Monitoring utility to get Nginx status values into Zabbix. Works on Windows and Linux (JRE required).

# Configuration

* Configure Nginx status page (http://nginx.org/en/docs/http/ngx_http_stub_status_module.html)

Example:
```
server {		
		listen 127.0.0.1:8181;
		location /nginx_status {
			#limit_conn addr 10;
			#limit_req zone=one;
			stub_status on;
			access_log off;
			allow 127.0.0.1;
			deny all;
		}
	}
```

* Add a new user parameter to the Zabbix agent (zabbix_agentd.conf) to use zabbix-nginx-monitor
```
UserParameter=nginx[*],java -jar /path/to/zabbix-nginx-monitor-<version>.jar <zabbix status url> $1
```

Example:
```
UserParameter=nginx[*],"C:\Program Files\Java\jdk1.7.0_79\jre\bin\java" -jar c:\zabbix-nginx-monitor-1.0.0.jar http://localhost:8181/nginx_status $1
```

* Create the host items on Zabbix server to monitorize Nginx with the following keys:
  * nginx[connections]
  * nginx[requests]
  * nginx[accepts]
  * nginx[handled]
  * nginx[reading]
  * nginx[writing]
  * nginx[waiting]

![Zabbix Nginx monitor graph](https://raw.githubusercontent.com/jpenren/zabbix-nginx-monitor/master/doc/graph.png)
