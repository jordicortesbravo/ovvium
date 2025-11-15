package com.ovvium.services.web.controller.bff.v1.transfer.request.staticweb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ContactData {

	private String name;
	private String email;
	private String phone;
	private String company;
	private String size;
	private String city;
	private String date;
	private String how;
}
