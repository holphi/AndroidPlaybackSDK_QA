from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By
from random import uniform, randint

X = 240
Y = 1400

#Connect to current device, return a MonkeyDevice obj
device = MonkeyRunner.waitForConnection()

if device:
    print 'Connect device successfullly!'
else:
    print 'Connect device failed!'

#easy_device = EasyMonkeyDevice(device)

def launchApp():
    #Launch the activity
    device.startActivity(component = "com.dolby.application.infra.app/.AndroidPlayer")
    #Wait for the UI is completed loaded.
    MonkeyRunner.sleep(uniform(2, 4))

def quitApp():
    #Quit the player
    device.press('KEYCODE_BACK', 'DOWN_AND_UP')
    device.press('KEYCODE_BACK', 'DOWN_AND_UP')
    #Sleep 1 second forcibly to confirm related resources are fully released 
    MonkeyRunner.sleep(1)

def playContent():
    #easy_device.touch(By.id("id/player_play"), 'DOWN_AND_UP')
    device.touch(X, Y, 'DOWN_AND_UP')
    MonkeyRunner.sleep(uniform(1, 2))

while True:
    #Launch the player
    launchApp()
    #Play the default content
    playContent()
    #Quit the player by touching back button
    quitApp()