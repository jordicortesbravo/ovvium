package com.ovvium.integration.repository.client.payment;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.integration.config.RepositoryTestConfig;
import com.ovvium.mother.builder.PaymentOrderAppCardBuilder;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.OrderMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.app.config.WsConfig;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.client.payment.PaycometClientImpl;
import com.ovvium.services.repository.client.payment.dto.*;
import com.ovvium.services.repository.client.payment.exception.PaycometException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.UUID;

import static com.ovvium.mother.model.CustomerMother.EL_BULLI_CUSTOMER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * INSTRUCTIONS:
 * - Use paycometJetTokenGenerationForm.html to get JET_TOKEN from Sandbox, then run first test to get USER_TOKEN and USER_ID, and change constants before running other tests
 */
@ContextConfiguration(classes = {RepositoryTestConfig.class, WsConfig.class})
public class PayCometClientImplIT  extends AbstractIntegrationTest {


	private final static String JET_TOKEN = "7dacafe24c74efb48fc1ce81977c2c7f3679143b5aa43c61d24ac627b164bcce";

	// For VISA CARD NUMBER:  5445288852200883
	private final static String USER_TOKEN = "YkNWaFIycG5MRE4";
	private final static String USER_ID = "40590619";

	private final static String SUBMERCHANT_ID = "a915c673219628d93c62e409c22f37b93259d33004672cdbe499d0fd3466ddb5";

	@Autowired
	private PaycometClientImpl client;

	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_paycomet_user_request_when_addusertoken_then_should_add_user_token_to_paycomet() {
		final AddUserTokenRequest request = new AddUserTokenRequest(JET_TOKEN, UserMother.getUserJordi());

		AddUserTokenResponse userResponse = client.addUserToken(request);

		System.out.println(userResponse);
		assertThat(userResponse.getUserToken()).isNotBlank();
		assertThat(userResponse.getUserId()).isNotBlank();
	}

	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_paycomet_user_token_when_removeusertoken_then_should_remove_user_token_from_paycomet() {
		final RemoveUserTokenRequest request = new RemoveUserTokenRequest(UserMother.getUserJordi(), USER_ID, USER_TOKEN);

		RemoveUserTokenResponse response = client.removeUserToken(request);

		System.out.println(response);
		assertThat(response.getResponse()).isEqualTo(1);
	}

	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_incorrect_paycomet_user_request_when_addusertoken_then_should_throw_exception() {
		final AddUserTokenRequest request = new AddUserTokenRequest("WRONG_JET_TOKEN", UserMother.getUserJordi());

		assertThatThrownBy(() -> client.addUserToken(request))
				.isInstanceOf(PaycometException.class)
				.hasMessage("Error ID 1204: Recibido token incorrecto");
	}

	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_paycomet_execute_purchase_request_when_execute_purchase_then_should_return_correct_response() {
		var orderId = UUID.randomUUID();
		var user = User.basicUser("test", "test@ovvium.com", "OvvTest123");
		var pciDetails = user.addUserPciDetail(USER_ID, USER_TOKEN);
		final ExecutePurchaseRequest request = new ExecutePurchaseRequest(
				EL_BULLI_CUSTOMER_ID,
				user,
				pciDetails,
				MoneyAmount.ofDouble(2.0),
				orderId, // this should be Payment Order ID and should be Unique
				Collections.singleton(OrderMother.getOrderOfCerveza())
		);

		ExecutePurchaseResponse wsResponse = client.executePurchase(request);
		System.out.println(wsResponse);
		assertThat(wsResponse.getChallengeUrl()).isNotEmpty();
		assertThat(wsResponse.getAmount()).isEqualTo(0); // when challenge url is empty, will be 0
		assertThat(wsResponse.getResponse()).isEqualTo(0);
	}


	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_executed_purchase_correctly_and_split_transfer_request_when_split_transfer_then_should_return_correct_response() {
		final UUID orderId = UUID.randomUUID();
		var user = User.basicUser("test", "test@ovvium.com", "OvvTest123");
		var pciDetails = user.addUserPciDetail(USER_ID, USER_TOKEN);
		final ExecutePurchaseRequest purchaseRequest = new ExecutePurchaseRequest(
				EL_BULLI_CUSTOMER_ID,
				user,
				pciDetails,
				MoneyAmount.ofDouble(6.0),
				orderId, // this should be Payment Order ID and should be Unique
				Collections.singleton(OrderMother.getOrderOfCerveza())
		);
		ExecutePurchaseResponse wsResponse = client.executePurchase(purchaseRequest);

		String authCode = wsResponse.getAuthCode();
		final SplitTransferRequest splitTransferRequest = new SplitTransferRequest(CustomerMother.getCanRocaCustomer(), orderId, authCode, SUBMERCHANT_ID, MoneyAmount.ofDouble(1));
		SplitTransferResponse splitTransferResponse = client.splitTransfer(splitTransferRequest);

		assertThat(splitTransferResponse.getOrder()).isEqualTo(orderId.toString());
		assertThat(splitTransferResponse.getResponse()).isEqualTo(1);
		assertThat(splitTransferResponse.getSubmerchantAmount()).isEqualTo(100);
		assertThat(splitTransferResponse.getSubmerchantCurrency()).isEqualTo("EUR");
		assertThat(splitTransferResponse.getTransferAuthCode()).isNotEqualTo(authCode);
	}

