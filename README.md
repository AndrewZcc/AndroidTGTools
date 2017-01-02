## 本文 实验对比工具 情况

> 位置：/Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools  
> 论文官网: http://bear.cc.gatech.edu/~shauvik/androtest/  
> GitHub: https://github.com/AndrewZcc/AndroidTGTools.git

### 1. GUIRipper 

Smali 类型源码：tools/guiripper/tools/smali

#### 1.1 How to run?  

```  
vagrant@run1:~$ cd scripts
❌ vagrant@run1:~/scripts$ bash -x run_monkey.sh
✅ vagrant@run1:~/scripts$ ./run_monkey.sh
```
Results are saved in `/vagrant/results` directory in the VM, which is `~/vagrant/androtest/results` directory on the host machine.

· 如果发现在运行过程中 `adb devices` 失去反应，尝试使用如下命令进行恢复：

```
zhchuch$ adb kill-server
zhchuch$ adb start-server
zhchuch$ adb devices
```
#### 1.2 GUIRipper 整体运行与输出结果
- GUIRipper测试的整体流程

```
for (every AUT) do:
	start emulator;
	run.sh prepare [random/systematic] AUT > $RESULTDIR$p/tool_prepare.log;
	run.sh ripper [random/systematc] AUT > $RESULTDIR$p/tool.log;
	copy test-report;
	kill emulator;
endfor	
```

- run.sh prepare AUT 的流程

```
start emulator;
install APK & ripper-tool;
kill emulator;
```

- run.sh ripper AUT 的流程

```
while (true):  // 不停的测试，直到用户设定的时间到了为止
	start emulator;
	每次循环就是一次测试，测试结果输出到 >> $EXPPATH/test.txt
	unclock emulator device; // adb shell input keyevent 82
	running instrument-testing;
	// adb shell am instrument -w -e coverage false -e class $TESTPACKAGE.$TESTCLASS
	copy coverage-report for each test;
	kill emulator;
```

#### 1.3 How to get HTML Report (emma)

根据 `em & ec` 文件生成 html 报告  
`
java -cp /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/emma.jar emma
`  
`
report -r html -in coverage.em,coverage.ec 
-Dreport.html.out.file=./report/coverage.html
`

合并多个 ec 文件  
`
java -cp /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/emma.jar emma
`  
`
merge –in coverage1.ec,coverage2.ec,coverage3.ec –out coverage.ec
`

命令前缀总结  
`
java -cp /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/emma.jar emma *
`

### 2. SwiftHand