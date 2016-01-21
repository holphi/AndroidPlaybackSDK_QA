''' Entry script for Automation test.

COPYRIGHT: (C) 2014 Dolby Laboratories,. All rights reserved.
Author: Alex LI

History:
2014/02/25 Alex LI   Initial version for test prototype
2014/03/11 Alex LI   Add optional parameter for uploading auto test result
2014/03/13 Alex LI	 Import class TestReportParser for generating html format report
2014/04/09 Alex LI   Start rest server before executing automation cases, REST server will be used for receiving bandwidth throttling request
2014/04/10 Alex LI   Stop rest server after completing auto execution
2014/04/11 Alex LI   New functionality added, to build native project automatically
2014/05/06 Alex LI   Fix a failure issue when build native project.
2014/05/07 Alex LI   Fix an issue that the failure chart can't be generated.
2014/05/27 Alex LI   Auto copy Player.java from the dev workspace.
2014/06/09 Alex LI   Add revision number to regression test report.
2014/06/12 Alex LI   Copy Android test log from the external storage of DUT to local folder of PC.
2014/06/13 Alex LI   Fix a bad format issue of attached zip file
2014/08/06 Alex LI   Send test report first, then upload test result to TL, cause gti_scutil is VERY slow.
2014/08/07 Alex LI   Reduce the size of attachments, attach test log of failure cases only
2014/08/22 Alex LI   Switch to CI branch, use workflow.py -w android to generate lipdope.so
2015/03/16 Alex LI   Maintain runAutoTest.py as dev team change the structure of ci branch
2015/08/12 Alex LI   Copy libdpfengine.so to lib folder of Android test project because of new engine plug-in system(DOPE-758)
'''

help_content='''
Usage: runAutoTest.py [Mandatory parameters] [Optional parameters]

Mandatory parameters:
-t/--target\t\t: Followed by the build target id, to see a list of available targets and their corresponding IDs, execute: android list targets.

Optional parameters:
-p/--priority\t\t: Specify test priority, the value can be set to high, medium or low, by default, all auto cases will be executed.
-r/--revision\t\t: Specify the revision number of dev branch, otherwise, it will sync the latest code by default.
--offline\t\t: Execute auto test through offline mode, produce code will be synchronized automatically by script.
--uploadresult\t\t: Upload test result immediately once completing test execution.
--sendmail\t\t: Send out Auto test report by E-mail.

'''

import sys
import os,stat
import re

from os.path import join, abspath, exists, isfile
from xml.dom import minidom
from xml import dom

sys.path.append('./TestTools/src/QAInfrastructure')

from qainfra import *

def runCommand(cmd):
    import subprocess
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    stdoutput, erroutput = p.communicate()
    return stdoutput, erroutput

#Get config data
def getConfigData():
    results = {}
    from xml.etree import ElementTree as ET
    config = ET.parse(abspath(join('.', 'Config', 'AutoConfig.xml')))
    entries = config.findall('./entry')
    for entry in entries:
        results[entry.attrib['key']]=entry.text
    return results

CONF = getConfigData()

def buildRefProject(target):
    buildfile = abspath(join('.', 'AndroidPlayer', 'build.xml'))

    #Delete last build file
    getLogger().info('Check and delete last build.xml in reference project.')
    if exists(buildfile):
        getLogger().info('Delete %s' % buildfile)
        os.remove(buildfile)
        
    #Update reference project
    cmd = 'call android update project -n AndroidPlayer -t %s -p ./AndroidPlayer' % target
    getLogger().info('Update reference project: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command:\n %s' % stdout)
    if not exists(buildfile):
        if stderr is not None:
            getLogger().error('Error from command:\n %s' % stderr)
        #Raise exception
    else:
        getLogger().info('Update reference project successfully.')

    #Build reference project
    cmd = 'call ant debug -buildfile ./AndroidPlayer/build.xml'
    getLogger().info('Build reference project: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command:\n %s' % stdout)
    if re.search('BUILD SUCCESSFUL', stdout) is None:
        if stderr is not None:
            getLogger().error('Error from command:\n %s' % stderr)
        #Raise exception
        raise Exception('Build reference project failure.')
    else:
        getLogger().info('Build reference project successfully.')

