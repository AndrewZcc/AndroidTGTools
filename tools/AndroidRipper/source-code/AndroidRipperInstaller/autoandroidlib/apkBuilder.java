import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class apkBuilder {
	public static void main(String [] args) throws InterruptedException, IOException {
		AndroidTools.get().apkBuilder(args).forwardOutput().waitForSuccess();
	}
}
