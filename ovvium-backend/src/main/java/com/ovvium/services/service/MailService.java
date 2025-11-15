package com.ovvium.services.service;

public interface MailService {

	void notifyError(String subject, Exception exc);

}
