package it.unina.android.ripper.installer.console;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.installer.legacy.SearchableManifest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class GuiRipperInstaller {
	
	public static final String AUT_PATH = "AUT_PATH";
	public static final String TEST_SUITE_PATH = "TEST_SUITE_PATH";
	public static final String SERVICE_APK_PATH = "SERVICE_APK_PATH";
	public static final String APP_PACKAGE = "APP_PACKAGE";
	public static final String APP_MAIN_ACTIVITY = "APP_MAIN_ACTIVITY";
	public static final String AVD_NAME = "AVD_NAME";
	public static final String AVD_PORT = "AVD_PORT";
	public static final String LOG_FILE = "LOG_FILE";
	public static final String EXTRACTOR_CLASS = "EXTRACTOR_CLASS";
	
	public static String shell_CMD = OSSpecific.getShellCommand();
	
	Properties config;
	String currentPath;
	
	public boolean deployFailed = false;
	
	public GuiRipperInstaller() {
		this("ripper.properties");
	}

	public GuiRipperInstaller(String fileName) {
		super();
		this.config = this.loadConfigurationFile(fileName);

		if (this.config == null) {
			throw new RuntimeException("Can't load configuration!");
		}
		
		Path currentRelativePath = Paths.get("");
		this.currentPath = currentRelativePath.toAbsolutePath().toString();
	}

	public void install() {
		
		//system variables validation
		String java_home = System.getenv("JAVA_HOME");
		if (java_home == null || java_home.equals("")) {
			throw new RuntimeException("JAVA_HOME not set!");
		}
		
		String android_sdk = System.getenv("ANDROID_HOME");
		if (android_sdk == null || android_sdk.equals("")) {
			throw new RuntimeException("ANDROID_HOME not set!");
		}
		
		//String path = System.getenv("PATH");
		
		if (validateCommand("java") == false) {
			throw new RuntimeException("java not in PATH");
		}
		
		//if (validateCommand("android.bat list avd") == false) {
			if (validateCommand("android list avd") == false) {
				throw new RuntimeException("android not in PATH");
			}
		//}
		
		//if (validateCommand("ant.bat") == false) {
			if (validateCommand("ant") == false) {
				throw new RuntimeException("ant not in PATH");
			}
		//}
		
		if (validateCommand("adb") == false) {
			throw new RuntimeException("adb not in PATH");
		}

		if (validateCommand("emulator") == false) {
			throw new RuntimeException("emulator not in PATH");
		}
		
		//System.exit(0);
		
		String autPath = loadAndValidatePath(AUT_PATH);
		String testSuitePath = loadAndValidatePath(TEST_SUITE_PATH);
		String serviceApkPath = loadAndValidatePath(SERVICE_APK_PATH);
		//String appPackage = loadAndValidateString(APP_PACKAGE);
		//String appMainActivity = loadAndValidateString(APP_MAIN_ACTIVITY);
		String avdName = loadAndValidateString(AVD_NAME);
		Integer avdPort = loadAndValidateInteger(AVD_PORT);

		String logfile = this.config.getProperty(LOG_FILE, "install_log.txt");
		
		String extractorClass = this.config.getProperty(EXTRACTOR_CLASS, "SimpleExtractor");
		
		//check avd
		if (checkAVD(avdName) == false) {
			throw new RuntimeException("AVD does not exist!");
		}
		
		if (new File(serviceApkPath+"/AndroidRipperService.apk").exists() == false) {
			throw new RuntimeException(serviceApkPath+"/AndroidRipperService.apk does not exist!");
		}
		
		String serviceApKFilePath = new File(serviceApkPath+"/AndroidRipperService.apk").getAbsolutePath();
		
		//get application infos
		String[] appInfo = getAppInfo(autPath);
		String appPackage = appInfo[0];
		String appMainActivity = appInfo[1];
		
		//bootEmulator(avdName, avdPort);
		//waitForEmulator(avdPort);
		
		avdDevices();
		
		//replace strings
		println("Editing 'Configuration.java'");
		replaceStringsInFile(testSuitePath+"/src/it/unina/android/ripper/configuration/Configuration.java.template", testSuitePath+"/src/it/unina/android/ripper/configuration/Configuration.java", appPackage, appMainActivity, extractorClass);
		println("Editing 'AndroidManifest.xml'");
		replaceStringsInFile(testSuitePath+"/AndroidManifest.xml.template", testSuitePath+"/AndroidManifest.xml", appPackage, appMainActivity, extractorClass);
		
		try
		{
			println(">>> STEP-1: update AUT project...");
			//Runtime.getRuntime().exec("cmd /C android update project --path "+autPath).waitFor();
			System.out.println("android update project --path "+autPath);
			//execCommand("android update project --path "+autPath);
			execCommand("android update project --path "+autPath + " --subprojects --target android-17");
			
			println(">>> STEP-1: update test-suite project...");
			//Runtime.getRuntime().exec("cmd /C android update test-project -p "+testSuitePath+" --main "+autPath).waitFor();
			System.out.println("android update test-project -p "+testSuitePath+" --main "+autPath);
			execCommand("android update test-project -p "+testSuitePath+" --main "+autPath);
			
			/*
			println(">>> STEP-2: compiling AUT project...");
			System.out.println("ant -f "+autPath+"/build.xml debug");
			execCommand("ant -f "+autPath+"/build.xml debug");
			*/
			
			println(">>> STEP-2: compiling and install test-suite project...");
			//Runtime.getRuntime().exec("cmd /C \" cd /d "+testSuitePath+" & ant emma debug install\"").waitFor();
			deploy(testSuitePath, logfile);
			/*
			System.out.println("ant -f "+testSuitePath+"/build.xml release");
			execCommand("ant -f "+testSuitePath+"/build.xml emma debug");
			*/
			
			if (deployFailed == false) {
				println(">>> STEP-3: install ripper-serivice apk...");
				//Runtime.getRuntime().exec("cmd /C adb shell mkdir /data/data/"+app_package+"/files").waitFor();
				System.out.println("adb install "+serviceApKFilePath);
				execCommand("adb install "+serviceApKFilePath);
				
				/*
				// Install AUT
				println(">>> STEP-3: install AUT apk...");
				execCommand("adb install "+ autPath + "/bin/***_debug.apk");
				// Install AndroidRipper.apk
				println(">>> STEP-3: install test-suite project: AndroidRipper apk...");
				execCommand("adb install "+ testSuitePath + "/bin/AndroidRipper.apk");
				*/
				
				println("\nadb shell mkdir...");
				//Runtime.getRuntime().exec("cmd /C adb shell mkdir /data/data/"+app_package+"/files").waitFor();
				System.out.println("su adb shell mkdir /data/data/"+appPackage+"/files");
				execCommand("su adb shell mkdir /data/data/"+appPackage+"/files");
				
				println("\nadb chmod 777...");
				//Runtime.getRuntime().exec("cmd /C adb chmod 777 /data/data/"+app_package+"/files").waitFor();
				System.out.println("su adb shell chmod 777 /data/data/"+appPackage+"/files");
				execCommand("su adb shell chmod 777 /data/data/"+appPackage+"/files");
				
				println("\nadb shell rm...");
				//Runtime.getRuntime().exec("cmd /C adb shell rm /data/data/"+app_package+"/files/*").waitFor();
				System.out.println("su adb shell rm /data/data/"+appPackage+"/files/*");
				execCommand("su adb shell rm /data/data/"+appPackage+"/files/*", false);
				
				println("Installation finished!");
			} else {
				println("Build failed! See installation log file for details!");
			}
				
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		unlockEmulator();
	}

	protected void bootEmulator(String avdName, Integer avdPort) {
		println("Booting Emulator!");
		//Actions.START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = 0;
		Actions.startEmulatorNoSnapshotLoadWipeData(avdName, avdPort);
	}
	
	protected void waitForEmulator(Integer avdPort) {
		println("Waiting for Emulator...");
		Actions.waitForEmulator(avdPort);
		println("Emulator online!");
	}

	protected void avdDevices() {
		println("adb devices...");
		try {
			Actions.tools.adb("devices").waitForSuccess();
		} catch (InterruptedException | IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error: Please start your AVD!");
			e1.printStackTrace();
		}
	}
	
	protected void unlockEmulator() {
		println("Unlocking Emulator...");
	}
	
	public String loadAndValidatePath(String param) {

		String path = this.config.getProperty(param, null);

		if (path == null) {
			throw new RuntimeException(param + " not set!");
		} else {
			if (path.contains("%PWD%")) {
				path = path.replace("%PWD%", this.currentPath);
			}
		}
			
		if (new File(path).exists() == false) {
			throw new RuntimeException(param + " not exists!");
		} else if (new File(path).isDirectory() == false) {
			throw new RuntimeException(param + " isn't a directory!");
		}

		return path;
	}

	public String loadAndValidateString(String param) {
		String parametro = this.config.getProperty(param, null);

		if (parametro == null) {
			throw new RuntimeException(param + " not set!");
		}

		return parametro;
	}

	public Integer loadAndValidateInteger(String param) {
		String parametro = this.config.getProperty(param, "5554");

		if (parametro == null) {
			throw new RuntimeException(param + " not set!");
		}

		Integer parametroInteger = null;
		try {
			parametroInteger = Integer.valueOf(parametro);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException(param + " is not a valid integer value!");
		}

		return parametroInteger;
	}

	private Properties loadConfigurationFile(String fileName) {
		Properties conf = new Properties();

		try {
			conf.load(new FileInputStream(fileName));
			return conf;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static void println(String line) {
		System.out.println("[" + System.currentTimeMillis() + "] " + line);
	}

	public void deploy(String testSuitePath, String logfile) {
			
		try {
			final PrintStream logFileStream = new PrintStream( new FileOutputStream(logfile, true) );
			
			final Process p = Runtime.getRuntime().exec(shell_CMD + "ant -buildfile "+testSuitePath+"/build.xml emma debug install");

			Thread t = new Thread() {
				public void run() {
					try {
						String line = "";
						BufferedReader input = new BufferedReader(
								new InputStreamReader(p.getInputStream()));
						while ((line = input.readLine()) != null) {
							
							if (line.contains("Build Failed")) {
								deployFailed = true;
							}
							
							logFileStream.println(line);
						}
						input.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			t.start();
			p.waitFor();
			
			logFileStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void execCommand(String cmd) {
		execCommand(cmd, true);
	}

	public static void execCommand(String cmd, boolean wait) {
		try {
			final Process p = Runtime.getRuntime().exec(shell_CMD + cmd);

			Thread t = new Thread() {
				public void run() {
					try {
						String line = "";
						BufferedReader input = new BufferedReader(
								new InputStreamReader(p.getInputStream()));
						while ((line = input.readLine()) != null) {
							println(line);
						}
						input.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			t.start();
			p.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void replaceStringsInFile(String templateFilePath, String outputFilePath, String appPackage, String appMainActivity, String extractorClass) {
		try {
			
			Path templatePath = Paths.get(templateFilePath);
			Path outPath = Paths.get(outputFilePath);
			Charset charset = StandardCharsets.UTF_8;
	
			String content = new String(Files.readAllBytes(templatePath), charset);
			content = content.replaceAll("%%_PACKAGE_NAME_%%", appPackage);
			content = content.replaceAll("%%_CLASS_NAME_%%", appMainActivity);
			content = content.replaceAll("%%_EXTRACTOR_CLASS_%%", extractorClass);
			
			Files.write(outPath, content.getBytes(charset));

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected boolean checkAVD(String avdName) {
		try {
			Process proc = Runtime.getRuntime().exec(OSSpecific.getAndroidListAVDCommand());			
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        String s;
	        while ((s = stdInput.readLine()) != null) {	        	
	        	if(s.contains("Name: ")) {
	        		String name = s.substring(s.indexOf("Name: ")+6).trim();
	        		
					if (name.equals(avdName)) {
						return true;
					}
	        	}
	        }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return false;
	}
	
	protected boolean validateCommand(String cmd) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			try {
				proc.destroy();
			} catch (Exception ex) {}
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	protected String[] getAppInfo(String sourcePath) {
		String[] ret = new String[2];
		
		String path = sourcePath + File.separator + "AndroidManifest.xml";
		SearchableManifest doc = new SearchableManifest (path);

		String thePackage = doc.parseXpath(MANIFEST_XPATH);
		String theClass = doc.parseXpath(CLASS_XPATH);
				
		String dot = (theClass.endsWith(".") || theClass.startsWith("."))?"":".";
		theClass = thePackage + dot + theClass;
		
		ret[0] = thePackage;
		ret[1] = theClass;
		
		return ret;
	}
	
	public final static String MANIFEST_XPATH = "//manifest[1]/@package";
	public final static String CLASS_XPATH = "//activity[intent-filter/action/@name='android.intent.action.MAIN'][1]/@name";
}
