package it.unina.android.ripper_service;

import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;

/**
 * AndroidRipperService Stub
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AndroidRipperServiceStub extends IAndroidRipperService.Stub {

	AndroidRipperService service = null;
	ActivityManager mActivityManager;

	public AndroidRipperServiceStub(AndroidRipperService service, ActivityManager activityManager)
	{
		this.service = service;
		this.mActivityManager = activityManager; 
	}

	@Override
	public void send(Map message) throws RemoteException {
		this.service.send((Map<String,String>)message);
	}

	@Override
	public void register(IAnrdoidRipperServiceCallback cb)
			throws RemoteException {
		this.service.register(cb);
		
	}

	@Override
	public void unregister(IAnrdoidRipperServiceCallback cb)
			throws RemoteException {
		this.service.unregister(cb);
	}

	@Override
	public String getForegroundProcess() throws RemoteException
	{
         List< ActivityManager.RunningTaskInfo > taskInfo = this.mActivityManager.getRunningTasks(1);
    	 ComponentName componentInfo = taskInfo.get(0).topActivity;
    	 return componentInfo.getPackageName();
	}

}
