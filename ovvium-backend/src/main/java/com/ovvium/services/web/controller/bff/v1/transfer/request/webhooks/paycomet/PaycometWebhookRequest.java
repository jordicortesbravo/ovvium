package com.ovvium.services.web.controller.bff.v1.transfer.request.webhooks.paycomet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Fields are mapped by Java Bean convention. Check field names here https://docs.paycomet.com/es/inicio/seguimiento
 */
@Data
@NoArgsConstructor
public class PaycometWebhookRequest implements Serializable {

    private Integer methodId;
    private String methodName;
    private Integer transactionType;
    private String transactionName;
    private String bankDateTime;
    private String order;
    private String response;
    private Integer errorID;
    private String errorDescription;
    private String authCode;
    private Integer amount;
    private String currency;
    private String accountCode;
    private String tpvID;
    private String notificationHash;

}
