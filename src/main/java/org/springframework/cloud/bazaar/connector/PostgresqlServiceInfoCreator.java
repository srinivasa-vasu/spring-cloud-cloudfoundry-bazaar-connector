package org.springframework.cloud.bazaar.connector;

import static org.springframework.cloud.service.common.PostgresqlServiceInfo.POSTGRES_JDBC_SCHEME;
import static org.springframework.cloud.service.common.PostgresqlServiceInfo.POSTGRES_SCHEME;

import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.PostgresqlServiceInfo;

public class PostgresqlServiceInfoCreator
		extends RelationalServiceInfoCreator<PostgresqlServiceInfo> {

	public PostgresqlServiceInfoCreator() {
		super(new Tags("postgresql"), POSTGRES_JDBC_SCHEME, POSTGRES_SCHEME);
		// TODO - Bazaar doesn't return these values. To be removed
		setUsername("postgres");
		setDatabase("my_db");
	}

	@Override
	public PostgresqlServiceInfo createServiceInfo(String id, String url,
			String jdbcUrl) {
		return new PostgresqlServiceInfo(id, url, jdbcUrl);
	}

}
