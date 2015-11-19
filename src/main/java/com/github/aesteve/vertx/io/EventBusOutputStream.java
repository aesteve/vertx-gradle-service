package com.github.aesteve.vertx.io;

import java.io.IOException;
import java.io.OutputStream;

import io.vertx.core.Vertx;

public class EventBusOutputStream extends OutputStream {
	
	private Vertx vertx;
	private String address;
	private StringBuilder buffer;
	
	public EventBusOutputStream(Vertx vertx, String address) {
		this.vertx = vertx;
		this.address = address;
		buffer = new StringBuilder(128);
	}

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        if (value.equals("\n") && buffer.length() > 0) {
        	vertx.eventBus().publish(address, buffer.toString());
            buffer.delete(0, buffer.length());
        }
        if (!value.equals("\n")){
            buffer.append(value);
        }
    }   
    
}
