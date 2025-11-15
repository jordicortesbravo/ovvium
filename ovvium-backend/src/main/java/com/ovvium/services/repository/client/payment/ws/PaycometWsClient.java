package com.ovvium.services.repository.client.payment.ws;

import com.ovvium.services.repository.client.payment.ws.dto.AddUserTokenWsRequest;
import com.ovvium.services.repository.client.payment.ws.dto.AddUserTokenWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.ExecutePurchaseWsRequest;
import com.ovvium.services.repository.client.payment.ws.dto.ExecutePurchaseWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.InfoUserWsRequest;
import com.ovvium.services.repository.client.payment.ws.dto.InfoUserWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.RemoveUserTokenWsRequest;
import com.ovvium.services.repository.client.payment.ws.dto.RemoveUserTokenWsResponse;
import com.ovvium.services.repository.client.payment.ws.dto.SplitTransferWsRequest;
import com.ovvium.services.repository.client.payment.ws.dto.SplitTransferWsResponse;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

@WebService(targetNamespace="http://schemas.xmlsoap.org/soap/envelope/")
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
public interface PaycometWsClient {

	@WebMethod(operationName = "add_user_token", action = "http://schemas.xmlsoap.org/soap/envelope/#BankStore#add_user_token")
	AddUserTokenWsResponse addUserToken(AddUserTokenWsRequest request);

	@WebMethod(operationName = "remove_user", action = "http://schemas.xmlsoap.org/soap/envelope/#BankStore#remove_user")
	RemoveUserTokenWsResponse removeUserToken(RemoveUserTokenWsRequest request);
	
	@WebMethod(operationName="execute_purchase", action="http://schemas.xmlsoap.org/soap/envelope/#BankStore#execute_purchase")
	ExecutePurchaseWsResponse executePurchase(ExecutePurchaseWsRequest executePurchaseRequest);

	@WebMethod(operationName = "info_user", action = "http://schemas.xmlsoap.org/soap/envelope/#BankStore#info_user")
	InfoUserWsResponse getInfoUser(InfoUserWsRequest infoUserWsRequest);


	@WebMethod(operationName = "split_transfer", action = "http://schemas.xmlsoap.org/soap/envelope/#BankStore#split_transfer")
	SplitTransferWsResponse splitTransfer(SplitTransferWsRequest splitTransferWsRequest);


}
