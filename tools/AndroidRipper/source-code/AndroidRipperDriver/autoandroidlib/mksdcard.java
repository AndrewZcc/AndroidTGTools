import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class mksdcard {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().mksdcard(args).forwardOutput().waitForSuccess();
	}
}
