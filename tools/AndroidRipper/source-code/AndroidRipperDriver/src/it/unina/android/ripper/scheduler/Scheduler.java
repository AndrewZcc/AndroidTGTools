package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

/**
 * Scheduler
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface Scheduler {
	
	/**
	 * Schedule Next Task
	 * 
	 * @return
	 */
	public Task nextTask();
	
	/**
	 * Add a Task to be scheduled
	 * 
	 * @param t Task
	 */
	public void addTask(Task t);
	
	/**
	 * Add a List of Tasks to be scheduled
	 * 
	 * @param taskList List of Tasks
	 */
	public void addTasks(TaskList taskList);
	
	/**
	 * Return the List of Tasks that can be scheduled
	 * 
	 * @return
	 */
	public TaskList getTaskList();
}
