package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

import java.util.Random;

/**
 * Randomly chooses the next task.
 *  
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RandomScheduler implements Scheduler {

	/**
	 * Random Seed
	 */
	private long RANDOM_SEED = 1;
	
	/**
	 * Task List
	 */
	protected TaskList taskList;
	
	/**
	 * Random Generator
	 */
	protected Random random;
	
	/**
	 * Constructor
	 * 
	 * @param seed random seed
	 */
	public RandomScheduler(long seed)
	{
		super();
		this.taskList = new TaskList();
		this.RANDOM_SEED = seed;
		random = new Random(RANDOM_SEED);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.scheduler.Scheduler#nextTask()
	 */
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			int pos = (int)( random.nextInt( taskList.size() ) );
			
			Task t = taskList.get(pos);
			taskList.clear();
			
			return t;
		}
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
