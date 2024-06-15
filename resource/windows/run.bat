@echo off
%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
java -jar C:\water_level_data\station-0.0.1-SNAPSHOT.jar > C:\water_level_data\server.log 2>&1 &
exit