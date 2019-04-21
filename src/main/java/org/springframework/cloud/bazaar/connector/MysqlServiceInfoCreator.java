package org.springframework.cloud.bazaar.connector;

import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.common.MysqlServiceInfo;

public class MysqlServiceInfoCreator
		extends RelationalServiceInfoCreator<MysqlServiceInfo> {

	public MysqlServiceInfoCreator() {
		super(new Tags("od-mysql", "ds-mysql"), MysqlServiceInfo.MYSQL_SCHEME);
		// TODO - Bazaar doesn't return these values. To be removed
		setUsername("root");
		setDatabase("my_db");
	}

	public MysqlServiceInfo createServiceInfo(String id, String url, String jdbcUrl) {
		return new MysqlServiceInfo(id, url, jdbcUrl);
	}

}
