import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class aidl {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().aidl(args).forwardOutput().waitForSuccess();
	}
}
