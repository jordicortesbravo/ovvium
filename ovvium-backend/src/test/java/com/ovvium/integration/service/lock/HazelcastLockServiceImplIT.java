package com.ovvium.integration.service.lock;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.services.app.config.BaseConfig;
import com.ovvium.services.app.config.HazelcastConfig;
import com.ovvium.services.repository.EventRepository;
import com.ovvium.services.service.impl.HazelcastLockServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ContextConfiguration(classes = {HazelcastConfig.class, BaseConfig.class})
public class HazelcastLockServiceImplIT extends AbstractIntegrationTest {

	@MockBean
	private EventRepository eventRepository;

	@MockBean
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private HazelcastLockServiceImpl hazelcastLockService;

	@Test
	public void given_try_lock_when_check_is_locked_then_try_should_return_false() throws InterruptedException {
		boolean firstLock = hazelcastLockService.tryLock("my-lock");

		assertThat(firstLock).isTrue();

		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

		newSingleThreadExecutor.execute(() -> {
			boolean secondLock = hazelcastLockService.tryLock("my-lock");

			assertThat(secondLock).isFalse();
		});
		newSingleThreadExecutor.awaitTermination(2, SECONDS);

		hazelcastLockService.unlock("my-lock");

		boolean thirdLock = hazelcastLockService.tryLock("my-lock");

		assertThat(thirdLock).isTrue();
	}

	@Test(timeout = 60000)
	public void given_basic_mutex_with_locks_when_create_try_lock_then_should_lock_and_unlock_correctly() throws Exception {
		int threads = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		SimpleMutex mutexClass = new SimpleMutex();
		Set<Integer> uniqueCounters = new HashSet<Integer>();

		// when
		IntStream.range(0, threads)
				.forEach((i) -> executorService.execute(() -> {
					uniqueCounters.add(mutexClass.increment());
				}));
		executorService.shutdown();
		executorService.awaitTermination(60, SECONDS);

		// If mutex works correctly, size of list should be num of threads
		assertThat(uniqueCounters.size()).isEqualTo(threads);
	}

	@Data
	public class SimpleMutex {

		private int counter = 0;

		public int increment() {
			String key = "my-key";
			boolean locked = hazelcastLockService.tryLock(key, 20, SECONDS);
			if (!locked) {
				throw new RuntimeException("Cannot lock");
			}
			try {
				counter++;
				log.debug("Inside mutex, increment counter: {}", counter);
				return counter;
			} finally {
				hazelcastLockService.unlock(key);
			}
		}

	}
}