def buildTestProject():
    buildfile = abspath(join('.', 'AndroidPlayerTests', 'build.xml'))

    #Delete last build file
    getLogger().info('Check and delete last build.xml in test project.')
    if exists(buildfile):
        getLogger().info('Delete %s' % buildfile)
        os.remove(buildfile)

    #Update test project
    cmd = 'call android update test-project -m ../AndroidPlayer -p ./AndroidPlayerTests'
    getLogger().info('Update test project: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Results from command:\n %s' % stdout)
    if not exists(buildfile):
        if stderr is not None:
            getLogger().error('Error from command:\n %s' % stderr)
        #Raise exception
    else:
        getLogger().info('Update test project successfully.')

    #Build test project
    cmd = 'call ant instrument -buildfile ./AndroidPlayerTests/build.xml'
    getLogger().info('Build test project: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command:\n %s' % stdout)
    if re.search('BUILD SUCCESSFUL', stdout) is None:
        if stderr is not None:
            getLogger().error('Error from command:\n %s' % stderr)
        #Raise exception
        raise Exception('Build test project failure.')
    else:
        getLogger().info('Build test project successfully.')

def buildProjects(target):
    buildRefProject(target)
    buildTestProject()

def uninstallTestBuilds():
    #Uninstall ref apk firstly.
    cmd = 'adb uninstall com.dolby.application'
    getLogger().info('Uninstall reference apk: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command: %s' % stdout)
    #Uninstall test apk.
    cmd = 'adb uninstall com.dolby.dope.test'
    getLogger().info('Uninstall test apk: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command: %s' % stdout)

#Install apks
def installTestBuilds():
    #Unistall original test builds from connected device.
    uninstallTestBuilds()
    #Install ref apk firstly.
    cmd = 'adb install ./AndroidPlayer/bin/AndroidPlayer-debug.apk'
    getLogger().info('Install reference apk: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command:%s' % stdout)
    #Install test apk.
    cmd = 'adb install ./AndroidPlayerTests/bin/AndroidPlayerTest-instrumented.apk'
    getLogger().info('Install test apk: %s' % cmd)
    stdout, stderr = runCommand(cmd)
    getLogger().info('Result from command:%s' % stdout)

