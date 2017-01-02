import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.autoandroid.lib.AndroidTools;

public class dx {
	public static void main(String [] args) throws InterruptedException, IOException {
		List<String> parameters = new ArrayList<String>();
		parameters.addAll(asList(args));

		String additionalArguments = System.getProperty("additional-arguments");
		if (additionalArguments != null) {
			parameters.addAll(asList(additionalArguments.split(File.pathSeparator)));
		}
		
		AndroidTools.get().dx(parameters).forwardOutput().waitForSuccess();
	}
}
