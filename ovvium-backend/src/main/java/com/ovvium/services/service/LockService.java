package com.ovvium.services.service;

import java.util.concurrent.TimeUnit;

public interface LockService {

	boolean tryLock(String key);

	boolean tryLock(String key, long amount, TimeUnit unit);

	void unlock(String key);

}
