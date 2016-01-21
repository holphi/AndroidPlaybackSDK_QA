'''
@COPYRIGHT:	(C) 2014 Dolby Laboratories,. All rights reserved.
@Author:	Alex LI

@History:
2014/03/24	Design a web server, provide RESTful web services that Auto cases could communicate with;
2014/03/24  Provide bandwidth throttling related API;
2014/03/25  Add extra code to workaround the known issues of dummynet on Win 64bit platform;
2014/03/25  Change port number to avoid to conflicts
2014/03/26	Replace the host name 'localhost' with '0.0.0.0'
2014/03/26  Use JSON object as the return type

'''

import sys, os
import re

from bottle import route, run, template, error

@route('/')
def index():
    return {'result':True}

@route('/ipfw/setup/<direction>')
def ipfw_setup(direction):
    if direction.lower()=='in':
        print 'Add rule to control outgoing bandwidth'
        stdout, errout = invokeCommand(processIpfwCmd('ipfw add pipe 2 in proto tcp'))
        #Workaround the known issue of dummynet on win 64bit platform
        if 'ProgramFiles(x86)' in os.environ:
            invokeCommand(processIpfwCmd('ipfw pipe 2 config bw 100000kbit/s'))
    elif direction.lower()=='out':
        print 'Add rule to control outgoing bandwidth'
        stdout, errout = invokeCommand(processIpfwCmd('ipfw add pipe 3 out proto tcp'))
        #Workaround the known issue of dummynet on win 64bit platform
        if 'ProgramFiles(x86)' in os.environ:
            invokeCommand(processIpfwCmd('ipfw pipe 3 config bw 100000kbit/s'))
    else:
        return {'result':False}
    print 'Command result: %s' % stdout
    pattern = 'from any to any'
    if pattern in stdout:
        return {'result':True}
    else:
        return {'result':False}

@route('/ipfw/config/<direction>/<bandwidth>')
def ipfw_config(direction, bandwidth):
    if direction.lower()=='in':
        print 'Limit incoming bandwidth to %skbit/s' % bandwidth
        cmd = processIpfwCmd('ipfw pipe 2 config bw %skbit/s' % bandwidth)
    elif direction.lower()=='out':
        print 'Limit outgoing bandwidth to %skbit/s' % bandwidth
        cmd = processIpfwCmd('ipfw pipe 3 config bw %skbit/s' % bandwidth)
    else:
        return {'result':False}
    stdout, errout = invokeCommand(cmd)
    print 'Command result: %s' % stdout
    if stdout=='':
        return {'result':True}
    else:
        return {'result':False}

@route('/ipfw/cleanup')
def ipfw_cleanup():
    print 'Delete rules'
    stdout, errout = invokeCommand(processIpfwCmd('ipfw show'))
    #Remove all incoming rules
    rules = re.findall(r'(0\d{2}00)\s+\d+\s+\d+ pipe \d+ ip from any to any in proto tcp', stdout)
    if len(rules)!=0:
        for rule in rules:
            invokeCommand(processIpfwCmd('ipfw delete %s' % rule))
    #Remove all outgoing rules
    rules = re.findall(r'(0\d{2}00)\s+\d+\s+\d+ pipe \d+ ip from any to any out proto tcp', stdout)
    if len(rules)!=0:
        for rule in rules:
            invokeCommand(processIpfwCmd('ipfw delete %s' % rule))
    stdout, errout = invokeCommand(processIpfwCmd('ipfw show'))
    incoming_rules = re.findall(r'(0\d{2}00)\s+\d+\s+\d+ pipe \d+ ip from any to any in proto tcp', stdout)
    outgoing_rules = re.findall(r'(0\d{2}00)\s+\d+\s+\d+ pipe \d+ ip from any to any out proto tcp', stdout)
    if len(incoming_rules)==0 and len(outgoing_rules)==0:
        return {'result':True}
    else:
        return {'result':False}

#Add proper command prefix according to platform where the server's running at 
def processIpfwCmd(cmd):
    prefix = ''
    if sys.platform != 'win32':
        prefix = '..\\Dummynet\\32bit\\'
    elif 'ProgramFiles(x86)' in os.environ:
        prefix = '..\\Dummynet\\64bit\\'
    else:
        prefix = 'sudo '
    return prefix + cmd

#Run ipfw command
def invokeCommand(cmd):
    import subprocess
    print 'Execute command: %s' % cmd
    p = subprocess.Popen(cmd, shell=True, stdout = subprocess.PIPE, stderr=subprocess.STDOUT)
    stdout, erroutput = p.communicate()
    return stdout, erroutput

#Start the test server
run(host='0.0.0.0', port=8095)