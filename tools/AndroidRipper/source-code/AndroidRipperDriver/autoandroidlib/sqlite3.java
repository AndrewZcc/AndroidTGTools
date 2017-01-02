import java.io.IOException;

import com.googlecode.autoandroid.lib.AndroidTools;

public class sqlite3 {
	public static void main(String[] args) throws InterruptedException, IOException {
		AndroidTools.get().sqlite3(args).forwardOutput().waitForSuccess();
	}
}
