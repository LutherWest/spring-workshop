package com.epam.workshop.presentation;

import com.epam.workshop.presentation.config.WaterConfig;
import com.epam.workshop.presentation.service.Fire;
import com.epam.workshop.presentation.service.Water;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class WorkshopApplication {

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext ctx =
				new AnnotationConfigApplicationContext();

		ctx.register(WaterConfig.class);
		ctx.scan("com.epam.workshop.presentation.service");

		ctx.refresh();

		final Water water = ctx.getBean(Water.class);
		water.water();
		final Fire fire = ctx.getBean(Fire.class);
		fire.fire();

		ctx.close();
	}

}
