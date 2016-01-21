'''

@COPYRIGHT: (C) 2014 Dolby Laboratories,. All rights reserved.
@Author:    Alex LI

@History:
2014/03/13  Remove scarttered common modules, create a QA infrastructure instead. 
2014/03/14  Add functions to generate pictures for test failures by test suites, and priority respectively
2014/03/15  Add functions for automation test report delivery
2014/03/15  Add Dolby logo when generate test report
2014/03/18  Add functions to send out moudule integration test report
2014/03/25  Add functions to launch and kill REST Server
2014/04/09  Fix an auto test report generation issue
2014/04/10  Add method to terminate REST Server after completing the automation execution
2014/06/11  Add method to compress specific folder by 7zip
2014/06/12  Attach zip package to the auto report body

'''

import logging
import sys
import os
import smtplib
import matplotlib.pyplot as plt

from xml.dom import minidom
from xml import dom
from os.path import join, abspath, exists, isfile, dirname
from collections import OrderedDict

_LOGGERINST = None
_PID_OF_REST_SERVER = None

#Output test log in real time 
def getLogger():
    from time import time,strftime,localtime
    global _LOGGERINST
    if _LOGGERINST is None:
        #Specify an unique file name
        filename = strftime('%Y%m%d%H%M%S', localtime(time()))
        _LOGGERINST = logging.getLogger()
        formatter = logging.Formatter('%(asctime)s %(levelname)-12s %(message)s', '%a, %d %b %Y %H:%M:%S',)
        file_handler = logging.FileHandler('%s.log' %(filename))
        file_handler.setFormatter(formatter)
        stream_handler = logging.StreamHandler(sys.stderr)
        stream_handler.setFormatter(formatter)
        _LOGGERINST.addHandler(file_handler)
        _LOGGERINST.addHandler(stream_handler)
        _LOGGERINST.setLevel(logging.INFO)
    return _LOGGERINST


#Compress folder to a zip file
def compressFolder(folder_name, zip_filename):
    import subprocess
    try:
        cmd = '.\\TestTools\\src\\7-zip\\64bit\\7z.exe a -tzip %s %s' %(zip_filename, folder_name)
        p = subprocess.Popen(cmd, shell=True, stdout = subprocess.PIPE, stderr=subprocess.STDOUT)
        stdout, stderr = p.communicate()
        return stdout
    except Exception, e:
        raise e

