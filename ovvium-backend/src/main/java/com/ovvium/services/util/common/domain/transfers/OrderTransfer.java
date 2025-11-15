package com.ovvium.services.util.common.domain.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTransfer implements Serializable {

    private static final long serialVersionUID = 5568524110996459728L;

    private String direction;
    
    private String property;
}
