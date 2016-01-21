#Import the monkey runner modules
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from random import uniform

x = 240
y = 1400

#Connect to current device, returning a MonkeyDevice obj
device = MonkeyRunner.waitForConnection()
#Launch the component
#device.startActivity(component = "com.dolby.dope/.AndroidPlayer")

#Wait for the UI is completed loaded.
MonkeyRunner.sleep(10)

#Press play button to start playing default content
device.touch(x,y,MonkeyDevice.DOWN_AND_UP)

#Wait for the content has been played.
MonkeyRunner.sleep(3)

#Do pause -> play in sequence.
while True:
    print "Press the second button"
    device.touch(x, y, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(uniform(1,3))