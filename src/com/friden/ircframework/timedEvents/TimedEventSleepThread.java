package com.friden.ircframework.timedEvents;

import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class TimedEventSleepThread extends Thread {

	private TimedEventHandler teh = null;
	
	private boolean running = true;
	private boolean notified = false;
	
	private PriorityQueue<TimedEvent> eventQueue = null;
	
	private Semaphore queueMutex = new Semaphore(1);
	
	public TimedEventSleepThread(TimedEventHandler teh){
		this.teh = teh;
		eventQueue = new PriorityQueue<TimedEvent>();
		TimedEvent evt = createOnDayChangedEvent();
		addTimedEvent(evt);
	}
	
	private TimedEvent createOnDayChangedEvent() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.DATE, calendar.get(GregorianCalendar.DATE) + 1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		return new TimedDayChangedEvent(calendar.getTime().getTime());
	}

	@Override
	public void run() {
		
		while(running){
			
			waitForQueueMutex();
			
			TimedEvent event = eventQueue.peek();
			
			releaseQueueMutex();
			
			long sleepTime = event == null ? Long.MAX_VALUE : (event.getTarget() - System.currentTimeMillis());
			
			if(sleepTime > 0){
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					if(notified){
						notified = false;
						waitForQueueMutex();
						releaseQueueMutex();
					}
					/* Continue the loop, to get earliest event, even if it was just a spurious wake up */
					continue;
				}
			}
			
			/* Thread did sleep successfully, or skipped sleeping because of negative sleep time */
			
			waitForQueueMutex();
			TimedEvent earliestEvent = eventQueue.remove();
			releaseQueueMutex();
			
			/* Making sure that the event used still is the event with the nearest target time */
			if(event == earliestEvent){
				teh.fireTimedEvent(earliestEvent);
				if(earliestEvent instanceof TimedDayChangedEvent){
					addTimedEvent(createOnDayChangedEvent());
				}
			}else{
				System.err.println("ERROR - Earliest event not equal to slept event (should not happen, report this as an issue if it is");
				/* If not, put event back on queue */
				eventQueue.add(earliestEvent);
				continue;
			}
			
		}
		
	}
	
	public void notifyDataChange(){
		notified = true;
		interrupt();
	}
	
	public void waitForQueueMutex(){
		boolean gotMutex = false;
		while(!gotMutex){
			try {
				queueMutex.acquire();
				gotMutex = true;
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
		}
	}
	
	public void releaseQueueMutex(){
		queueMutex.release();
	}
	
	public void addTimedEvent(TimedEvent timedEvent){
		waitForQueueMutex();
		eventQueue.add(timedEvent);
		releaseQueueMutex();
		notifyDataChange();
	}
	
	public long getEarliestEventTarget(){
		return eventQueue.peek().getTarget();
	}
	
}
