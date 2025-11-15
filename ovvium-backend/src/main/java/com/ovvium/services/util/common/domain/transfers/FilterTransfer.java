package com.ovvium.services.util.common.domain.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import com.ovvium.services.util.common.domain.Filter.Condition;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterTransfer implements Serializable {

    private static final long serialVersionUID = -108249333300859780L;
    
    private String field;
    //TODO: Mirar si això es pot serialitzar d'una altra manera per evitar que despres surti xs:string etc. als objectes que s'envien per WS
    private Object value;
    private Condition condition;
    private boolean not = false;
}
