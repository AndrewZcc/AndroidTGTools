import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class adb {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().adb(args).forwardOutput().waitForSuccess();
	}
}
