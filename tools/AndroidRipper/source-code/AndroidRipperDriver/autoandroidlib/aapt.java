import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class aapt {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().aapt(args).forwardOutput().waitForSuccess();
	}
}
