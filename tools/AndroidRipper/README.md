## AndroidRipper

**AndroidRipper** is a toolset for the automatic GUI testing of mobile Android Applications.

It is developed and maintained by the **REvERSE** (REsEarch laboRatory of Software Engineering) Group of the University of Naples "Federico II".

Welcome in the AndroidRipper [Wiki](https://github.com/reverse-unina/AndroidRipper/wiki) to have more info about **AndroidRipper** and the **REvERSE Group**

## 
### [First AndroidRipper Execution](https://github.com/reverse-unina/AndroidRipper/wiki/First-AndroidRipper-Execution)

> 工具目录：/Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release

Try the Ripper for the first time by following step:

1. Prerequisites (环境安装 与 环境变量配置)
	- Android SDK, AVD, ANT, etc.
	
2. Android Ripper Installer 配置
	- 路径：/Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/tools/AndroidRipper/Release/AndroidRipperInstaller
	- Edit "ripper.properties"
	- Start Android Ripper Installer  
	❌ `java -jar AndroidRipperInstaller.jar`// For Windows  
	✅ `java -jar ARInstaller.jar` // For MacOS, 修改 AndroidRipperInstaller 源码并重新编译成 ARInstaller.jar，使其成为适配 MAC-OSX 系统的可执行 JAR 包。
	
3. Android Ripper Driver: **Systematic Exploration**
4. Android Ripper Driver: **Random Exploration**