package org.springframework.cloud.bazaar.connector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.ServiceInfo;

public abstract class BazaarServiceInfoCreator<SI extends ServiceInfo>
		extends CloudFoundryServiceInfoCreator<SI> {

	BazaarServiceInfoCreator(Tags tags, String... uriSchemes) {
		super(tags, uriSchemes);
	}

	@Override
	public boolean accept(Map<String, Object> serviceData) {
		return labelStartsWithTag(serviceData) || super.accept(serviceData);
	}

	protected List<Map<String, Object>> getListFromCredentials(
			Map<String, Object> credentials, String key) {
		return (List<Map<String, Object>>) credentials.get(key);
	}

	protected String matchEndsWithCredentials(Map<String, Object> credentials,
			String... keys) {
		if (credentials != null) {
			for (String key : keys) {
				for (Map.Entry<String, Object> entry : credentials.entrySet()) {
					if (entry.getKey().endsWith(key)) {
						return (String) entry.getValue();
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected int parsePort(List<Map<String, Object>> serviceInfo, String filter,
			String... portMatch) {
		int port = 0;
		List<Map<String, Object>> portList = (List<Map<String, Object>>) ((Map<String, Object>) serviceInfo
				.stream().filter(obj -> ((String) obj.get("name")).endsWith(filter))
				.findFirst().orElse(Collections.emptyMap())
				.getOrDefault("spec", Collections.emptyMap())).getOrDefault("ports",
						Collections.emptyList());

		if (!portList.isEmpty()) {
			Optional<Map<String, Object>> portMap = portList.stream()
					.filter((Map<String, Object> obj) -> ((String) obj.get("name"))
							.equalsIgnoreCase(this.getDefaultUriScheme()))
					.findFirst();
			if (portMap.isPresent()) {
				port = getIntFromCredentials(portMap.get(), portMatch);
			}
		}
		return port;
	}

	@SuppressWarnings("unchecked")
	protected String parseHost(List<Map<String, Object>> serviceInfo, String filter,
			String hostMatch) {
		String host = null;
		Map<String, Object> hostInfo = (Map<String, Object>) ((Map<String, Object>) serviceInfo
				.stream().filter(obj -> ((String) obj.get("name")).endsWith(filter))
				.findFirst().orElse(Collections.emptyMap())
				.getOrDefault("status", Collections.emptyMap()))
						.getOrDefault("loadBalancer", Collections.emptyMap());

		if (!hostInfo.isEmpty()) {
			host = (String) ((List<Map<String, Object>>) hostInfo.get("ingress")).stream()
					.findFirst().orElse(Collections.emptyMap())
					.get(hostMatch);
		}
		return host;
	}

}
