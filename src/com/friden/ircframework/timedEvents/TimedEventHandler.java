package com.friden.ircframework.timedEvents;

import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import com.friden.ircframework.events.OnDayChangedEvent;
import com.friden.ircframework.events.OnTimedDebugEvent;
import com.friden.ircframework.extendable.IrcPlugin;


public class TimedEventHandler extends Thread {

	private PriorityQueue<TimedEvent> eventQueue = null;
	
	private Semaphore queueMutex = new Semaphore(1);
	private Semaphore eventFiredSem = new Semaphore(0);
	
	private boolean running = true;
	
	private IrcPlugin ircPlugin = null;
	
	public TimedEventHandler(IrcPlugin ircPlugin){
		this.eventQueue = new PriorityQueue<TimedEvent>();
		this.ircPlugin = ircPlugin;
	}
	
	@Override
	public void run() {
		super.run();
		
		while(running){
			
			waitForFiredEvent();
			
			waitForQueueMutex();
			
			TimedEvent event = eventQueue.poll();
			
			releaseQueueMutex();
			
			if(event instanceof TimedDayChangedEvent){
				ircPlugin.addEvent(new OnDayChangedEvent());
			}else if(event instanceof TimedDebugEvent){
				ircPlugin.addEvent(new OnTimedDebugEvent());
			}
			
		}
		
	}
	
	private void waitForQueueMutex(){
		boolean gotMutex = false;
		while(!gotMutex){
			try {
				queueMutex.acquire();
				gotMutex = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void releaseQueueMutex(){
		queueMutex.release();
	}
	
	private void waitForFiredEvent(){
		boolean gotMutex = false;
		while(!gotMutex){
			try {
				eventFiredSem.acquire();
				gotMutex = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void fireTimedEvent(TimedEvent timedEvent){
		waitForQueueMutex();
		eventQueue.add(timedEvent);
		releaseQueueMutex();
		eventFiredSem.release();
	}

}
