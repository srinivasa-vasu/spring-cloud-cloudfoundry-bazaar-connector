package org.springframework.cloud.bazaar.connector;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.MongoServiceInfo;
import org.springframework.cloud.util.UriInfo;

public class MongoServiceInfoCreator extends BazaarServiceInfoCreator<MongoServiceInfo> {

	@Value("${SPRING_DATA_MONGODB_DATABASE}")
	private String dbProp;

	public MongoServiceInfoCreator() {
		super(new Tags("od-mongo", "ds-mongo", "k8s-mongo"),
				MongoServiceInfo.MONGODB_SCHEME);
	}

	@SuppressWarnings("unchecked")
	public MongoServiceInfo createServiceInfo(Map<String, Object> serviceData) {

		int port = 0;
		String password = null;
		String host = null;
		String username = null;
		String database = dbProp;
		String uri = null;

		Map<String, Object> credentials = getCredentials(serviceData);
		if (credentials != null && !credentials.isEmpty()) {

			Map<String, Object> secretInfo = (Map<String, Object>) getListFromCredentials(
					credentials, "secrets").stream().findFirst()
							.orElse(Collections.emptyMap())
							.getOrDefault("data", Collections.emptyMap());

			if (!secretInfo.isEmpty()) {
				password = matchEndsWithCredentials(secretInfo, "root-password",
						"password");
				username = matchEndsWithCredentials(secretInfo, "user", "username");
				if ((database == null || database.isEmpty())
						&& (database = System
								.getenv("SPRING_DATA_MONGODB_DATABASE")) == null
						|| database.isEmpty()) {
					database = matchEndsWithCredentials(secretInfo, "database", "db",
							"adminDatabase");
				}
			}

			List<Map<String, Object>> services = getListFromCredentials(credentials,
					"services");

			if (services != null && !services.isEmpty()) {
				port = parsePort(services, "client", "port", "nodePort", "targetPort");
				host = parseHost(services, "client", "ip", "hostname");
			}

			uri = new UriInfo(getDefaultUriScheme(), host, port, username, password,
					database, "authSource=admin").toString();
		}

		return createServiceInfo(getId(serviceData), uri);
	}

	public MongoServiceInfo createServiceInfo(String id, String uri) {
		return new MongoServiceInfo(id, uri);
	}
}
