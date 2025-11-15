package com.ovvium.services.service.impl;

import com.hazelcast.map.IMap;
import com.ovvium.services.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HazelcastLockServiceImpl implements LockService {

	// We use Map instead of Locks so we don't have OutOfMemory errors.
	// See https://techlab.bol.com/baby-leaky-now-not/
	private final IMap<Object, Object> locks;

	@Override
	public boolean tryLock(String key) {
		log.debug("Lock requested for key {}", key);
		return locks.tryLock(key);
	}

	@Override
	public  boolean tryLock(String key, long amount, TimeUnit unit) {
		log.debug("Waiting for lock for key {} with wait duration of {} {}", key, amount, unit);
		try {
			return locks.tryLock(key, amount, unit);
		} catch (InterruptedException e) {
			return false;
		}
	}

	@Override
	public void unlock(String key) {
		locks.unlock(key);
		log.debug("Lock unlocked for key {}", key);
	}
}