	@Test
	@Ignore("This is just for Sandbox purposes")
	public void given_get_info_user_when_info_user_then_should_return_user_info() {
		var user = User.basicUser("test", "test@ovvium.com", "OvvTest123");
		var pciDetails = user.addUserPciDetail(USER_ID, USER_TOKEN);
		final InfoUserRequest infoUserRequest = new InfoUserRequest(user, pciDetails);

		final InfoUserResponse wsResponse = client.getInfoUser(infoUserRequest);

		assertThat(wsResponse.getCardBrand()).isEqualTo("MASTERCARD");
		assertThat(wsResponse.getCardPan()).contains("X");
		assertThat(wsResponse.getCardType()).isEqualTo("CREDIT");
		assertThat(wsResponse.getCardCountry()).isEqualTo("USA");
		assertThat(wsResponse.getCardExpiryDate()).isEqualTo("2021/05");
		assertThat(wsResponse.getCardCategory()).isEqualTo("CONSUMER");
		assertThat(wsResponse.getCardHash()).isNotBlank();
		assertThat(wsResponse.getCardSepa()).isEqualTo(0);
	}


	@Test
	public void given_hashes_from_notification_when_verify_notification_hash_then_should_match_calculated_hash() {
		var isValidA = client.verifyNotification(new VerifyNotificationRequest(
				new PaymentOrderAppCardBuilder().setPciTransactionId(UUID.fromString("3839291a-a93a-42f4-a593-dabea0baec81")).build(),
				"1199b5f46184458ec9466798c963ad16f1d8a72573f7d5daa059306aabc174f56c3feac84bdbbb849138f9cb62079eb186eb0d822a22ff2c630768f4e992fcb4",
				1,
				200,
				"EUR",
				"20201214203017",
				"OK"
		));
		var isValidB = client.verifyNotification(new VerifyNotificationRequest(
				new PaymentOrderAppCardBuilder().setPciTransactionId(UUID.fromString("7712564b-2649-4648-a661-4938bec28037")).build(),
				"6c83bcefe9063cc6d1437f4657cd3e67bc425fccf25c332bd47114ec1caef8657a93fad59a980575c44ad43803e1176f4890b9fd9735ecd86ff57e6596a00466",
				1,
				200,
				"EUR",
				"20201214202825",
				"OK"
		));
		assertThat(isValidA).isTrue();
		assertThat(isValidB).isTrue();
	}

}
