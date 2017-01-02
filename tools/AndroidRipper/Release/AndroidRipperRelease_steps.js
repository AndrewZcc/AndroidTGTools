
First AndroidRipper Execution
https://github.com/reverse-unina/AndroidRipper/wiki/First-AndroidRipper-Execution

创建 AVD

android create avd -n RipperDevice -t android-17 -c 1024M -b armeabi-v7a

帮助信息
android --help create avd

android create avd -n gui-ripper -t android-17 -c 1024M -b armeabi-v7a --snapshot

---------------------------------------------------------------------------------------------------------------------------------

Step 1 - Android Ripper Installer

pwd: /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller

命令：java -jar ARInstaller.jar
注意：修改 ripper.properties 配置文件

	zhchuch-MacBook:AndroidRipperInstaller zhchuch$ java -jar ARInstaller.jar 
	Android Ripper Installer
	Using default configuration file 'ripper.properties'!
	Starting installation...
	[1479713432160] Booting Emulator!
	[1479713432165] Waiting for Emulator...)
	Emulator Online!
	Emulator Booted!
	[1479713580492] Emulator online!
	[1479713580492] adb devices...
	[1479713580520] Editing 'Configuration.java'
	[1479713580537] Editing 'AndroidManifest.xml'
	[1479713580538] update project...
		android update project --path /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/subjects/tippy_1.1.3 --subprojects --target android-17
		android update project -p /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/subjects/tippy_1.1.3 -s -t android-17

	[1479713581219] It seems that there are sub-projects. If you want to update them
	[1479713581219] please use the --subprojects parameter.
	[1479713581238] update test project...
		android update test-project -p /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller/AndroidRipper --main /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/subjects/tippy_1.1.3
		android update test-project -p /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller/AndroidRipper -m /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/subjects/tippy_1.1.3

	[1479713581674] Resolved location of main project to: /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/subjects/tippy_1.1.3
	[1479713581840] compiling...
	[1479713582578] install ripper-serivice apk...
		adb install /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller/AndroidRipperService.apk

	[1479713584398] 	pkg: /data/local/tmp/AndroidRipperService.apk
	[1479713590450] Success
	[1479713590759] adb shell mkdir...
	[1479713590819] mkdir failed for /data/data/net.mandaria.tippytipper/files, No such file or directory)
		su adb shell mkdir /data/data/net.mandaria.tippytipper/files

	[1479713590826] adb chmod 777...
	[1479713590886] Unable to chmod /data/data/net.mandaria.tippytipper/files: No such file or directory
	[1479713590899] adb shell rm...
	[1479713590962] rm failed for /data/data/net.mandaria.tippytipper/files/*, No such file or directory)
	[1479713590968] Installation finished!
	[1479713590968] Unlocking Emulator...

---------------------------------------------------------------------------------------------------------------------------------

AndroidRipper Driver Configuration Info
https://github.com/reverse-unina/AndroidRipper/wiki/AndroidRipperDriver-Configuration
---------------------------------------------------------------------------------------------------------------------------------

Step 2a - Android Ripper Driver: Systematic Exploration

pwd: /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperDriver

命令：java -jar AndroidRipper.jar s systematic.properties
	 java -jar ARDriver.jar s systematic.properties

注意：1. 修改 systematic.properties 配置文件
	 2. 每次回放都需要人为地帮忙 解锁手机 && 重启adb-server
	 		zhchuch-MacBook:~ zhchuch$ adb kill-server
			zhchuch-MacBook:~ zhchuch$ adb start-server
运行结果
	【 Generated code coverage data to /data/data/net.mandaria.tippytipper/files/coverage.ec 】

	Android ADB 命令：
		// Start AndroidRipperService on main emulator
		/AndroidRipperNewDriver/src/it/unina/android/ripper/autoandroidlib/Actions.java 
		L154
		- adb shell am startservice -a it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE

	# Exception -1 #
		
		adb shell am instrument -w -e coverage true -e class it.unina.android.ripper.RipperTestCase it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner

		INSTRUMENTATION_STATUS: id=ActivityManagerService
		INSTRUMENTATION_STATUS: Error=Unable to find instrumentation info for: ComponentInfo{it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner}
		INSTRUMENTATION_STATUS_CODE: -1
		android.util.AndroidException: INSTRUMENTATION_FAILED: it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner
			at com.android.commands.am.Am.runInstrument(Am.java:676)
			at com.android.commands.am.Am.run(Am.java:119)
			at com.android.commands.am.Am.main(Am.java:82)
			at com.android.internal.os.RuntimeInit.nativeFinishInit(Native Method)
			at com.android.internal.os.RuntimeInit.main(RuntimeInit.java:235)
			at dalvik.system.NativeStart.main(Native Method)

		Method:
			You need to check which instrumentation packages have been installed on your device:
			 `adb shell pm list instrumentation`
			Then verify whether `it.unina.android.ripper` is actually listed there.

		HOW-to-deal-with-it:
			Ans: Eclipse -> AndroidProject(AndroidRipper) -> Run As Android-Application -> Installing AndroidRipper.apk Success!
			You can use it (adb shell pm/am command).

	# Exception -2 #
		
		adb shell am instrument -w -e coverage true -e class it.unina.android.ripper.RipperTestCase it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner

		INSTRUMENTATION_STATUS: id=ActivityManagerService
		INSTRUMENTATION_STATUS: Error=Unable to find instrumentation target package: { kdk.android.simplydo }
		INSTRUMENTATION_STATUS_CODE: -1
		android.util.AndroidException: INSTRUMENTATION_FAILED: it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner
			at com.android.commands.am.Am.runInstrument(Am.java:676)
			at com.android.commands.am.Am.run(Am.java:119)
			at com.android.commands.am.Am.main(Am.java:82)
			at com.android.internal.os.RuntimeInit.nativeFinishInit(Native Method)
			at com.android.internal.os.RuntimeInit.main(RuntimeInit.java:235)
			at dalvik.system.NativeStart.main(Native Method)

		Method:
			Modify instrumentation target package { kdk.android.simplydo } -> { AUT package name }

	# 11.22 Exception -3: `ant -f ./AndroidRipper/build.xml debug`#
		
		[aapt] invalid resource directory name: /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller/AndroidRipper/bin/res/crunch
		解释：Ant and the ADT Plugin for Eclipse are packing the .apk file in a different build chain and temp generation folders. Crunch is created by the ADT.) 

		Method: (ref: http://stackoverflow.com/questions/19746319/how-to-solve-invalid-resource-directory-name-resource-crunch)
			1. use ant clean if you used the ADT from eclipse before. | Use Projects -> clean ... in Eclipse if you used ant before. 
			
			2. I had the same issue: invalid resource directory name: D:\work\merge\Client_2_24\Client\bin\res/crunch. I tried Project->Clean but didn't work. Then I directly deleted the directory crunch and it worked :)
			[Success]

$$$$$$$$
Success: instrumentation:it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner (target=net.mandaria.tippytipper)
$$$$$$$$
Success: Success run the Systematic Exploration Strategy! Congratulations to myself!
$$$$$$$$

---------------------------------------------------------------------------------------------------------------------------------

Step 2b - Android Ripper Driver: Random Exploration

pwd: /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperDriver

命令：java -jar AndroidRipper.jar r random.properties
	 java -jar ARDriver.jar r random.properties
注意：修改 random.properties 配置文件


---------------------------------------------------------------------------------------------------------------------------------

Step 2c - Android Ripper Driver: Hybrid Manual-Systematic Exploration

命令：java -jar AndroidRipper.jar tc manualSystem.properties
	 java -jar ARDriver.jar tc manualSystem.properties
注意：添加 manualSystem.properties 配置文件