#Build native project, copy artifacts to test projects(libdope.so)
def buildNativeProject():
    global CONF
    import shutil
    try:
        android_build_branch = join(CONF['dev_workspace_path'], 'IntegrationFramework', 'make', 'ndk');
        android_build_script = join(android_build_branch, 'jni')
        armeabi_lib_path = join(android_build_branch, 'libs', 'armeabi', 'libdope.so')
        armeabi_v7a_lib_path = join(android_build_branch, 'libs', 'armeabi-v7a', 'libdope.so')
        if exists(armeabi_lib_path):
            getLogger().info('Remove native lib: %s' % armeabi_lib_path)
            os.remove(armeabi_lib_path)
        if exists(armeabi_v7a_lib_path):
            getLogger().info('Remove native lib: %s' % armeabi_v7a_lib_path)
            os.remove(armeabi_v7a_lib_path)
        armeabi_dpflib_path = join(android_build_branch, 'libs', 'armeabi', 'libdpfengine.so')
        armeabi_v7a_dpflib_path = join(android_build_branch, 'libs', 'armeabi-v7a', 'libdpfengine.so')
        if exists(armeabi_dpflib_path):
            getLogger().info('Remove native lib: %s' % armeabi_dpflib_path)
            os.remove(armeabi_dpflib_path)
        if exists(armeabi_v7a_dpflib_path):
            getLogger().info('Remove native lib: %s' % armeabi_v7a_dpflib_path)
            os.remove(armeabi_v7a_dpflib_path)
        #Invoke command 'ndk-build clean' to clean all generated binaries
        build_cmd = 'ndk-build clean -C %s' % android_build_script
        getLogger().info('Run command to clean all generated binaries: %s' % build_cmd)
        stdout, stderr = runCommand(build_cmd)
        getLogger().info('Command result:\n%s' % stdout)
        #Invoke command 'ndk-build' to generate binaries
        build_cmd = 'ndk-build -C %s' % android_build_script
        getLogger().info('Run command to build native project: %s' % build_cmd)
        stdout, stderr = runCommand(build_cmd)
        #getLogger().info('Command result:\n%s' % stdout)
        ''' Disable workflow.py for temporary, as the mbuild script doesn't support windows platform 
        cmd = join(CONF['dev_workspace_path'], 'workflow', 'scripts', 'workflow.lnk')
        getLogger().info('Call workflow.py - w android to generate libdope.so: %s' % cmd)
        runCommand(cmd)'''
        #Can't find build artifact libdope.so, then raise an exception direclty
        if not exists(armeabi_lib_path) and not exists(armeabi_v7a_lib_path):
            raise Exception('Build native project failed, please execute native build command manually and check your env!')
        #Can't find build artifact lipdpfengine.so, then raise an exception directly
        if not exists(armeabi_dpflib_path) and not exists(armeabi_v7a_dpflib_path):
            raise Exception('Build native project failed, please execute native build command manually and check your env!')
        #If the armeabi lib exists, then copy the latest native lib in armeabi folder to Android Java project
        if exists(armeabi_lib_path):
            #Copy libdope.so
            target = join('.', 'AndroidPlayer', 'libs', 'armeabi', 'libdope.so')
            if exists(target):
                os.remove(target)
            getLogger().info('Copy the native lib from %s to %s' % (armeabi_lib_path, target))
            shutil.copy2(armeabi_lib_path, target)
            #Copy libdpfengine.so
            target = join('.', 'AndroidPlayer', 'libs', 'armeabi', 'libdpfengine.so')
            if exists(target):
                os.remove(target)
            getLogger().info('Copy the native lib from %s to %s' % (armeabi_dpflib_path, target))
            shutil.copy2(armeabi_dpflib_path, target)
        #If the armeabi-v7a lib exists, then copy the latest native lib in armeabi-v7a folder to Android Java project
        if exists(armeabi_v7a_lib_path):
            #Copy lipdope.so
            target = join('.', 'AndroidPlayer', 'libs', 'armeabi-v7a', 'libdope.so')
            if exists(target):
                os.remove(target)
            getLogger().info('Copy the native lib from %s to %s' % (armeabi_v7a_lib_path, target))
            shutil.copy2(armeabi_v7a_lib_path, target)
            #Copy lipdpfengine.so
            target = join('.', 'AndroidPlayer', 'libs', 'armeabi-v7a', 'libdpfengine.so')
            if exists(target):
                os.remove(target)
            getLogger().info('Copy the native lib from %s to %s' % (armeabi_v7a_dpflib_path, target))
            shutil.copy2(armeabi_v7a_dpflib_path, target)
        #Copy Player.java to Test project
        player_src_path = join(CONF['dev_workspace_path'], 'IntegrationFramework', 'bindings', 'java', 'src', 'com', 'dolby', 'infra', 'Player.java')
        player_target_path = join('.', 'AndroidPlayer', 'src', 'com', 'dolby', 'infra', 'Player.java')
        if not exists(player_src_path):
            raise Exception("Cannot locate the path: %s" % player_src_path)
        #Remove original Player.java first
        if exists(player_target_path):
            getLogger().info('Remove original Player.java: %s' % player_target_path)
            os.chmod(player_target_path, stat.S_IWRITE)
            os.remove(player_target_path)
        getLogger().info('Copy Player.java from %s to %s' % (player_src_path, player_target_path))
        shutil.copy2(player_src_path, player_target_path)
    except Exception, e:
        raise e

#Create a temporary folder
def createFolder(folderName):
    getLogger().info('Create a temporary folder: %s' % folderName)
    import shutil
    try:
        folder = './%s' % folderName
        if exists(folder):
            shutil.rmtree(folder)
        os.mkdir(folder)
    except Exception, e:
        raise e

#Create a Junit report template
def createJUnitRptTemplate():
    try:
        #Create a new xml document
        impl = dom.getDOMImplementation()
        doc = impl.createDocument(None, 'testsuite', None)
        root = doc.documentElement
        #Append attributes to root element
        ts_attributes = {'name':'', 'skips':'0', 'failures':'0', 'errors':'0', 'tests':'0', 'time':'0.0'}
        for (k,v) in ts_attributes.items():
            attr = doc.createAttribute(k)
            attr.value = v
            root.setAttributeNode(attr)
        return doc
    except Exception, ex:
        raise ex

