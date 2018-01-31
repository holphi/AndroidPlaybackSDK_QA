# Introduction
This auto test framework is designed and implemented for Android Playback SDK. Android Playback SDK provides an Java API for 3rd-party users. The test project is built on Android Instrumentation, by default, the InstrumentationRunner is not able to generate jUnit format test result, it marks test pass/failure on Console directly, to sovle this problem, I wrote an XmlInstrumentationRunner to replace the default instrumentation runner. To upload the execution results to TMS, I wrote a customized annotations to associate test methods with test cases in TMS. There are over 350+ automation cases implemented in the test project. 

# Continuous Integration Test
This project is a cross-GEO project, DEV and QA are seperated in different locations, in early phase, team didn't set up CI, it affected us to evaluate and measure prodcut quality. I wrote a tool with Python to enable each team members execute integration test from their workstation, the tool itself provides parameters to let team members can set build targets, test properties etc., when team members launch the tool, it will pull the latest product code and QA code from repository, build the product APK and test APK, once the test completes, the tool will upload test reeulst to TMS automatically and send out an auto test report with detailed metrics by e-mail to all team members. An illustration of the workflow cna be simply described in below diagram:


# Class Diagrams
