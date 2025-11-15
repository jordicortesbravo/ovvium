package com.ovvium.services.web.controller.bff.v1.transfer.request.account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecoverPasswordRequest {

	//TODO Hacer el proceso de recover más completo por seguridad. Ejemplo (Código para ejecutar recover por SMS o mail, questions...) --> Después de PMV
	private String email;

}
