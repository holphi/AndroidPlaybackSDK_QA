#Import the monkey runner modules
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from random import uniform

#Button PLAY
x1 = 240
y1 = 1400

#Button STOP
x2 = 600
y2 = 1400

#Connect to current device, returning a MonkeyDevice obj
device = MonkeyRunner.waitForConnection()
#Launch the component
#device.startActivity(component = "com.dolby.dope/.AndroidPlayer")

#Wait for the UI is completed loaded.
MonkeyRunner.sleep(10)

i=0

#Iterate PLAY -> STOP operations in sequence.
while True:
    i=i+1
    print "Press button PLAY: %d " % i
    device.touch(x1, y1, MonkeyDevice.DOWN_AND_UP)
    #Wait for the content has been played.
    MonkeyRunner.sleep(1)
    print "Press button STOP: %d " % i
    device.touch(x2, y2, MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(1)