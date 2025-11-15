package com.ovvium.integration.repository;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.ovvium.integration.config.RepositoryTestConfig;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@AutoConfigureTestEntityManager
@ContextConfiguration(classes = {RepositoryTestConfig.class})
@Sql(scripts = "/init_data.sql", executionPhase = BEFORE_TEST_METHOD)
public abstract class AbstractRepositoryIT {

	@Autowired
	protected TestEntityManager entityManager;

	@MockBean
	private Map<Integer, String> paycometErrorsMap;

	@MockBean
	private PaycometWsClient paycometWsClient;

	@MockBean
	private AmazonS3Client amazonS3Client;

	@MockBean
	private TransferManager transferManager;


}
