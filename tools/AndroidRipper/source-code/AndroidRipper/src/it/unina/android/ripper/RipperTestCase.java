package it.unina.android.ripper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.robotium.solo.Solo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import it.unina.android.ripper.automation.IAutomation;
import it.unina.android.ripper.automation.RipperAutomation;
import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.automation.robot.RobotiumWrapperRobot;
import it.unina.android.ripper.configuration.Configuration;
import it.unina.android.ripper.extractor.IExtractor;
import it.unina.android.ripper.extractor.ReflectionExtractor;
import it.unina.android.ripper.extractor.SimpleExtractor;
import it.unina.android.ripper.extractor.screenshoot.IScreenshotTaker;
import it.unina.android.ripper.extractor.screenshoot.RobotiumScreenshotTaker;
import it.unina.android.ripper.log.Debug;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.output.XMLRipperOutput;
import it.unina.android.ripper_service.IAndroidRipperService;
import it.unina.android.ripper_service.IAnrdoidRipperServiceCallback;

/**
 * AndroidRipperTestCase
 * 
 * Communicates with the AndroidRipperDriver and executes events on the AUT.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperTestCase extends ActivityInstrumentationTestCase2 {

	/**
	 * Log TAG
	 */
	public static final String TAG = "RipperTestCase";

	/**
	 * Robot Component Instance
	 */
	IRobot robot = null;
	
	/**
	 * Automation Component Instance
	 */
	IAutomation automation = null;
	
	/**
	 * Extractor Component Instance
	 */
	IExtractor extractor = null;
	
	/**
	 * ScreenshotTaker Component Instance
	 */
	IScreenshotTaker screenshotTaker = null;

	/**
	 * Test Case Running Status
	 */
	private boolean testRunning = true;

	/**
	 * Android ActivityManager
	 */
	ActivityManager mActivityManager;
	
	/**
	 * Android Context
	 */
	Context mContext;
	
	/**
	 * Ready to Opreate Status
	 */
	private boolean readyToOperate = false;

	/**
	 * Constructor
	 */
	public RipperTestCase() {
		super(Configuration.autActivityClass);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mContext = this.getInstrumentation().getContext();
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

		bindCommunicationServices();
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {

		unbindCommunicationServices();

		if (this.automation != null) {
			Activity theActivity = this.automation.getCurrentActivity();

			try {
				this.automation.finalizeRobot();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			theActivity.finish();
		}

		try {
			this.unbindCommunicationServices();
		} catch (Throwable tr) {
		}

		super.tearDown();
	}

	/**
	 * Get Automation Component Instance
	 * 
	 * @return
	 */
	public IAutomation getAutomation() {
		return this.automation;
	}

	/**
	 * Operation done after restart
	 */
	public void afterRestart() {
		automation.setActivityOrientation(Solo.PORTRAIT);
		sleepAfterTask();
		automation.waitOnThrobber();

		// TODO: precrawling
		Debug.info(this, "Ready to operate after restarting...");
	}

	/**
	 * Main AndroidRipperTestCase Loop
	 */
	public void testApplication() {
		this.robot = new RobotiumWrapperRobot(this);
		this.automation = new RipperAutomation(this.robot);
		
		if (Configuration.EXTRACTOR_CLASS.equals("SimpleExtractor")) {
			this.extractor = new SimpleExtractor(this.robot);
		} else {
			this.extractor = new ReflectionExtractor(this.robot);
		}
		
		this.screenshotTaker = new RobotiumScreenshotTaker(this.robot);

		this.afterRestart();
		readyToOperate = true;

		//loops until test is in running status
		while (this.testRunning)
			this.robot.sleep(500);
	}

	/**
	 * Sleep After Task Completion
	 */
	private void sleepAfterTask() {
		automation.sleep(Configuration.SLEEP_AFTER_TASK);
	}

	/**
	 * IAndroidRipperService instance
	 */
	IAndroidRipperService mService = null;
	
	/**
	 * ServiceConnection to the Android Service
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IAndroidRipperService.Stub.asInterface(service);

			try {
				mService.register(mCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	/**
	 * IAnrdoidRipperServiceCallback
	 */
	IAnrdoidRipperServiceCallback mSecondaryService = null;
	private ServiceConnection mSecondaryConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mSecondaryService = IAnrdoidRipperServiceCallback.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			mSecondaryService = null;
		}
	};

	/**
	 * Bind to AndroidRipperService
	 */
	private void bindCommunicationServices() {
		Intent bindIntent = new Intent(".IAndroidRipperService");
		bindIntent.setClassName("it.unina.android.ripper_service",
				"it.unina.android.ripper_service.AndroidRipperService");
		this.getInstrumentation().getContext().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);

		bindIntent = new Intent(".IAnrdoidRipperServiceCallback");
		bindIntent.setClassName("it.unina.android.ripper_service",
				"it.unina.android.ripper_service.AndroidRipperService");
		this.getInstrumentation().getContext().bindService(bindIntent, mSecondaryConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Unbind from AndroidRipperService
	 */
	private void unbindCommunicationServices() {
		try {
			mService.unregister(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getInstrumentation().getContext().unbindService(mConnection);
		this.getInstrumentation().getContext().unbindService(mSecondaryConnection);
	}

	/**
	 * IAnrdoidRipperServiceCallback
	 * 
	 * Handles Messages from AndroidRipperDriver
	 */
	private IAnrdoidRipperServiceCallback mCallback = new IAnrdoidRipperServiceCallback.Stub() {

		@Override
		public void receive(Map message) throws RemoteException {

			Message msg = new Message(message);

			Debug.info("Recived message: " + msg.getType());

			if (readyToOperate == false) // skip
			{
				Log.v(TAG, "Not ready to operate!");
				return;
			}

			if (msg.isTypeOf(MessageType.CONFIG_MESSAGE)) {
				// config
				Message m = Message.getAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			} else if (msg.isTypeOf(MessageType.PING_MESSAGE)) {
				Message m = Message.getPongMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);

			} else if (msg.isTypeOf(MessageType.DESCRIBE_MESSAGE)) {
				try {
					String processName = getForegroundApp2();
					Log.v(TAG, "DSC : " + processName);

					if (processName.equals(Configuration.PACKAGE_NAME) == false) {
						Log.v(TAG, "DSC : wait process name");
						Message retMsg = Message.getDescribeMessage();
						retMsg.addParameter("wait", "wait");
						retMsg.addParameter("index", msg.get("index"));
						mService.send(message);
					} else {
						getInstrumentation().waitForIdleSync();

						Activity activity = getActivity();

						if (activity != null) {
							XMLRipperOutput o = new XMLRipperOutput();
							XMLRipperOutput.RUN_IN_THREAD = false;

							Message retMsg = Message.getDescribeMessage();
							retMsg.addParameter("index", msg.get("index"));

							ActivityDescription ad = extractor.extract();
							String s = o.outputActivityDescription(ad);
									
							retMsg.addParameter("xml", s);

							mService.send(retMsg);
						} else {
							Log.v(TAG, "DSC : wait activity null");
							Message retMsg = Message.getDescribeMessage();
							retMsg.addParameter("wait", "wait");
							retMsg.addParameter("index", msg.get("index"));
							mService.send(message);
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
			} else if (msg.isTypeOf(MessageType.INPUT_MESSAGE)) {
				String processName = getForegroundApp2();
				Log.v(TAG, "DSC : " + processName);

				if (processName.equals(Configuration.PACKAGE_NAME)) {
					Integer widgetId = Integer.parseInt(msg.get("widgetId"));
					String inputType = msg.get("inputType");
					String value = msg.get("value");

					try {
						automation.setInput(widgetId, inputType, value);
						Message m = Message.getAckMessage();
						m.addParameter("index", msg.get("index"));
						mService.send(m);
					} catch (Throwable t) {
						t.printStackTrace();
						Message retMsg = Message.getFailMessage();
						retMsg.addParameter("index", msg.get("index"));
						mService.send(message);
					}
				} else {
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
			} else if (msg.isTypeOf(MessageType.EVENT_MESSAGE)) {
				String processName = getForegroundApp2();
				Log.v(TAG, "DSC : " + processName);

				if (processName.equals(Configuration.PACKAGE_NAME)) {
					String widgetId = msg.get("widgetId");
					String widgetIndexString = msg.get("widgetIndex");
					Integer widgetIndex = (widgetIndexString != null) ? Integer.parseInt(widgetIndexString) : null;
					String widgetName = msg.get("widgetName");
					String widgetType = msg.get("widgetType");
					String eventType = msg.get("eventType");
					String value = msg.get("value");

					try {
						automation.fireEvent(widgetId, widgetIndex, widgetName, widgetType, eventType, value);
						Message m = Message.getAckMessage();
						m.addParameter("index", msg.get("index"));
						mService.send(m);
					} catch (Throwable t) {
						t.printStackTrace();
						Message retMsg = Message.getFailMessage();
						retMsg.addParameter("index", msg.get("index"));

						try {
							RipperTestCase.this.dumpCoverage("coverage-dump.ec");
							retMsg.addParameter("coverage_file", "coverage-dump.ec");
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						mService.send(message);
					}
				} else {
					Message retMsg = Message.getFailMessage();
					retMsg.addParameter("index", msg.get("index"));

					try {
						RipperTestCase.this.dumpCoverage("coverage-dump.ec");
						retMsg.addParameter("coverage_file", "coverage-dump.ec");
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					mService.send(message);
				}
			} else if (msg.isTypeOf(MessageType.END_MESSAGE)) {
				testRunning = false;
				// mService.send(Message.getAckMessage());
			} else if (msg.isTypeOf(MessageType.HOME_MESSAGE)) {
				// TODO:
			} else if (msg.isTypeOf(MessageType.COVERAGE_MESSAGE)) {
				// TODO: add coverage_%timestamp%.ec for async download from
				// driver
				String filename = msg.get("filename");
				// String filename = "coverage.ec";

				Log.v(TAG, "Dumping coverage data!");
				try {
					RipperTestCase.this.dumpCoverage(filename);
					Message m = Message.getAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				} catch (Exception e1) {
					Message m = Message.getNAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				}

			}
			// Messaggio ricevuto dal Driver: ESEGUI TEST CASE
			// per eseguire il ripper partendo dallo stato nuovo trovato
			else if (msg.isTypeOf(MessageType.EXECUTE_TEST_CASE_MESSAGE)) {

				String parametroRxD = msg.getParameterValue("runner"); // parametro
																		// che
																		// contiene
																		// il
																		// numero
																		// del
																		// test
																		// case
																		// da
																		// eseguire
				String parametroR = msg.getParameterValue("index");
				System.out.println("Messaggio ricevuto RipperTestCase con ParamRxD " + parametroRxD);
				System.out.println("Messaggio ricevuto RipperTestCase con index " + parametroR);
				// int valore_parametro_test_runner =
				// Integer.parseInt(parametroRxD);

				// chiamare metodo per forzare testRun i-esimo
				try {
					Log.v(TAG, "testtry : ");
					Class<?> cls = Class.forName("it.unina.android.ripper.RobotiumTest");

					Log.v(TAG, "Reflection ESEGUI TEST CASE (stato nuovo) ");

					Object obj = cls.newInstance();

					String run_metodo = "testRun" + parametroRxD;
					System.out.println("Nome metodo ESEGUI TEST CASE : " + run_metodo);

					Method mtdS = cls.getMethod(run_metodo, Solo.class);

					mtdS.setAccessible(true);

					mtdS.invoke(obj, ((RobotiumWrapperRobot)robot).getRobotiumSolo());

				} catch (IllegalArgumentException e) {
					Log.v(TAG, "IllegalArgumentException : " + e.getCause());
					String st = Log.getStackTraceString(e);
					Log.v(TAG, "IllegalArgumentException2 : " + st);
					e.printStackTrace();
				} catch (InstantiationException e) {
					Log.v(TAG, "InstantiationException : " + e.getCause());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.v(TAG, "IllegalAccessException : " + e.getCause());
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Log.v(TAG, "InvocationTargetException : " + e.getTargetException());
					String st = Log.getStackTraceString(e);
					Log.v(TAG, "InvocationTargetException2 : " + st);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Message m = Message.getExecuteTestCaseMessage();
				m.addParameter("runner", msg.get("runner"));

				mService.send(m);

			}

			else if (msg.isTypeOf(MessageType.TOTAL_NUMBER_OF_TEST_CASE_MESSAGE)) {
				// driver richiede numero totale di test_case utente da eseguire
				// reflection per get num to test case
				// il valore e' ottenuto da classe di test tramite reflection
				try {
					Log.v(TAG, "reflectiontry : ");
					Class<?> cls = Class.forName("it.unina.android.ripper.RobotiumTest");

					Object obj = null;

					obj = cls.newInstance();

					Field fields[] = cls.getFields();
					fields[0].setAccessible(true);
					Log.v(TAG, "Field ottenuto : " + fields[0].getName());

					int numeroTC = -1;

					numeroTC = (Integer) fields[0].getInt(obj);
					Log.v(TAG, "Numero ottenuto : " + numeroTC);

					String num_test_tot = Integer.toString(numeroTC);

					Message retMsg = Message.getNumTestCaseMessage();
					retMsg.addParameter("num", num_test_tot);

					mService.send(retMsg);

				} catch (IllegalArgumentException e) {
					Log.v(TAG, "IllegalArgumentException : " + e.getCause());
					String st = Log.getStackTraceString(e);
					Log.v(TAG, "IllegalArgumentException2 : " + st);

					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.v(TAG, "IllegalAccessException : " + e.getCause());
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Messaggio ricevuto dal driver per eseguire i test case utente;
			// inoltre fornisce al driver l'activity description ottenuta al termine dell-esecuzione del test case
			else if (msg.isTypeOf(MessageType.USER_TEST_MESSAGE))
			{
				
				Log.v(TAG, "MSG_TEST_UTENTE_MESSAGE_ricevuto : " + msg.getParameterValue("test")) ;
				
				String parametroRx=msg.getParameterValue("test");
				Log.v(TAG, "Parametro_rx : " + parametroRx) ;
				// indice del test case da eseguire
				//int valore_parametro_test = Integer.parseInt(parametroRx);
				
				try {
					
					Class<?> cls = Class.forName("it.unina.android.ripper.RobotiumTest");
					
					Object obj = cls.newInstance();
					
					String nome_metodo="testRun"+parametroRx;
					System.out.println("Eseguo : "+nome_metodo);
					
					Method mtdS = cls.getMethod(nome_metodo, Solo.class);
					
					mtdS.setAccessible(true);
					
					mtdS.invoke(obj,((RobotiumWrapperRobot)robot).getRobotiumSolo()); 
				
					
				} catch (IllegalArgumentException e) {
					Log.v(TAG, "IllegalArgumentException : " +e.getCause());
					String st=Log.getStackTraceString(e);
					Log.v(TAG, "IllegalArgumentException2 : " + st);
							
							
					e.printStackTrace();
				} catch (InstantiationException e) {
					Log.v(TAG, "InstantiationException : " +e.getCause() );
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.v(TAG, "IllegalAccessException : " +e.getCause() );
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Log.v(TAG, "InvocationTargetException : " +e.getTargetException() );
					String st=Log.getStackTraceString(e);
					Log.v(TAG, "InvocationTargetException2 : " + st);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
					try
					{
						String processName = getForegroundApp2();
						Log.v(TAG, "RUN : " + processName);
						
						if(processName.equals(Configuration.PACKAGE_NAME) == false)
						{
							Log.v(TAG, "RUN : wait process name");
		        			Message retMsg = Message.getDescribeMessage();
		        			retMsg.addParameter("wait","wait");
		        			retMsg.addParameter("index", msg.get("index"));
							mService.send(message);	
										
						}
						else
						{
			        		Activity activity = getActivity();
							
			        		if (activity != null)
			        		{
			        			XMLRipperOutput o = new XMLRipperOutput();
			        			XMLRipperOutput.RUN_IN_THREAD = false;
			        			
			        			Message retMsg = Message.getDescribeMessage();
			        			retMsg.addParameter("index", msg.get("index"));
			        			
			        			ActivityDescription ad = extractor.extract(); 
			        			String s = o.outputActivityDescription(ad);
			        			retMsg.addParameter("xml", s);  
			        			
			        			mService.send(retMsg);  
			        			
			        		}
			        		else
			        		{
			        			Log.v(TAG, "DSC : wait activity null");
			           			Message retMsg = Message.getDescribeMessage();
			        			retMsg.addParameter("wait","wait");
			        			retMsg.addParameter("index", msg.get("index"));
								mService.send(message);	
			        		}
						}
					}
					catch(Throwable t)
					{
						t.printStackTrace();
						Message retMsg = Message.getFailMessage();
	        			retMsg.addParameter("index", msg.get("index"));
						mService.send(message);	
					}
				
				
			}
			else {
				Message m = Message.getNAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			}
		}

	};

	/**
	 * Dump Emma Coverage File
	 * 
	 * @param filename file name
	 * @throws Exception
	 */
	protected void dumpCoverage(String filename) throws Exception {
		Log.v(TAG, "Dumping coverage data!");
		java.io.File coverageFile = new java.io.File("/data/data/" + Configuration.PACKAGE_NAME + "/" + filename); // chmod
																													// 777
																													// from
																													// adb
																													// shell
		Class<?> emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
		Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData", java.io.File.class, boolean.class,
				boolean.class);
		// dumpCoverageMethod.invoke(null, null, false, false);
		dumpCoverageMethod.invoke(null, coverageFile, false, false);
	}

	/**
	 * Get the app in foreground
	 * 
	 * @return name of the app
	 */
	private String getForegroundApp2() {
		try {
			if (mService != null)
				return mService.getForegroundProcess();
		} catch (Throwable t) {
		}

		return "";
	}
}
