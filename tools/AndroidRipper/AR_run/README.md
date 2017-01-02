## AndroidRipper 运行脚本

#### 共分为四步

1️⃣ `ARinstall.sh`

- 正确位置：AndroidTGTools/tools/AndroidRipper/Release/
- 作用：安装 AUT & AndroidRipper测试环境。

2️⃣ `ARdrive.sh`

- 正确位置：AndroidTGTools/tools/AndroidRipper/Release/
- 作用：调用 SystematicDriver 对AUT进行 DFS 遍历测试。测试期间也会产生 Emma 覆盖元数据。

3️⃣ `collectInfo.sh`

- 正确位置：AndroidTGTools/tools/AndroidRipper/collectInfo/
- 作用：收集由第2️⃣步测试所产生的覆盖元数据 (em, ec 文件)

4️⃣ `genEmma.sh`

- 正确位置：AndroidTGTools/tools/EvalResult/
- 作用：只是提供一个利用覆盖元数据生成HTML报告的命令，此脚本并不可直接运行。
