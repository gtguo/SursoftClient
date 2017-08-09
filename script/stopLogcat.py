#!/usr/bin/python2.7
import os
import sys
import re
import getopt

def usage():
    print "Usage py stopLogcat.py -d xxx"
    print "-d:device id "
    print "-h: usage"

deviceId = ""
if __name__ == "__main__":
    try:
        options, args = getopt.getopt(sys.argv[1:], "hd:t:l:")
    except getopt.GetoptError:
        sys.exit()
    if len(options) == 0:
        usage()
        sys.exit()
    for op, value in options:
        if op == "-d":
            deviceId = value
        elif op == "-h":
            usage()
            sys.exit()
    if deviceId.strip() == "":
        print "error"
        usage()
        sys.exit()

readPSList = list(os.popen('adb -s %s shell ps'%(deviceId)).readlines())
if len(readPSList) <2:
    print 'ps len <2'
    sys.exit()
for line in readPSList:
    if 'logcat' in line:
        ids = re.findall(r'\d+\b',line)
        if len(ids) > 0:
            print ("logcat pid:%s" % (ids[0]))
            tag = os.system('adb -s %s shell kill %s'%(deviceId,ids[0]))