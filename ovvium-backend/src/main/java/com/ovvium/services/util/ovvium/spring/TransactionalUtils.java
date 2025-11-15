package com.ovvium.services.util.ovvium.spring;

import lombok.experimental.UtilityClass;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@UtilityClass
public class TransactionalUtils {

	/**
	 * Execute this lambda function after Commit.
	 */
	public static void executeAfterCommit(Runnable runnable) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCompletion(int status) {
					if (status == STATUS_COMMITTED) {
						runnable.run();
					}
				}
			});
		} else {
			runnable.run();
		}
	}

	/**
	 * Execute this lambda function after this transaction ends (doesn´t mind if rollbacked)
	 */
	public static void executeAfterTransaction(Runnable runnable) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCompletion(int status) {
					runnable.run();
				}
			});
		} else {
			runnable.run();
		}
	}

}
