package com.fridenmf.ircframework.core.timedEvents;

import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import com.fridenmf.ircframework.core.events.OnDayChangedEvent;
import com.fridenmf.ircframework.core.events.OnTimedDebugEvent;
import com.fridenmf.ircframework.core.extendable.IrcPlugin;

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
			
			eventFiredSem.acquireUninterruptibly();
			
			queueMutex.acquireUninterruptibly();
			TimedEvent event = eventQueue.poll();
			queueMutex.release();
			
			if(event instanceof TimedDayChangedEvent){
				ircPlugin.addEvent(new OnDayChangedEvent());
			}else if(event instanceof TimedDebugEvent){
				ircPlugin.addEvent(new OnTimedDebugEvent());
			}
			
		}
		
	}
	
	public void fireTimedEvent(TimedEvent timedEvent){
		queueMutex.acquireUninterruptibly();
		eventQueue.add(timedEvent);
		queueMutex.release();
		eventFiredSem.release();
	}

}
