package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public abstract class ScheduleCommand {
private boolean scheduled;
public boolean isScheduled() {
	return scheduled;
}


public void setScheduled(boolean scheduled) {
	this.scheduled = scheduled;
}


private boolean cancelled;

public boolean isCancelled() {
	return cancelled;
}


public void setCancelled(boolean cancelled) {
	this.cancelled = cancelled;
}


public void scheduleExecute(){
	if(!scheduled){
		scheduled=true;
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				scheduled=false;
				fireExecute();
			}
			
		});
	}else{
		//LogUtils.log("scheduleExecute:debug-blocked");
	}
}


public abstract void fireExecute();
}
