package it.unina.android.ripper_service;

import it.unina.android.ripper_service.IAnrdoidRipperServiceCallback;
import java.util.Map;

interface IAndroidRipperService {
	void send(in Map message);
	void register(IAnrdoidRipperServiceCallback cb);
	void unregister(IAnrdoidRipperServiceCallback cb);
	String getForegroundProcess();
}