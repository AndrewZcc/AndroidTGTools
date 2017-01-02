

AndroidRipper Driver Configuration Info
https://github.com/reverse-unina/AndroidRipper/wiki/AndroidRipperDriver-Configuration


* scheduler defaults:

	1. for (SystematicDriver)
	    it.unina.android.ripper.scheduler.BreadthScheduler;
	2. for (RandomDriver)
	    it.unina.android.ripper.termination.MaxEventsTerminationCriterion.

* termination_criterion defaults: 

	1. for (SystematicDriver)
		it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion
	2. for (RandomDriver)
		it.unina.android.ripper.scheduler.RandomScheduler

* planner default: it.unina.android.ripper.planner.HandlerBasedPlanner

* ripper_input default: it.unina.android.ripper.input.XMLRipperInput

* ripper_output default: it.unina.android.ripper.input.XMLRipperOutput


6 schedular: (The Scheduler Component schedules the events to be fired during the GUI Ripping Process.)
	1. it.unina.android.ripper.scheduler.BreadthScheduler: Chooses the next task from the TaskList in a FI-FO order.
	2. it.unina.android.ripper.scheduler.LimitedBreadthScheduler: Chooses the next task from the TaskList in a FI-FO order.
	3. it.unina.android.ripper.scheduler.DepthScheduler: Chooses the next task from the TaskList in a LI-FO order.
	4. it.unina.android.ripper.scheduler.LimitedDepthScheduler: Chooses the next task from the TaskList in a LI-FO order.
	5. it.unina.android.ripper.scheduler.RandomScheduler: Randomly chooses the next task.
	6. it.unina.android.ripper.scheduler.DebugRandomScheduler: Randomly chooses the next task; dump information about fired events.

3 termination_criterion: (The Termination Criterion determines when to stop the GUI Ripping Process.)
	1. it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion: The Process is ended when no more events to be scheduled.
	2. it.unina.android.ripper.termination.MaxEventsTerminationCriterion: The Process is ended after a predefined number of fired events.
	3. it.unina.android.ripper.termination.NullTermination: No Termination Criterion.

2 planner: (The Planner Component extracts fireable events from a GUI Status description.)
	1. it.unina.android.ripper.planner.HandlerBasedPlanner: Planner based on a relevant events on the current ActivityDescription.
	2. it.unina.android.ripper.planner.ConfigurationBasedPlanner: Planner based on a static configuration.

