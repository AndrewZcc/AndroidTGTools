package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;

/**
 * Randomly chooses the next task.
 * Output information about fired events.
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class DebugRandomScheduler extends RandomScheduler {

	/**
	 * Event Counter Element
	 * 
	 * @author Nicola Amatucci - REvERSE
	 *
	 */
	private class EventCounter {
		Event e;
		int count = 0;
		
		EventCounter(Event e)
		{
			count = 0;
		}
		
		public void inc()
		{
			count++;
		}
		
		public int getCount()
		{
			return count;
		}
	}
	
	/**
	 * Event Counter
	 * 
	 * NOTE: hash(Acctivity, widgetid, widgetname, eventtype), eventcounter 
	 */
	private Hashtable<String, EventCounter> eventCounters = null;
	
	/**
	 * Add Event to the counter
	 * 
	 * @param t task
	 */
	private void addEvent(Task t)
	{
		Event e = t.get(0);
		String key = ((e.getWidget()!=null)?e.getWidget().getId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getName() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getTextualId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getSimpleType() + ":":"") + e.getInteraction();
		
		EventCounter evtCnt = eventCounters.get(key); 
		if (evtCnt == null)
		{
			evtCnt = new EventCounter(e);
			eventCounters.put(key, evtCnt);
		}
	}
	
	/**
	 * Add Performed Event to the counter
	 * 
	 * @param t task
	 */
	private void addEventPerformed(Task t)
	{
		Event e = t.get(0);
		String key = ((e.getWidget()!=null)?e.getWidget().getId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getName() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getTextualId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getSimpleType() + ":":"") + e.getInteraction();
		
		EventCounter evtCnt = eventCounters.get(key); 	
		evtCnt.inc();
	}
	
	/**
	 * Output the event list to a file named events.txt
	 */
	private void outputEventList()
	{
		String report = "";
		for (String k : eventCounters.keySet())
		{
			EventCounter ec = eventCounters.get(k);
			report += k + " -> " + ec.getCount() + "\n";
		}
		
		try
		{
			FileWriter fileWritter = new FileWriter("events.txt", false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(report);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param seed Random Seed
	 */
	public DebugRandomScheduler(long seed)
	{
		super(seed);
		eventCounters = new Hashtable<String, EventCounter>();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.RandomScheduler#nextTask()
	 */
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			int pos = (int)( random.nextInt( taskList.size() ) );
			
			Task t = taskList.get(pos);
			taskList.clear();
			
			this.addEventPerformed(t);
			outputEventList();
			return t;
		}
		else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.RandomScheduler#addTask(it.unina.android.ripper.model.Task)
	 */
	@Override
	public void addTask(Task t) {
		super.addTask(t);
		this.addEvent(t);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.RandomScheduler#addTasks(it.unina.android.ripper.model.TaskList)
	 */
	@Override
	public void addTasks(TaskList taskList) {
		super.addTasks(taskList);
		for(Task t : taskList)
			this.addEvent(t);
	}
}