#Load XML format report, parser test result
class TestReportParser:

    #Initializer: load the xml report and populate test data
    def __init__(self, xmlRptPath):
        self.summaryInfo = {}
        self.testSuitesInfo = {}
        self.failureCasesInfo = {}
        self.errorCasesInfo = {}
        if not exists(xmlRptPath) or not isfile(xmlRptPath):
            raise Exception('Wrong xml file path.')
        try:
            doc = minidom.parse(xmlRptPath)
            root = doc.documentElement
            self.__parseSummaryInfo(root)
            self.__parseTestSuitesInfo(root)
        except Exception, e:
            raise e

    #Populate summary info
    def __parseSummaryInfo(self, root):
        dict = {}
        attributes = ['tests', 'failures', 'errors', 'skips']
        try:
            for attr in attributes:
                dict[attr] = int(root.getAttribute(attr))
            dict['passes'] = dict['tests'] - dict['errors'] - dict['failures'] - dict['skips']
            dict['passrate'] = float('%.2f' %((dict['passes']*1.0/dict['tests'])*100))
            dict['time'] = root.getAttribute('time')
            self.summaryInfo = dict
        except Exception, e:
            raise e

    #Populate test suite info
    def __parseTestSuitesInfo(self, root):
        dict = {}
        try:
            casesNodeList = root.getElementsByTagName('testcase')
            for element in casesNodeList:
                classname = element.getAttribute('classname')
                if classname not in dict.keys():
                    dict[classname] = {'T':0, 'P':0, 'F':0, 'E':0, 'S':0}
                dict[classname]['T'] = dict[classname]['T'] + 1
                if len(element.getElementsByTagName('failure')) == 0:
                    dict[classname]['P'] = dict[classname]['P'] + 1
                else:
                    failure_element = element.getElementsByTagName('failure')[0]
                    if failure_element.getAttribute('type') == 'junit.framework.AssertionFailedError':
                        dict[classname]['F'] = dict[classname]['F'] + 1
                        #Parse failure cases info
                        self.__parseFailureCasesInfo(element)
                    else:
                        dict[classname]['E'] = dict[classname]['E'] + 1
                        #Parse error cases info
                        self.__parseErrorCasesInfo(element)
            self.testSuitesInfo = OrderedDict(sorted(dict.items(),key = lambda t:t[0]))
        except Exception, e:
            raise e

    #Populate failure cases info
    def __parseFailureCasesInfo(self, element):
        try:
            dict = {}
            methodname = element.getAttribute('methodname')
            dict['name'] = element.getAttribute('name')
            dict['classname'] = element.getAttribute('classname')
            dict['priority'] = element.getAttribute('priority')
            dict['failuremsg'] = element.getElementsByTagName('failure')[0].firstChild.nodeValue
            self.failureCasesInfo[methodname] = dict
        except Exception, ex:
            raise ex

    #Populate error cases info
    def __parseErrorCasesInfo(self, element):
        try:
            dict = {}
            methodname = element.getAttribute('methodname')
            dict['name'] = element.getAttribute('name')
            dict['classname']= element.getAttribute('classname')
            dict['priority']= element.getAttribute('priority')
            dict['failuremsg'] = element.getElementsByTagName('failure')[0].firstChild.nodeValue
            self.errorCasesInfo[methodname] = dict
        except Exception, ex:
            raise ex


#Load the report template, populate test result info to the template and generate failure charts
def generateReport(rptTitle, testEnvInfo, reportParser):
    from jinja2 import Environment, PackageLoader, Template
    filename = 'report.dat'
    try:
        #Populate test result info to the template
        removeFile(filename)
        env = Environment(loader=PackageLoader(__name__, 'template'))
        template = env.get_template('rpt.tpl')
        txt = template.render(reportTitle = rptTitle, envInfo = testEnvInfo, filteredData = reportParser)
        file_obj = open('report.dat', 'w')
        file_obj.write(txt)
        file_obj.close()
        #Draw failure chart by priorities
        __drawFailureChartByTestSuites(reportParser.testSuitesInfo)
        #Draw failure chart by testsuites
        __drawFailureChartByTestPriorities(reportParser)
    except Exception, ex:
        raise ex

def removeFile(filename):
    try:
        if isfile(filename) and exists(filename):
            os.remove(filename)
    except Exception, ex:
        raise ex

#Return current log file name
def getCurrentLogFileName():
    import re
    try:
        files = [f for f in os.listdir('.') if re.match('[0-9]+.*\.log', f)]
        if files is not None and len(files)!=0:
            return files[len(files)-1]
        else:
            return ''
    except Exception, ex:
        raise ex

#Draw a pie chart to present the test failures by test suites
def __drawFailureChartByTestSuites(testSuitesInfo):
    labels = []
    data = []
    for suiteName in testSuitesInfo:
        suiteData = testSuitesInfo[suiteName]
        failures = suiteData['F']
        errors = suiteData['E']
        if failures!=0 or errors!=0:
            labels.append(suiteName)
            data.append(failures + errors)
    #Remove original picture
    fileName = 'failuresByTestSuites.png'
    removeFile(fileName)
    if len(labels)==0:
        return
    try:
        font = {'family':'Calibri', 'weight':'normal', 'size':6}
        plt.rc('font', **font)
        plt.figure(figsize = (4.5, 4.5))
        patches, texts = plt.pie(x=data, startangle=90, shadow = True)
        plt.legend(patches, labels, loc="lower left")
        plt.axis('equal')
        plt.title('Test failures By Test suites', fontsize=12)
        plt.savefig(fileName)
        plt.close()
    except Exception, ex:
        raise ex

