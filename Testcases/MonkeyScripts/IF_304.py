from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By
from random import uniform, randint

Y = 210
i = 1 

#Connect to current device, return a MonkeyDevice obj
device = MonkeyRunner.waitForConnection()

if device:
    print 'Connect device successfullly!'
else:
    print 'Connect device failed!'

while True:
    print 'Seek %d' % i
    device.touch(int(uniform(40, 2445)), Y, 'DOWN_AND_UP')
    timeout = int(uniform(2,5))
    print 'Sleep for %d seconds' % timeout
    MonkeyRunner.sleep(timeout)
    i= i + 1