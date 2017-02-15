@echo off
set mainClass=com.traclabs.ga.client.handlers.HandlerPanel
set buildDir="%GA_HOME%\build"
set jacoOrbClass=org.omg.CORBA.ORBClass=org.jacorb.orb.ORB
set jacoSingletonOrbClass=org.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
set machineType=MACHINE_TYPE=CYGWIN
set jacoNameIOR="ORBInitRef.NameService=file:%GA_HOME%\tmp\ns\ior.txt"
set resourceDir="%GA_HOME%\resources"
set plotClasspath="%GA_HOME%\lib\jfreechart\jcommon.jar;%GA_HOME%\lib\jfreechart\jfreechart.jar"
set jacoClasspath="%GA_HOME%\lib\jacorb/jacorb.jar;%GA_HOME%\lib\jacorb;%GA_HOME%\lib\jacorb/avalon-framework.jar;%GA_HOME%\lib\jacorb/logkit.jar"
set logCLasspath="%GA_HOME%\lib\log4j\log4j.jar;%GA_HOME%\lib\log4j\commons-logging.jar"
set gefClasspath="%GA_HOME%\lib\gef\gef.jar"
java -classpath %buildDir%;%resourceDir%;%logCLasspath%;%jacoClasspath%;%plotClasspath%;%gefClasspath% %machineTypeEnv% -D%jacoOrbClass% -D%jacoSingletonOrbClass% -D%jacoNameIOR% "%mainClass%" %*
pause