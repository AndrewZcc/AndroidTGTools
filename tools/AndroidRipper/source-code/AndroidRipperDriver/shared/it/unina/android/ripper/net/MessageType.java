package it.unina.android.ripper.net;

/**
 * String Constants representing Message Types
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MessageType {
	public static final String CONFIG_MESSAGE = "CFG";
	public static final String EVENT_MESSAGE = "EVT";
	public static final String INPUT_MESSAGE = "INP";
	public static final String DESCRIBE_MESSAGE = "DSC";
	public static final String END_MESSAGE = "END";
	public static final String ACK_MESSAGE = "ACK";
	public static final String NACK_MESSAGE = "NACK";
	public static final String PING_MESSAGE = "PING";
	public static final String PONG_MESSAGE = "PONG";
	public static final String FAIL_MESSAGE = "FAIL";
	public static final String CRASH_MESSAGE = "CRASH";
	public static final String COVERAGE_MESSAGE = "COVER";
	public static final String HOME_MESSAGE = "HOME";
	public static final String USER_TEST_MESSAGE = "TEST"; // NOTE: usato per exec i-esimo testcase
	public static final String TOTAL_NUMBER_OF_TEST_CASE_MESSAGE = "NUMTC"; // NOTE: usato per ricavare il numero tot di testcase presenti
	public static final String EXECUTE_TEST_CASE_MESSAGE = "ETC"; // NOTE: per rieseguire testcase utente da driver 	// messaggio per forzare exec test case
}
