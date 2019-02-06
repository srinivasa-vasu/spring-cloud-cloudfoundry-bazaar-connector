package org.springframework.cloud.bazaar.connector;

import java.util.List;
import java.util.Map;

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
		return (List) credentials.get(key);
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

}
