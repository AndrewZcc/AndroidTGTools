import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class dmtracedump {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().dmtracedump(args).forwardOutput().waitForSuccess();
	}
}
