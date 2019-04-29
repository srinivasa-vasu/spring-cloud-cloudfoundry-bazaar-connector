package org.springframework.cloud.bazaar.connector;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.RedisServiceInfo;

public class RedisServiceInfoCreator extends BazaarServiceInfoCreator<RedisServiceInfo> {

	public RedisServiceInfoCreator() {
		super(new Tags("od-redis", "ds-redis", "k8s-redis"),
				RedisServiceInfo.REDIS_SCHEME);
	}

	@SuppressWarnings("unchecked")
	public RedisServiceInfo createServiceInfo(Map<String, Object> serviceData) {

		int port = 0;
		String password = null;
		String host = null;

		Map<String, Object> credentials = getCredentials(serviceData);

		if (credentials != null && !credentials.isEmpty()) {

			Map<String, Object> secretInfo = (Map<String, Object>) getListFromCredentials(
					credentials, "secrets").stream().findFirst()
							.orElse(Collections.emptyMap())
							.getOrDefault("data", Collections.emptyMap());

			if (!secretInfo.isEmpty()) {
				password = matchEndsWithCredentials(secretInfo, "redis-password",
						"password");
			}

			List<Map<String, Object>> services = getListFromCredentials(credentials,
					"services");

			if (services != null && !services.isEmpty()) {
				port = parsePort(services, "", "port", "nodePort", "targetPort");
				host = parseHost(services, "", "ip", "hostname");
			}

		}

		return createServiceInfo(getId(serviceData), host, port, password);
	}

	public RedisServiceInfo createServiceInfo(String id, String host, int port, String password) {
		return new RedisServiceInfo(id, host, port, password);
	}
}
