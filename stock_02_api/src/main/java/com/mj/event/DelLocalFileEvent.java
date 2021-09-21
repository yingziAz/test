package com.mj.event;

import net.dreamlu.event.core.ApplicationEvent;

@SuppressWarnings("serial")
public class DelLocalFileEvent extends ApplicationEvent {

	public DelLocalFileEvent(Object source) {
		super(source);
	}
	
}