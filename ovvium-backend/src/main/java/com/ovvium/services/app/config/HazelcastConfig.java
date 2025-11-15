package com.ovvium.services.app.config;

import com.hazelcast.collection.QueueStore;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.hazelcast.spring.transaction.HazelcastTransactionManager;
import com.ovvium.services.app.config.hazelcast.KryoSerializer;
import com.ovvium.services.app.config.properties.HazelcastProperties;
import com.ovvium.services.app.constant.Caches;
import com.ovvium.services.security.dto.ApiKeyDto;
import com.ovvium.services.service.LockService;
import com.ovvium.services.service.event.EventConsumerService;
import com.ovvium.services.service.event.EventHandler;
import com.ovvium.services.service.event.EventQueueStore;
import com.ovvium.services.service.impl.HazelcastLockServiceImpl;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import com.ovvium.services.util.ovvium.spring.AppWrapper;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.response.bill.BillResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.category.CategoryResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.TagResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderPosResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductGroupResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.product.ProductItemResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.rating.RatingResponse;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static com.hazelcast.config.InMemoryFormat.BINARY;
import static com.hazelcast.config.InMemoryFormat.OBJECT;
import static com.hazelcast.config.MaxSizePolicy.ENTRY_COUNT;
import static java.util.Arrays.asList;

@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = EventQueueStore.class)
public class HazelcastConfig {

	private static final String OVVIUM_EVENT_QUEUE = "OVVIUM_EVENT_QUEUE";
	private static final String OVVIUM_MAP_LOCKS = "OVVIUM_MAP_LOCKS";
	private static final String OVVIUM_BILLS_BY_USER_CACHE = "OVVIUM_BILLS_BY_USER_CACHE";
	private static final String OVVIUM_API_KEYS_CACHE = "OVVIUM_API_KEYS_CACHE";

	@Autowired
	private AppWrapper appWrapper;

	@Autowired
	private HazelcastProperties hazelcastProperties;

	@Autowired
	private QueueStore<OvviumEvent> eventQueueStore;

	@Bean(destroyMethod = "shutdown")
	public HazelcastInstance hazelcastInstance() {
		val config = new Config()
				.setInstanceName("hazelcast-instance");
		configureAwsClusterJoin(config);
		configureCaches(config);
		configureEventsQueue(config);
		configureLockSystem(config);
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	public BlockingQueue<OvviumEvent> eventsQueue(HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getQueue(OVVIUM_EVENT_QUEUE);
	}

	@Bean(destroyMethod = "shutdown")
	public EventConsumerService eventConsumerService(BlockingQueue<OvviumEvent> eventsQueue, List<EventHandler<?>> handlers) {
		return new EventConsumerService(eventsQueue, handlers);
	}

	@Bean
	public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
		return new HazelcastCacheManager(hazelcastInstance);
	}

	@Bean
	public LockService lockFactory(HazelcastInstance hazelcastInstance) {
		return new HazelcastLockServiceImpl(hazelcastInstance.getMap(OVVIUM_MAP_LOCKS));
	}

	@Bean
	public Map<UUID, UUID> billsByUserCache(HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getMap(OVVIUM_BILLS_BY_USER_CACHE);
	}

	@Bean
	public Map<String, ApiKeyDto> apiKeysByKey(HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getMap(OVVIUM_API_KEYS_CACHE);
	}

	@Bean
	@Primary
	public PlatformTransactionManager chainedTransactionManager(PlatformTransactionManager jpaTransactionManager, HazelcastInstance hazelcastInstance) {
		// Order is important here, as the commit is done in reversed order. JPA should be placed as last argument.
		return new ChainedTransactionManager(
				new HazelcastTransactionManager(hazelcastInstance),
				jpaTransactionManager
		);
	}

	private void configureCaches(Config config) {
		config.setProperty("hazelcast.map.invalidation.batch.enabled", "false"); // evict near caches immediately

		val serializationConfig = config.getSerializationConfig();
		ReflectionUtils.getStringConstants(Caches.class).forEach(cacheName ->
				addCacheConfig(config, cacheName)
		);
		getSerializers()
				.forEach((serializer) -> serializationConfig.addSerializerConfig(new SerializerConfig()
						.setImplementation(serializer)
						.setTypeClass(serializer.getConcreteClazz()))
				);
	}

	private void addCacheConfig(Config config, String cacheName) {
		val nearCacheConfig = new NearCacheConfig()
				.setInMemoryFormat(OBJECT)
				.setCacheLocalEntries(true)
				.setInvalidateOnChange(true)
				.setTimeToLiveSeconds(hazelcastProperties.getCache().getTimeToLiveSeconds())
				.setEvictionConfig(new EvictionConfig()
						.setMaxSizePolicy(ENTRY_COUNT)
						.setEvictionPolicy(EvictionPolicy.LRU)
						.setSize(hazelcastProperties.getCache().getMaxEntriesSize()));
		config.getMapConfig(cacheName)
				.setInMemoryFormat(BINARY)
				.setNearCacheConfig(nearCacheConfig);
	}

	/**
	 * For Kryo+Hazelcast to work, responses must be Immutable (final fields).
	 */
	private List<KryoSerializer<?>> getSerializers() {
		return asList(
				new KryoSerializer<>(1, CollectionWrapper.class),
				new KryoSerializer<>(2, ProductItemResponse.class),
				new KryoSerializer<>(3, BillResponse.class),
				new KryoSerializer<>(4, RatingResponse.class),
				new KryoSerializer<>(5, TagResponse.class),
				new KryoSerializer<>(6, ProductGroupResponse.class),
				new KryoSerializer<>(7, CategoryResponse.class),
				new KryoSerializer<>(8, ApiKeyDto.class),
				new KryoSerializer<>(9, PaymentOrderAppCardResponse.class),
				new KryoSerializer<>(10, PaymentOrderPosResponse.class)
		);
	}

	private void configureEventsQueue(Config config) {
		val queueProps = new Properties();
		queueProps.putAll(Map.of(
				"binary", "false",
				"memory-limit", "0",
				"bulk-load", "300"
		));
		config.getQueueConfig(OVVIUM_EVENT_QUEUE)
				.setMaxSize(hazelcastProperties.getQueue().getMaxSize())
				.setQueueStoreConfig(new QueueStoreConfig()
						.setProperties(queueProps)
						.setStoreImplementation(eventQueueStore));
	}

	private void configureLockSystem(Config config) {
		config.getMapConfig(OVVIUM_MAP_LOCKS)
				.setTimeToLiveSeconds(hazelcastProperties.getLock().getTtl())
				.setMaxIdleSeconds(hazelcastProperties.getLock().getMaxIdle());
	}

	private void configureAwsClusterJoin(Config config) {
		if (!appWrapper.isLocal()) {
			config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
			config.getNetworkConfig().getInterfaces().setEnabled(true).addInterface("10.0.*.*");
			config.getNetworkConfig().getJoin()
					.getAwsConfig()
					.setEnabled(true);
		}
	}

}
