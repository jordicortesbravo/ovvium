package com.ovvium.services.repository.client.payment;

import com.ovvium.services.repository.client.payment.dto.VerifyNotificationRequest;
import com.ovvium.services.util.ovvium.encoding.EncodingUtils.Algorithm;
import lombok.NoArgsConstructor;
import org.springframework.util.DigestUtils;

import static com.ovvium.services.util.ovvium.encoding.EncodingUtils.Algorithm.SHA256;
import static com.ovvium.services.util.ovvium.encoding.EncodingUtils.hash;
import static com.ovvium.services.util.ovvium.encoding.EncodingUtils.hex;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PaycometSignatureFactory {

	private static class PaycometSignatureBuilder {

		private static final Algorithm DEFAULT_ALGORITHM = Algorithm.SHA512;

		private final StringBuilder signatureBuilder = new StringBuilder();
		private Algorithm algorithm = DEFAULT_ALGORITHM;

		private PaycometSignatureBuilder add(String value) {
			this.signatureBuilder.append(value);
			return this;
		}

		private PaycometSignatureBuilder withAlgorithm(Algorithm algorithm) {
			this.algorithm = algorithm;
			return this;
		}

		private String build() {
			return hex(hash(algorithm, signatureBuilder.toString(), UTF_8)).toLowerCase();
		}

	}


	static String addUserToken(String merchantCode, String merchantJetToken, String merchantJetId, String merchantTerminal, String password) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(merchantJetToken)
				.add(merchantJetId)
				.add(merchantTerminal)
				.add(password)
				.build();
	}

	static String removeUserToken(String merchantCode, String userId, String userToken, String merchantTerminal, String password) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(userId)
				.add(userToken)
				.add(merchantTerminal)
				.add(password)
				.build();
	}

	static String executePurchase(String merchantCode, String idUser, String userToken, String merchantTerminal,
								  String amount, String order, String password) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(idUser)
				.add(userToken)
				.add(merchantTerminal)
				.add(amount)
				.add(order)
				.add(password)
				.build();
	}


	static String getUserInfo(String merchantCode, String idUser, String userToken, String merchantTerminal, String password) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(idUser)
				.add(userToken)
				.add(merchantTerminal)
				.add(password)
				.build();
	}

	static String splitTransfer(String merchantCode, String terminal, String order, String authCode, String splitId, String amount, String currency, String password) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(terminal)
				.add(order)
				.add(authCode)
				.add(splitId)
				.add(amount)
				.add(currency)
				.add(password)
				.withAlgorithm(SHA256)
				.build();
	}

	// SHA512(AccountCode+TpvID+TransactionType+Order+Amount+Currency+md5(password)+BankDateTime+Response);
	static String notificationHash(String merchantCode, String terminal, String password, VerifyNotificationRequest request) {
		return new PaycometSignatureBuilder()
				.add(merchantCode)
				.add(terminal)
				.add(request.transactionType().toString())
				.add(request.paymentOrder().getPciTransactionId().toString())
				.add(request.amount().toString())
				.add(request.currency())
				.add(DigestUtils.md5DigestAsHex(password.getBytes()))
				.add(request.bankDateTime())
				.add(request.response())
				.build();
	}

}