#Draw a line chart to present test failures by priorities
def __drawFailureChartByTestPriorities(reportParser):
    results = {'High':0, 'Medium':0, 'Low':0}
    if len(reportParser.failureCasesInfo)!=0:
        for methodName in reportParser.failureCasesInfo:
            item = reportParser.failureCasesInfo[methodName]
            if item['priority']!='':
                pri_value = item['priority']
                results[pri_value] = results[pri_value] + 1 
    if len(reportParser.errorCasesInfo)!=0:
        for methodName in reportParser.errorCasesInfo:
            item = reportParser.errorCasesInfo[methodName]
            if item['priority']!='':
                pri_value = item['priority']
                results[pri_value] = results[pri_value] + 1
    #Remove original generated picture
    fileName = 'failuresByPriorities.png'
    removeFile(fileName)
    #Exit directly if all values in results are zero.
    if results['High']==0 and results['Medium']==0 and results['Low']==0:
        return
    try:
        #Draw bar chart
        font = {'family':'Calibri', 'weight':'normal', 'size':10}
        plt.rc('font', **font)
        fig = plt.figure(figsize = (4.5, 4.5))
        ax = fig.add_subplot(1, 1, 1)
        y = results.values()
        n = len(y)
        ind = range(n)
        ax.bar(ind, y, facecolor='gold', align='center', ecolor='black')
        ax.set_ylabel('Number of Failures')
        ax.set_yticks(range(11))
        #Set scale for y_axis 
        if (max(y))>10:
            ax.set_yticks(range(max(y)+1))
        else:
            ax.set_yticks(range(11))
        #Create the title, in italics
        ax.set_title('Test failures by priorities', fontstyle='italic')
        ax.set_xticks(ind)
        ax.set_xticklabels(results.keys())
        fig.autofmt_xdate()
        plt.savefig(fileName)
        plt.close()
    except Exception, ex:
        raise ex

def composeTestRpt(subject, sender, receivers):
    
    from email.mime.multipart import MIMEMultipart
    from email.mime.text import MIMEText
    from email.mime.image import MIMEImage
    from email.mime.base import MIMEBase
    from email.encoders import encode_base64

    #Create message body data
    msgbody = MIMEMultipart()
    msgbody['Subject'] = subject
    msgbody['From'] =  sender
    msgbody['To'] = receivers

    try:
        #Append auto test report data
        fileHandler = open('report.dat')
        body_txt = fileHandler.read()
        body = MIMEText(body_txt, _subtype='html')
        msgbody.attach(body)

        #Append dolby logo
        logo_file = join(dirname(__file__),'template','global.logo')
        imgdata = open(logo_file, 'rb').read()
        img = MIMEImage(imgdata, 'png')
        img.add_header('Content-Id', '<logo>')
        msgbody.attach(img)

        #If find failureByTestSuites.png, then attach it
        if exists('failuresByTestSuites.png'):
            imgdata = open('failuresByTestSuites.png', 'rb').read()
            img = MIMEImage(imgdata, 'png')
            img.add_header('Content-Id', '<failureBySuites>')
            msgbody.attach(img)

        #If find failureByPriorities.png, then attach it
        if exists('failuresByPriorities.png'):
            imgdata = open('failuresByPriorities.png', 'rb').read()
            img = MIMEImage(imgdata, 'png')
            img.add_header('Content-Id', '<failureByPriorities>')
            msgbody.attach(img)

        #Attach JUnit test report
        testRptName = 'AutoTestResults.xml'
        if exists(testRptName):
            contype = 'application/octet-stream'
            maintype, subtype = contype.split('/',1)
            rptdata = open(testRptName,'rb')
            rpt = MIMEBase(maintype, subtype)
            rpt.set_payload(rptdata.read())
            rptdata.close()
            rpt['Content-Disposition'] = 'attachment;filename="%s"' % testRptName
            encode_base64(rpt)
            msgbody.attach(rpt)
                    
        #Attach current test execution log generated by runAutoTest
        logName = getCurrentLogFileName()
        if logName!='':
            contype = 'application/octet-stream'
            maintype, subtype = contype.split('/',1)
            logdata = open(logName,'rb')
            log = MIMEBase(maintype, subtype)
            log.set_payload(logdata.read())
            logdata.close()
            log['Content-Disposition'] = 'attachment;filename="%s"' % logName
            encode_base64(log)
            msgbody.attach(log)

        autoTestLog = 'AutoTestLog.zip'
        if exists(autoTestLog):
            contype = 'application/zip'
            maintype, subtype = contype.split('/',1)
            autoTestLogData = open(autoTestLog,'rb')
            testLog = MIMEBase(maintype, subtype)
            testLog.set_payload(autoTestLogData.read())
            autoTestLogData.close()
            testLog['Content-Disposition'] = 'attachment;filename="%s"' % autoTestLog
            encode_base64(testLog)
            msgbody.attach(testLog)

        return msgbody
    
    except Exception, ex:
        raise ex