#Collect individual results and generate a standard JUnit report
def generateJUnitReport(tc_list):
    #Get test case element from single test result
    def getTCElement(filename):
        try:
            doc = minidom.parse(filename)
            root = doc.documentElement
            element = root.getElementsByTagName('testcase')[0]
            return element
        except Exception, e:
            raise e
    getLogger().info('Collect individual results and generate a standard JUnit report')
    #Remove last test report
    auto_report_file_path = abspath(join('.', 'AutoTestResults.xml'))
    if exists(auto_report_file_path):
        os.remove(auto_report_file_path)
    totalTC = len(tc_list)
    errorTC = 0
    failedTC = 0
    totalTime = 0.0
    try:
        doc = createJUnitRptTemplate()
        root = doc.documentElement
        for tc in tc_list:
            if tc['result']=='F':
                failedTC = failedTC + 1
            elif tc['result']=='E':
                errorTC = errorTC + 1
            tc_result_file = abspath(join('.','temp', '%s.%s.xml' %(tc['classname'], tc['methodname'])))
            element = getTCElement(tc_result_file)
            # If can't find property for priority, then create one.
            if not element.hasAttribute('priority'):
                    element.setAttribute('priority', tc['priority'])
            totalTime = totalTime + float(element.getAttribute('time'))
            root.appendChild(element)
        #Update related attributes in root element
        root.setAttribute('errors', str(errorTC))
        root.setAttribute('failures', str(failedTC))
        root.setAttribute('tests', str(len(tc_list)))
        root.setAttribute('time', str(totalTime))
        #Save report
        f = open(auto_report_file_path,'w')
        f.write(doc.toprettyxml(indent = "\t", newl = "\n", encoding = "utf-8"))
        f.close()
    except Exception, e:
        raise e

#Write crash xml result to temp
def writeCrashResult(tc_info, crash_info):
    try:
        #Get a Junit report template
        doc = createJUnitRptTemplate()
        root = doc.documentElement
        root.setAttribute('errors', '1')
        root.setAttribute('tests', '1')
        #Add test case element and append attributes to its element
        tc = doc.createElement('testcase')
        for (k, v) in tc_info.items():
            attr = doc.createAttribute(k)
            attr.value = v
            tc.setAttributeNode(attr)
        attr = doc.createAttribute('time')
        attr.value = '0.0'
        tc.setAttributeNode(attr)
        root.appendChild(tc)
        #Add failure element to test case element
        failure = doc.createElement('failure')
        failure_attributes = {'message':'crash_message','type':'crash'}
        for (k, v) in failure_attributes.items():
            attr = doc.createAttribute(k)
            attr.value = v
            failure.setAttributeNode(attr)
        #Create text node
        failure_content = doc.createTextNode(crash_info)
        failure.appendChild(failure_content)
        tc.appendChild(failure)
        #Append sys_out element
        sys_err = doc.createElement('system-err')
        tc.appendChild(sys_err)
        filename = join('.', 'temp', '%s.%s.xml' % (tc_info['classname'], tc_info['methodname']))
        f = open(filename, 'w')
        f.write(doc.toprettyxml(indent = "\t", newl = "\n", encoding = "utf-8"))
        f.close()
    except Exception, ex:
        raise ex

