import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class traceview {
	public static void main(String[] args) throws InterruptedException, IOException {
		AndroidTools.get().traceview(args).forwardOutput().waitForSuccess();
	}
}