#Launch rest server and return its PID, the rest server is used to listen to http requests of Automation cases
def startRestServer():
    #Retrieve current python processes
    def getPythonPIDs():
        import psutil
        processName = 'python'
        ret = []
        #If current platform's Windows, then change the process names
        if sys.platform=='Win32' or 'ProgramFiles(x86)' in os.environ:
            processName = 'python.exe'
        for p in psutil.process_iter():
            try:
                if p.name() == processName:
                    ret.append(p.pid)
            except Exception, ex:
                pass
        return ret
    #Launch rest server
    import subprocess
    serviceName = join(dirname(__file__), 'rest.py')
    try:
        PIDs_1 = getPythonPIDs()
        retcode = subprocess.call('start python %s' % serviceName, shell=True)
        PIDs_2 = getPythonPIDs()
        #Do substraction, get the pid of rest server
        for pid in PIDs_1:
            if pid in PIDs_2:
                PIDs_2.remove(pid)
        if len(PIDs_2)!=1:
            raise Exception("Can't get the correct PID of rest server!")
        global _PID_OF_REST_SERVER 
        _PID_OF_REST_SERVER = PIDs_2[0]
        return retcode
    except Exception, ex:
        raise ex

#Terminate Rest server
def stopRestServer():
    import psutil
    try:
        psutil.Process(_PID_OF_REST_SERVER).kill()
    except NoSuchProcess, ex:
        pass
    except Exception, ex:
        raise ex

def sendAutoTestReport(smtp_address, subject, sender, receivers):
    receiverItems = receivers.split(';')
    receiverList = []
    #Append receivers to a list object
    for item in receiverItems:
        receiverList.append(item)
    try:
        msgbody = composeTestRpt(subject, sender, receivers)
        server = smtplib.SMTP()
        server.connect(smtp_address)
        server.sendmail(sender, receiverList, msgbody.as_string())
    except Exception, ex:
        raise ex
    finally:
        server.close()

if __name__ == '__main__':
    '''env = {'Hardware':'XXXXXXXXXXXXXX', 'OS Version':'XXXXXXXXXXXXXXX', 'Software':'XXXXXXXXXXXXXX'}
    t = TestReportParser('AutoTestResults.xml')
    title = 'Android, Hello this is a test!'
    generateReport(title, env, t)
    sendAutoTestReport('cas-prod.dolby.net', 'This is a test mail!', 'alex.li@dolby.com', 'alex.li@dolby.com;alex.li@dolby.com;holphi@outlook.com')'''
    startRestServer()