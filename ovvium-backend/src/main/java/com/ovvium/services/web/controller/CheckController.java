package com.ovvium.services.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class CheckController {

	@GetMapping("/check")
	public String check() {
		return "OK";
	}
}
