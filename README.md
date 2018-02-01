# Introduction
This auto test solution is designed and implemented for Android Playback SDK, which is an internal product. The test project is built on Android Instrumentation, by default, the InstrumentationRunner is not able to generate JUnit format test result, it just marks test pass/failure on Console directly, to solve this problem, I wrote an XmlInstrumentationRunner to replace the default instrumentation runner, the XmlInstumentationRunner is able to write test result to the SD card of Android device directly. In order to upload the execution results to TMS, I also wrote some customized annotations to associate test methods in QA code with test cases in TMS. There are over 350+ automation cases implemented in the test project.
 
# Continuous Integration Test
This project is a cross-GEO project, DEV and QA are separated in different locations, in early phase, team didn't set up CI, it affected us to evaluate and measure product quality. I wrote CI test script with Python to enable each team members can easily execute integration test from their workstation, the script itself provides parameters to let team members set build targets, test properties etc., when team members execute the scripts, it will pull the latest product code and QA code from repository, build the product APK and test APK, once the test completes, the tool will upload test result to TMS automatically and send out an auto test report with detailed metrics by e-mail to all team members. An illustration of the workflow can be simply described in below diagram:

![image](https://github.com/holphi/AndroidPlaybackSDK_QA/blob/master/resources/CI.jpg)

Team members can receive a quality notification by e-mail as below:

![image](https://github.com/holphi/AndroidPlaybackSDK_QA/blob/master/resources/CI_Report.jpg)
 
# Test Cases Management Plug-in
The project was using Testlink as the TMS, however, we felt difficult to maintain test cases and requirements in the Testlink, especially when we need to bulk update test cases/requirements, the test management plug-in I designed in spare time is to solve these pain points, this plug-in was implemented by Excel VBA, it's integrated into Excel as Macro, it can be loaded automatically when users launch Excel. By using this plug-in, team members can add/update test cases in Excel worksheet independently, they can also associate test cases with requirements, once they finish editing, they can export the test cases or requirements to XML files, then manually upload the XML files to Testlink. Below screenshot shows how this plug-in looks like:

![image](https://github.com/holphi/AndroidPlaybackSDK_QA/blob/master/resources/IF_PlugIn.jpg)