package com.ovvium.services.repository.client.payment;

import com.ovvium.services.model.payment.UnsuccessfulPaymentClientException;
import com.ovvium.services.repository.client.payment.dto.*;

public interface PaymentClient {

	AddUserTokenResponse addUserToken(AddUserTokenRequest request);

	RemoveUserTokenResponse removeUserToken(RemoveUserTokenRequest request);

	ExecutePurchaseResponse executePurchase(ExecutePurchaseRequest request);

	InfoUserResponse getInfoUser(InfoUserRequest request);

	SplitTransferResponse splitTransfer(SplitTransferRequest request);

	boolean verifyNotification(VerifyNotificationRequest request);

	void checkForErrors(CheckClientErrorsRequest request) throws UnsuccessfulPaymentClientException;
}