#Pass the list of available auto cases, and invoke auto test
def execTest(tc_list):
    #Copy single test result to local
    def copyAutoResult(fileName):
        target = './temp/%s' % fileName
        cmd = 'adb pull /mnt/sdcard/AutoTestResults.xml %s' % target
        runCommand(cmd)
    def copyTestLog(filename):
        cmd = 'adb pull /mnt/sdcard/%s ./AutoTestLog/%s' %(filename, filename)
        runCommand(cmd)
    def compressLogFolder():
        srcFolder = './AutoTestLog'
        destFile = './AutoTestLog.zip'
        if exists(srcFolder):
            if exists(destFile):
                os.remove(destFile)
            return compressFolder(srcFolder, destFile)
    #Create a tem folder for invididual test result
    createFolder('temp')
    #Create a temp folder for individual Android logcat log
    createFolder('AutoTestLog')
    getLogger().info('Start running auto cases in sequence') 
    try:
        for tc in tc_list:
            getLogger().info('Running test method %s in test class %s' % (tc['methodname'], tc['classname']))
            cmd = 'adb shell am instrument -w -e class %s#%s com.dolby.dope.test/com.dolby.test.runner.XmlInstrumentationTestRunner' %(tc['classname'], tc['methodname'])
            stdout, stderr = runCommand(cmd)
            test_result = 'P'
            if 'OK' in stdout:
                getLogger().info('PASSED')
                test_result = 'P'
                copyAutoResult('%s.%s.xml' %(tc['classname'],tc['methodname']))
            elif 'junit.framework.AssertionFailedError' in stdout:
                getLogger().info('FAILED\n%s' % stdout)
                test_result = 'F'
                copyAutoResult('%s.%s.xml' %(tc['classname'],tc['methodname']))
                copyTestLog('%s.log' % tc['methodname'])
            elif 'Process crashed' or 'Native crash' in stdout:
                getLogger().info('CRASHED\n%s' % stdout)
                test_result = 'E'
                writeCrashResult(tc, stdout)
                copyTestLog('%s.log' % tc['methodname'])
            else:
                getLogger().info('UNKNOWN\n%s' % stdout)
                test_result= 'UNKNOWN'
                raise Exception('Please add code to handle this situation')
            tc['result'] = test_result
        #Compress android logcat log to a zip file
        getLogger().info('Compress test log folder to AutoTestLog.zip')
        rst = compressLogFolder()
        getLogger().info('Result from command: %s' % rst) 
        return tc_list
    except Exception, e:
        raise e

def runTest(target, priority):
    #1. Build dev project for native libs and copy them to test projets;
    buildNativeProject()
    #2. Build test projects
    buildProjects(target)
    #3. Install test builds;
    installTestBuilds()
    #4. Retrieve available auto cases
    tc_list = getAvailableAutoCases(priority)
    if len(tc_list)==0:
        raise Exception('None test cases retrieved for test execution.')
    getLogger().info('%s Auto test cases retrieved.' % len(tc_list))
    #5. Start REST Server
    getLogger().info('Start REST Server to listen to http requests')
    startRestServer()
    #6. Pass the lsit of available auto cases, invoke auto test
    execTest(tc_list)
    #7. Terminate REST Server
    getLogger().info('Terminate REST server.')
    stopRestServer()
    #8. Collect individual test results, generate a JUnit test report
    generateJUnitReport(tc_list)
    
def uploadTestResult():
    getLogger().info('Upload test result to Testlink server.')
    try:
        #1. Connect with Testlink server
        cmd = 'gti_scutils login -s %s -d %s' % (CONF['testlink_url'], CONF['dev_key'])
        getLogger().info('Connect to Testlink server with command: %s' % cmd)
        stdout, stderr = runCommand(cmd)
        if re.search('Logged in', stdout) is None:
            raise Exception('Login failed, pls check your config file!')
        else:
            getLogger().info('Result from command: %s' % stdout)
        #2. Upload test result
        if 'platform' in CONF.keys():
            cmd = 'gti_scutils xunit -a upload -p "%s" -s "%s" --sc-platform-name %s -b "%s" -x AutoTestResults.xml' % (CONF['project'],CONF['plan'],CONF['platform'],CONF['build'])
        else:
            cmd = 'gti_scutils xunit -a upload -p "%s" -s "%s" -b "%s" -x AutoTestResults.xml' % (CONF['project'],CONF['plan'],CONF['build'])
        getLogger().info('Import test result to Testlink, this command take a few minutes, pls wait patiently.')
        getLogger().info(cmd)
        stdout, stderr = runCommand(cmd)
        getLogger().info('Result from command: %s' % stdout)
        #3. Disconnect with Testlink server
        cmd = 'gti_scutils logout'
        getLogger().info('Disconnect with Testlink server: %s' % cmd)
        stdout, stderr = runCommand(cmd)
        getLogger().info('Result from command: %s' % stdout)
    except Exception, e:
        raise e

