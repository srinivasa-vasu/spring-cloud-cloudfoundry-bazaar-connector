package org.springframework.cloud.bazaar.connector;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.RedisServiceInfo;
import org.springframework.cloud.util.UriInfo;

public class RedisServiceInfoCreator extends BazaarServiceInfoCreator<RedisServiceInfo> {

	public RedisServiceInfoCreator() {
		super(new Tags("redis-od", "redis-ds", "redis-odc"),
				RedisServiceInfo.REDIS_SCHEME, RedisServiceInfo.REDISS_SCHEME);
	}

	@SuppressWarnings("unchecked")
	public RedisServiceInfo createServiceInfo(Map<String, Object> serviceData) {

		String uri = null;
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
				host = parseHost(services, "", "ip", "clusterIP");
			}

			uri = new UriInfo(getDefaultUriScheme(), host, port, null, password, null)
					.toString();
		}

		return createServiceInfo(getId(serviceData), uri);
	}

	public RedisServiceInfo createServiceInfo(String id, String uri) {
		return new RedisServiceInfo(id, uri);
	}
}
