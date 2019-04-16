package org.springframework.cloud.bazaar.connector;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.RelationalServiceInfo;
import org.springframework.cloud.util.UriInfo;

public abstract class RelationalServiceInfoCreator<SI extends RelationalServiceInfo>
		extends BazaarServiceInfoCreator<SI> {

	private String username;
	private String database;

	RelationalServiceInfoCreator(Tags tags, String... uriSchemes) {
		super(tags, uriSchemes);
	}

	@Override
	public boolean accept(Map<String, Object> serviceData) {
		return super.accept(serviceData);
	}

	public abstract SI createServiceInfo(String id, String uri, String jdbcUrl);

	@SuppressWarnings("unchecked")
	public SI createServiceInfo(Map<String, Object> serviceData) {
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
				password = matchEndsWithCredentials(secretInfo, "root-password",
						"password");
			}

			List<Map<String, Object>> services = getListFromCredentials(credentials,
					"services");

			if (services != null && !services.isEmpty()) {

				port = parsePort(services, "", "port", "nodePort");
				host = parseHost(services, "", "ip");

			}

			uri = new UriInfo(getDefaultUriScheme(), host, port, username, password,
					database).toString();
		}

		return createServiceInfo(getId(serviceData), uri, null);
	}

	void setUsername(String username) {
		this.username = username;
	}

	void setDatabase(String database) {
		this.database = database;
	}
}
