import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class ddms {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().ddms(args).forwardOutput().waitForSuccess();
	}
}
