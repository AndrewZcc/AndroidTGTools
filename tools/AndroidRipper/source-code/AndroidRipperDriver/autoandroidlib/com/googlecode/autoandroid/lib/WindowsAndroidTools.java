package com.googlecode.autoandroid.lib;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WindowsAndroidTools extends AndroidTools {

	private Process2 startBatch(String batch, String... args) throws IOException {
		List<String> parameters = new ArrayList<String>();
		parameters.addAll(asList("/c", locateTool(batch)));
		parameters.addAll(asList(args));
		
		String systemRoot = System.getenv("SystemRoot");
		if (systemRoot == null) {
			throw new IllegalStateException("Please set (or pass through) the SystemRoot environment variable.");
		}
		
		return start(systemRoot + "\\system32\\cmd.exe", parameters);
	}
	
	@Override
	protected Process2 start(String binary, List<String> args) throws IOException {
		return super.start(quote(binary), quote(args));
	}

	private List<String> quote(List<String> unquoteds) {
		List<String> quoted = new ArrayList<String>();
		for (String unquoted : unquoteds) {
			quoted.add(quote(unquoted));
		}
		return quoted;
	}
	
	private String quote(String unquoted) {
		return "\"" + unquoted + "\"";
	}
	
	@Override
	public Process2 aapt(String... args) throws IOException {
		return startTool("aapt.exe", args);
	}
	
	@Override
	public Process2 adb(String... args) throws IOException {
		return startTool("adb.exe", args);
	}
	
	@Override
	public Process2 aidl(String... args) throws IOException {
		return startTool("aidl.exe", args);
	}
	
	@Override
	public Process2 apkBuilder(String... args) throws IOException {
		return startBatch("apkbuilder.bat", args);
	}
	
	@Override
	public Process2 dx(String... args) throws IOException {
		return startBatch("dx.bat", args);
	}
	
	@Override
	public Process2 emulator(String... args) throws IOException {
		return startTool("emulator.exe", args);
	}

	@Override
	public Process2 ddms(String... args) throws IOException {
		return startBatch("ddms.bat", args);
	}

	@Override
	public Process2 dmtracedump(String... args) throws IOException {
		return startTool("dmtracedump.exe", args);
	}

	@Override
	public Process2 mksdcard(String... args) throws IOException {
		return startTool("mksdcard.exe", args);
	}

	@Override
	public Process2 sqlite3(String... args) throws IOException {
		return startTool("sqlite3.exe", args);
	}

	@Override
	public Process2 traceview(String... args) throws IOException {
		return startBatch("traceview.bat", args);
	}

}
