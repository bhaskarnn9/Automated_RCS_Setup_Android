echo "Android Messages Rich Communications set Up script"
echo "Copying main application to device internal storage"
adb push app-debug.apk /data/tmp/com.bhaskarn.automated_rcs_setup
echo "Copying test application app to device internal storage"
adb push app-debug-androidTest.apk /data/tmp/com.bhaskarn.automated_rcs_setup.test
echo "Installing main application"
adb install -r -t /data/tmp/com.bhaskarn.automated_rcs_setup
echo "Installing test application"
adb install -r /data/tmp/com.bhaskarn.automated_rcs_setup.test
echo "Running instrumenation tests"
adb shell am instrument -w com.bhaskarn.automated_rcs_setup.test/androidx.test.runner.AndroidJUnitRunner
