package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

/**
 * Chooses the next task from the TaskList in a FI-FO order.
 * The length of the sequence of events can be limited.  
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class LimitedDepthBreadthScheduler implements Scheduler {

	private TaskList taskList;
	private int limit = 0;	
	
	/**
	 * Constructor
	 * 
	 * @param limit limit of the length of the sequence of events
	 */
	public LimitedDepthBreadthScheduler(int limit)
	{
		super();
		this.taskList = new TaskList();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#nextTask()
	 */
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			Task t = null;
			
			//todo: TESTING
			do
			{
				t = taskList.remove(0);
			}
			while (t.size() > limit && t != null);
			
			return t;
		}
		else
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#addTask(it.unina.android.ripper.model.Task)
	 */
	@Override
	public void addTask(Task t) {
		this.taskList.add(t);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#addTasks(it.unina.android.ripper.model.TaskList)
	 */
	@Override
	public void addTasks(TaskList taskList) {
		this.taskList.addAll(taskList);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#getTaskList()
	 */
	@Override
	public TaskList getTaskList() {
		return this.taskList;
	}

}
