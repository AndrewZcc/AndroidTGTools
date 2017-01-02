
time = 2016年11月18日 14:10

echo "running instrument"

- MAC OS / Ubuntu

$PLATFORMPATH/adb shell am instrument [-w] [-e coverage false] [-e class $TESTPACKAGE.$TESTCLASS] \
                                  $TESTPACKAGE/android.test.InstrumentationTestRunner >> $EXPPATH/test.txt

- Windows

adb shell am instrument [-w] [-e coverage true] [-e class %TESTPACKAGE%.%TESTCLASS%] [%TESTPACKAGE%/android.test.InstrumentationTestRunner] >> %EXPPATH%\test.txt

------------------------

coverage = false
class = $TESTPACKAGE.$TESTCLASS = it.unina.androidripper.guitree.GuiTreeEngine

------------------------

instrument [options] component	Start monitoring with an Instrumentation instance. 
Typically the target component is the form test_package/runner_class.

Options are:

-r: Print raw results (otherwise decode report_key_streamresult). Use with [-e perf true] to generate raw output for performance measurements.
-e name value: Set argument name to value. For test runners a common form is -e testrunner_flag value[,value...].
-p file: Write profiling data to file.
-w: Wait for instrumentation to finish before returning. Required for test runners.
--no-window-animation: Turn off window animations while running.
--user user_id | current: Specify which user instrumentation runs in; current user if not specified.

