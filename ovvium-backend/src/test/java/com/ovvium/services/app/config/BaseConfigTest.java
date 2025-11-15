package com.ovvium.services.app.config;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseConfigTest {

	private BaseConfig baseConfig;

	@Before
	public void setUp() throws Exception {
		baseConfig = new BaseConfig();
	}

	@Test
	public void given_paycomet_commissions_file_when_load_commissions_properties_then_should_map_json_correctly() {
		PaycometCommissionProperties paycometCommissionProperties = baseConfig.paycometCommissions();

		assertThat(paycometCommissionProperties.getConfig()).isNotEmpty();
		assertThat(paycometCommissionProperties.getSplitCommissionPercentage()).isNotNull();
	}

	@Test
	public void given_paycomet_errors_file_when_load_errors_file_then_should_map_json_correctly() {
		Map<Integer, String> paycometErrorsMap = baseConfig.paycometErrorsMap();

		assertThat(paycometErrorsMap).isNotEmpty();
	}
}