package com.googlecode.autoandroid.lib;

import java.io.IOException;

public class UnixAndroidTools extends AndroidTools {

	@Override
	public Process2 aapt(String... args) throws IOException {
		return startTool("aapt", args);
	}

	@Override
	public Process2 adb(String... args) throws IOException {
		return startTool("adb", args);
	}
	
	@Override
	public Process2 aidl(String... args) throws IOException {
		return startTool("aidl", args);
	}
	
	@Override
	public Process2 apkBuilder(String... args) throws IOException {
		return startTool("apkbuilder", args);
	}
	
	@Override
	public Process2 ddms(String... args) throws IOException {
		return startTool("ddms", args);
	}
	
	@Override
	public Process2 dmtracedump(String... args) throws IOException {
		return startTool("dmtracedump", args);
	}
	
	@Override
	public Process2 dx(String... args) throws IOException {
		return startTool("dx", args);
	}
	
	@Override
	public Process2 emulator(String... args) throws IOException {
		return startTool("emulator", args);
	}

	@Override
	public Process2 mksdcard(String... args) throws IOException {
		return startTool("mksdcard", args);
	}

	@Override
	public Process2 sqlite3(String... args) throws IOException {
		return startTool("sqlite3", args);
	}

	@Override
	public Process2 traceview(String... args) throws IOException {
		return startTool("traceview", args);
	}
	
}
