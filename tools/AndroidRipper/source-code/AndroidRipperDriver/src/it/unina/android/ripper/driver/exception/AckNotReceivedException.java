package it.unina.android.ripper.driver.exception;

/**
 * AckNotReceived Exception
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AckNotReceivedException extends Exception {

	public AckNotReceivedException() {
		super();
	}

	public AckNotReceivedException(String message) {
		super(message);
	}

	public AckNotReceivedException(Throwable cause) {
		super(cause);
	}

	public AckNotReceivedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AckNotReceivedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
