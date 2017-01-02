package it.unina.android.ripper.driver.exception;

/**
 * NullMessageReceived Exception
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class NullMessageReceivedException extends Exception {

	public NullMessageReceivedException() {
		super();
	}

	public NullMessageReceivedException(String message) {
		super(message);
	}

	public NullMessageReceivedException(Throwable cause) {
		super(cause);
	}

	public NullMessageReceivedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullMessageReceivedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
