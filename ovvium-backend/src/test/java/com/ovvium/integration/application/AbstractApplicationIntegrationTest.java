package com.ovvium.integration.application;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.ovvium.integration.config.RepositoryTestConfig;
import com.ovvium.services.app.config.BaseConfig;
import com.ovvium.services.app.config.WebConfig;
import com.ovvium.services.app.config.WsConfig;
import com.ovvium.services.app.config.listener.StartupPopulator;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.util.io.IOUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.LoginRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.utils.TransactionalHelper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.ovvium.utils.SpringMockMvcUtils.doPost;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, RepositoryTestConfig.class, BaseConfig.class, WsConfig.class},
		loader = AnnotationConfigWebContextLoader.class)
@Sql(scripts = "/init_data.sql", executionPhase = BEFORE_TEST_METHOD)
public abstract class AbstractApplicationIntegrationTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected TransactionalHelper transactionalHelper;

	@MockBean
	protected PaycometWsClient paycometWsClient;

	@MockBean
	protected MailHelper mailHelper;

	@MockBean
	private StartupPopulator startupPopulator;

	@Autowired
	private Environment props;

	@MockBean
	private AmazonS3Client amazonS3Client;

	@MockBean
	private TransferManager transferManager;

	protected String loginUser(String email, String password) {
		return doPost(mockMvc, "/account/login", new LoginRequest().setEmail(email).setPassword(password), status().is2xxSuccessful(), null, LoginResponse.class)
				.getSession().getAccessToken();
	}

	protected String fromJson(String filename) {
		return IOUtils.getResourceAsString("json/" + filename, UTF_8);
	}

}
