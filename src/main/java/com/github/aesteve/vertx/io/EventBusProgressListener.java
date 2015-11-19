package com.github.aesteve.vertx.io;

import org.gradle.tooling.events.ProgressEvent;
import org.gradle.tooling.events.ProgressListener;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;

public class EventBusProgressListener implements ProgressListener {

	private Vertx vertx;
	private String address;
	
	public EventBusProgressListener(Vertx vertx, String address) {
		this.vertx = vertx;
		this.address = address;
	}
	
	@Override
	public void statusChanged(ProgressEvent event) {
		vertx.eventBus().publish(address, event.getDisplayName(), getDeliveryOptions(event));
	}
	
	private static DeliveryOptions getDeliveryOptions(ProgressEvent event) {
		DeliveryOptions options = new DeliveryOptions();
		options.addHeader("event-time", String.valueOf(event.getEventTime()));
		return options;
	}

}
