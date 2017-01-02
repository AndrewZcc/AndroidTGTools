package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

/**
 * Chooses the next task from the TaskList in a FI-FO order.  
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class BreadthScheduler implements Scheduler {

	/**
	 * Task List
	 */
	private TaskList taskList;
	
	/**
	 * Constructor
	 */
	public BreadthScheduler()
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
			return this.taskList.remove(0);
		else
			return null;
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
