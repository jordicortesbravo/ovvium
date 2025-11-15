package com.ovvium.services.app.config.listener;

import com.ovvium.services.service.PopulatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@RequiredArgsConstructor
public class StartupPopulator implements ApplicationListener<ContextRefreshedEvent> {

	private final PopulatorService populatorService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!populatorService.isPopulated()) {
			populatorService.populate();
		}
		populatorService.onPopulated();
	}
}
