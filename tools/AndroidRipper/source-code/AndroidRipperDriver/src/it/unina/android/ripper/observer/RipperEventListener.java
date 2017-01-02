package it.unina.android.ripper.observer;

/**
 * Ripper Proces Event Listener Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface RipperEventListener {
	/**
	 * Notify new log line
	 * 
	 * @param log
	 */
	public void ripperLog(String log);
	
	/**
	 * Notify status change
	 * 
	 * @param status
	 */
	public void ripperStatusUpdate(String status);
	
	/**
	 * Notify task ended
	 */
	public void ripperTaskEneded();
	
	/**
	 * Notify ripping process ended
	 */
	public void ripperEneded();
}
