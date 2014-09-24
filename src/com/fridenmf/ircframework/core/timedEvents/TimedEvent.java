package com.fridenmf.ircframework.core.timedEvents;

public abstract class TimedEvent implements Comparable<TimedEvent>{

	private long target;
	private long identifier;
	
	public TimedEvent(long target){
		//TODO make identifier
		this.target = target;
	}

	public long getTarget() {
		return target;
	}

	public long getIdentifier() {
		return identifier;
	}

	@Override
	public int compareTo(TimedEvent o) {
		return Long.compare(this.target, o.getTarget());
	}
	
	public abstract String getDescription();
	
}
