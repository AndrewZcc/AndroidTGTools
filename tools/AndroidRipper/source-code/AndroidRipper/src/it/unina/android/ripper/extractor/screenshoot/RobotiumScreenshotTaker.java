package it.unina.android.ripper.extractor.screenshoot;

import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.log.Debug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

/**
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RobotiumScreenshotTaker implements IScreenshotTaker
{
	/**
	 * Image captured counter
	 */
	private static int COUNT_CAPTURED_IMAGES = 0;
	
	/**
	 * Instrumentation
	 */
	Instrumentation instrumentation;
	
	/**
	 * Context
	 */
	Context context;
	
	/**
	 * Robot Instance
	 */
	IRobot robot = null;
	
	/**
	 * Constructor 
	 * 
	 * @param robot Robot Instance
	 */
	public RobotiumScreenshotTaker(IRobot robot)
	{
		this.robot = robot;
		this.instrumentation = robot.getInstrumentation();
		this.context = instrumentation.getContext();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.extractor.screenshoot.IScreenshotTaker#takeScreenshot(android.app.Activity)
	 */
	@SuppressLint("WorldWriteableFiles")
	public void takeScreenshot(Activity activity)
	{
		String filename = "screenshot_" + Integer.toString((COUNT_CAPTURED_IMAGES++)) + ".jpg";

		FileOutputStream fileOutput = null;
		try {
			fileOutput = this.context.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);

			ArrayList<View> views = robot.getViews();
			if (views != null && views.size() > 0)
			{
				final View view = views.get(0);
				final boolean flag = view.isDrawingCacheEnabled();
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if (!flag) {
							view.setDrawingCacheEnabled(true);
						}
			            view.buildDrawingCache();
					}
				});
				this.instrumentation.waitForIdleSync();
				Bitmap b = view.getDrawingCache();
	            b = b.copy(b.getConfig(), false);
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if (!flag) {
							view.setDrawingCacheEnabled(false);
						}
					}
				});

				if (fileOutput != null) {
					b.compress(Bitmap.CompressFormat.JPEG, 90, fileOutput);
					Debug.info(this, "Saved image on disk: " + filename);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileOutput != null) {
				try { fileOutput.close(); } catch(Exception ex) {}
			}
		}
	}
}
