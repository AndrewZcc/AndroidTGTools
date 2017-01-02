package com.googlecode.autoandroid.lib;

import java.io.BufferedReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

/** Extends Process by wrapping one. */
public class Process2 {

	private final static Appendable DEV_NULL = new Appendable() {

		public Appendable append(CharSequence csq) throws IOException { return this; }
		public Appendable append(char c) throws IOException { return this; }
		public Appendable append(CharSequence csq, int start, int end) throws IOException { return this; }
		
	};
	
	private final static Set<Process2> processes = new HashSet<Process2>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (Process2 process : processes) {
					try {
						process.destroy();
					} catch (Exception ignored) {}
				}
			}
		});
	}
	
	private final Process process;

	public Process2(Process process) {
		processes.add(this);
		this.process = process;		
	}
	
	public void destroy() {
		process.destroy();
	}

	public int exitValue() {
		return process.exitValue();
	}

	public Process2 waitFor() throws InterruptedException {
		process.waitFor();
		return this;
	}
	
	public Process2 waitForSuccess() throws InterruptedException {
		int exitValue = waitFor().exitValue();
		if (exitValue != 0) throw new RuntimeException("Tool return " + exitValue);
		return this;
	}

	private Writer wrap(OutputStream sink) {
		return new OutputStreamWriter(sink);
	}
	
	private Reader wrap(InputStream sink) {
		return new InputStreamReader(sink);
	}
	
	public OutputStream getStdin() {
		return process.getOutputStream();
	}

	public Writer getStdinWriter() {
		return wrap(process.getOutputStream());
	}
	
	public InputStream getStdout() {
		return process.getInputStream();
	}
	
	public BufferedReader getStdoutReader() {
		return new BufferedReader(wrap(process.getInputStream()));
	}
	
	public InputStream getStderr() {
		return process.getErrorStream();
	}
	
	public BufferedReader getStderrReader() {
		return new BufferedReader(wrap(process.getErrorStream()));
	}
	
	private void connect(final Readable source, final Appendable sink) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				CharBuffer cb = CharBuffer.wrap(new char [256]);
				try {
					while (source.read(cb) != -1) {
						cb.flip();
						sink.append(cb);
						cb.clear();
					}

					if (sink instanceof Flushable) {
						((Flushable)sink).flush();
					}
				} catch (IOException e) { /* prolly broken pipe, just die */ }
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public Process2 connectStdin(Readable source) {
		connect(source, wrap(getStdin()));
		return this;
	}
		
	public Process2 connectStdin(InputStream source) {
		return connectStdin(wrap(source));
	}
	
	public Process2 connectStdout(Appendable sink) {
		connect(wrap(getStdout()), sink);
		return this;
	}
	
	public Process2 connectStderr(Appendable sink) {
		connect(wrap(getStderr()), sink);
		return this;
	}

	public Process2 discardStdout() {
		return connectStdout(DEV_NULL);
	}
	
	public Process2 discardStderr() {
		return connectStderr(DEV_NULL);
	}
	
	public Process2 forwardIO() {
		connectStdin(System.in);
		return forwardOutput();
	}
	
	public Process2 forwardOutput() {
		connectStdout(System.out);
		connectStderr(System.err);
		return this;
	}
	
}