def processCommand(target, priority, revision_num='latest', offline=False, uploadResult=False, sendMail=False):
    getLogger().info('Build target id: %s' % target)
    getLogger().info('Test priority: %s' % priority)
    getLogger().info('Offline mode: %r' % offline)
    getLogger().info('Revision number: %s' % revision_num)
    getLogger().info('Upload test result: %r' % uploadResult)
    getLogger().info('Send auto test e-mail: %r' % sendMail)
    if offline:
        #Sync with P4 to get specified product code;
        revision_num = syncDevWorkspace(revision_num)
        #Sync with P4 to get the latest test code;
        syncTestWorkspace()
    #Run Automation test
    runTest(target, priority)
    #Parse JUnit test report and output test result
    testRptParser = TestReportParser('AutoTestResults.xml')
    summaryInfo = testRptParser.summaryInfo
    #Output simple test results:
    getLogger().info('Tests:%d  Passes:%d  Failures:%d  Errors:%d' %(summaryInfo['tests'], summaryInfo['passes'], summaryInfo['failures'], summaryInfo['errors']))
    getLogger().info('ExecutionTime: %s' % summaryInfo['time'])
    getLogger().info('Passrate: %s%%' % summaryInfo['passrate'])
    #Send out test report
    if sendMail==True:
        sendTestReport(priority, target, revision_num, testRptParser)
    #Upload test result to TL
    if uploadResult==True:
        uploadTestResult()
    getLogger().info('Done. Thanks for using it!')

#Send test report to team members
def sendTestReport(test_priority, target, revision_num, testRptParser):
    #Get test environment data
    def getTestEnvData():
        results = {}
        from xml.etree import ElementTree as ET
        config = ET.parse(abspath(join('.', 'Config', 'TestEnv.xml')))
        entries = config.findall('./entry')
        for entry in entries:
            results[entry.attrib['key']]=entry.text
        return results

    global CONF
    import time
    str_date = time.strftime('%Y/%m/%d', time.localtime(time.time()))
    test_env = getTestEnvData()
    if test_priority == 'High':
        subject = '[Integration Framework] Android IF Smoke Test Report @%s (%s)' %(revision_num, str_date)
        rptTitle = 'Android IF Smoke test report @%s (%s)' %(revision_num, str_date)
    elif test_priority == 'All':
        subject = '[Integration Framework] Android IF Regression Test Report @%s (%s)' %(revision_num, str_date)
        rptTitle = 'Android Integration Framework Regression Test Report @%s (%s)' %(revision_num, str_date)
    else:
        subject = '[Integration Framework] Android IF Auto Test Report @%s (%s)' %(revision_num, str_date)
        rptTitle = 'Android Integration Framework Test Report @%s (%s)' %(revision_num, str_date)
    try:
        #Generate test report first
        getLogger().info('Generate Automation test report.')
        generateReport(rptTitle, test_env, testRptParser)
        #Send out test report
        getLogger().info('Send out Automation test report to team members.')
        sendAutoTestReport(CONF['smpt_server'], subject, CONF['sender'], CONF['receivers'])
    except Exception, e:
        raise e

#Get available auto case list by dry run command 
def getAvailableAutoCases(priority='All'):
    priority = priority.lower().capitalize()
    if priority == 'All':
        dryrun_cmd = 'adb shell am instrument -w -e log true com.dolby.dope.test/com.dolby.test.runner.XmlInstrumentationTestRunner'
    else:
        dryrun_cmd = 'adb shell am instrument -w -e annotation com.dolby.test.annotation.%s -e log true com.dolby.dope.test/com.dolby.test.runner.XmlInstrumentationTestRunner' % priority
    getLogger().info('Get available auto case list by dry-run command: %s' % dryrun_cmd)
    result = []
    try:
        #Dry run for avaiable test cases.
        stdout, stderr = runCommand(dryrun_cmd)
        getLogger().info('Result from command:\n %s' % stdout)
        target = './AutoCasesList.xml'
        cmd = 'adb pull /mnt/sdcard/AutoTestResults.xml %s' % target
        getLogger().info('Pull dry run result to local: %s' %cmd)
        stdout, stderr  = runCommand(cmd)
        getLogger().info('Result from command: %s' % stdout)
        #Get all available test method from dry run result
        dom = minidom.parse(target)
        root = dom.documentElement
        for tc in root.getElementsByTagName('testcase'):
            tc_info = {}
            tc_info['classname'] = tc.attributes['classname'].nodeValue
            tc_info['name'] = tc.attributes['name'].nodeValue
            tc_info['methodname'] = tc.attributes['methodname'].nodeValue
            tc_info['priority'] = tc.attributes['priority'].nodeValue
            result.append(tc_info)
        return result
    except Exception, e:
        raise e

