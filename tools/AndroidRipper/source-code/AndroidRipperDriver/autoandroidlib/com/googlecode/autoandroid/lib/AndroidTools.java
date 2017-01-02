package com.googlecode.autoandroid.lib;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/** Find and start various tools in the android sdk. */
public abstract class AndroidTools {
	
	private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
	private static final String PATH_SEP = System.getProperty("path.separator");

	public static final String ANDROID_HOME_ENV_VAR = "ANDROID_HOME";
	public static final String ANDROID_SDK_ENV_VAR = "ANDROID_SDK";
	public static final String ANDROID_HOME_SYS_PROP = "android-home";
	public static final String OS_FAMILY_SYS_PROP = "os-family";
	public static String DETECTED_OS = null;
	
	public static AndroidTools get() {
		String osFamily = System.getProperty(OS_FAMILY_SYS_PROP);
		
		if (osFamily == null) {
			// boosted straight from ant:
			if (OS_NAME.indexOf("windows") > -1) {
				osFamily = "windows";
			} else if (PATH_SEP.equals(":") && OS_NAME.indexOf("openvms") == -1 &&
					(OS_NAME.indexOf("mac") == -1 || OS_NAME.endsWith("x"))) {
				osFamily = "unix";
			} else {
				throw new IllegalStateException("Can't infer your OS family.  Please set the " + OS_FAMILY_SYS_PROP + " system property to one of 'windows', 'unix'.");
			}
		}
		DETECTED_OS = osFamily;
		
		return forOsFamily(osFamily);
	}

	private static ConcurrentHashMap<String, AndroidTools> androidTools = new ConcurrentHashMap<String, AndroidTools>();
	
	public static AndroidTools forOsFamily(String osFamily) {
		AndroidTools instance = androidTools.get(osFamily);
		
		if (instance == null) {
			AndroidTools newInstance = null;
			if (osFamily.equals("windows")) {
				newInstance = new WindowsAndroidTools();
			} else if (osFamily.equals("unix")) {
				newInstance = new UnixAndroidTools();
			} else {
				throw new UnsupportedOperationException("Don't know how to start android tools on " + osFamily);
			}
			
			instance = androidTools.putIfAbsent(osFamily, newInstance);
			if (instance == null) instance = newInstance;
		}
		
		return instance;
	}
	
	private String androidHome;
	
	public void setAndroidHome(String androidHome) {
		this.androidHome = androidHome;
	}
	
	public String getAndroidHome() {
		String androidHome = this.androidHome;
		if (androidHome == null) androidHome = System.getProperty(ANDROID_HOME_SYS_PROP);
		if (androidHome == null) androidHome = System.getenv(ANDROID_HOME_ENV_VAR);
		if (androidHome == null) androidHome = System.getenv(ANDROID_SDK_ENV_VAR);
				
		if (androidHome == null) {
			throw new IllegalStateException("Can't find the android sdk home.  " +
					"Set the " + ANDROID_HOME_SYS_PROP + " system property or either of the " +
					ANDROID_HOME_ENV_VAR + " or " + ANDROID_SDK_ENV_VAR + " environment variables to your sdk root.");
		}

		File androidHomeFile = new File(androidHome);
		if (!androidHomeFile.exists()) {
			throw new IllegalStateException("" + androidHome + " doesn't exist.");
		}

		if (!androidHomeFile.isDirectory()) {
			throw new IllegalStateException("" + androidHome + " isn't a directory.");			
		}
		
		return androidHome;
	}
	
	protected Process2 startTool(String tool, String... args) throws IOException {	
		return startTool(tool, asList(args));
	}
	
	protected Process2 start(String binary, List<String> args) throws IOException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command().add(binary);
		pb.command().addAll(args);

		return new Process2(pb.start());
	}

	protected Process2 startTool(String tool, List<String> args) throws IOException {	
		return start(locateTool(tool), args);
	}

	protected String locateTool(String tool) {
		String path = null;
		String androidHome = getAndroidHome();

		if (tool.equals("emulator") && DETECTED_OS.equals("unix")) {
			return new File(androidHome, "tools/emulator").getAbsolutePath();
		}
		
		for (File file : new File(androidHome, "tools").listFiles()) {
			if (!file.getName().toLowerCase().startsWith(tool)) continue;
			path = file.getAbsolutePath();
		}
		
		for (File file : new File(androidHome, "platform-tools").listFiles()) {
			if (!file.getName().toLowerCase().startsWith(tool)) continue;
			path = file.getAbsolutePath();
		}

		if (path == null) {
			throw new IllegalStateException("Can't find " + tool + " inside the sdk at " + androidHome);
		}
		return path;
	}
	
	public abstract Process2 aapt(String... args) throws IOException;
	public Process2 aapt(List<String> args) throws IOException {
		return aapt(args.toArray(new String [0]));
	}
	
	public abstract Process2 aidl(String... args) throws IOException;
	public Process2 aidl(List<String> args) throws IOException {
		return aidl(args.toArray(new String [0]));
	}
	
	public abstract Process2 apkBuilder(String... args) throws IOException;
	public Process2 apkBuilder(List<String> args) throws IOException {
		return apkBuilder(args.toArray(new String [0]));
	}
	
	public abstract Process2 adb(String... args) throws IOException;
	public Process2 adb(List<String> args) throws IOException {
		return adb(args.toArray(new String [0]));
	}
	
	public abstract Process2 ddms(String... args) throws IOException;
	public Process2 ddms(List<String> args) throws IOException {
		return ddms(args.toArray(new String [0]));
	}
	
	public abstract Process2 dmtracedump(String... args) throws IOException;
	public Process2 dmtracedump(List<String> args) throws IOException {
		return dmtracedump(args.toArray(new String [0]));
	}
	
	public abstract Process2 dx(String... args) throws IOException;
	public Process2 dx(List<String> args) throws IOException {
		return dx(args.toArray(new String [0]));
	}
	
	public abstract Process2 emulator(String... args) throws IOException;
	public Process2 emulator(List<String> args) throws IOException {
		return emulator(args.toArray(new String [0]));
	}
	
	public abstract Process2 mksdcard(String... args) throws IOException;
	public Process2 mksdcard(List<String> args) throws IOException {
		return mksdcard(args.toArray(new String [0]));
	}
	
	public abstract Process2 sqlite3(String... args) throws IOException;
	public Process2 sqlite3(List<String> args) throws IOException {
		return sqlite3(args.toArray(new String [0]));
	}
	
	public abstract Process2 traceview(String... args) throws IOException;
	public Process2 traceview(List<String> args) throws IOException {
		return traceview(args.toArray(new String [0]));
	}
	
}