def getLastRevisionNum(cmd):
    try:
        getLogger().info('Get last revision number: %s' % cmd)
        stdout, stderr =runCommand(cmd)
        m = re.search('Change (\d+)', stdout)
        if m is None:
            raise Exception("Can't get last revision number")
        return m.group(1)
    except Exception, e:
        raise e

def syncTestWorkspace(revision_num='latest'):
    if revision_num=='latest':
        cmd = 'p4 sync'
    else:
        cmd = 'p4 sync @' % revision_num
    try:
        #Sync test workspace
        getLogger().info('Sync test workspace: %s' %cmd)
        stdout, stderr = runCommand(cmd)
        getLogger().info('Result from sync command: %s' % stdout)
        if revision_num=='latest':
            #Get revision number
            cmd = 'p4 changes -m1 @%s' % CONF['test_workspace_name']
            return getLastRevisionNum(cmd)
        else:
            return revision_num
    except Exception, e:
        raise e

def syncDevWorkspace(revision_num='latest'):
    if revision_num=='latest':
        cmd = 'p4 sync'
    else:
        cmd = 'p4 sync @%s' % revision_num
    try:
        #Switch to dev workspace
        os.chdir(CONF['dev_workspace_path'])
        getLogger().info('Sync dev workspace: %s' %cmd)
        stdout, stderr = runCommand(cmd)
        getLogger().info('Result from sync command: %s' % stdout)
        #Restore back to current workspace
        os.chdir(os.path.abspath(os.path.dirname(__file__)))
        if revision_num=='latest':
            #Get revision number
            cmd = 'p4 changes -m1 @%s' % CONF['dev_workspace_name']
            return getLastRevisionNum(cmd)
        else:
            return revision_num
    except Exception, e:
        raise e

def main(argvs):
    import getopt
    try:
        opts, args = getopt.getopt(argvs, 'hp:t:p:r:', ['help', 'target', 'priority', 'revision', 'uploadresult', 'sendmail', 'offline'])
    except Exception, e:
        print e
        sys.exit(0)
    if len(opts)==0:
        print 'Pls specify build target id! To see a list of available targets and their corresponding IDs, execute: android list targets.'
        sys.exit(0)
    target = None
    uploadResult = False
    sendMail = False
    priority = 'All'
    revision_num = 'latest'
    offline = False
    try:
        for op, value in opts:
            if op in ('-h', '--help'):
                print help_content
                sys.exit(0)
            if op in ('-t', '--target'):
                target = value
            if op in ('-p', '--priority'):
                if value.lower() in ('all', 'high', 'medium', 'low'):
                    priority = value.lower().capitalize()
                else:
                    print 'Pls set correct test priority!'
                    sys.exit(0)
            if op in ('-r', '--revision'):
                if not value.isdigit():
                    print 'Incorrect format of revision number'
                    sys.exit(0)
                revision_num = value
            if op.lower()=='--uploadresult':
                uploadResult = True
            if op.lower()=='--sendmail':
                sendMail = True
            if op.lower()=='--offline':
                offline = True
        #User doesn't specify build target, pops up an error and quit
        if target is None:
            print 'Pls specify build target id! To see a list of available targets and their corresponding IDs, execute: android list targets.'
            sys.exit(0)
        #If user chooses online mode to trigger test execution, then revision_num must be provided
        if not offline:
            if not revision_num.isdigit():
                print 'Pls correct correct revision number.'
                sys.exit(0)
        #Process test execution according to parameter passed in
        processCommand(target, priority, revision_num, offline, uploadResult, sendMail)
    except Exception, e:
        getLogger().error('Encounter an exception: %s' % e)

if __name__=='__main__':
    main(sys.argv[1:])