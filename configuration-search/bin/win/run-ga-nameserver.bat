@echo off
set mainClass=org.jacorb.naming.NameServer
set jacoOrbClass=org.omg.CORBA.ORBClass=org.jacorb.orb.ORB
set jacoSingletonOrbClass=org.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
set iorOutput=jacorb.naming.ior_filename="%GA_HOME%\tmp\ns\ior.txt"
set resourceDir="%GA_HOME%\resources"
set jacoClasspath="%GA_HOME%\lib\jacorb\jacorb.jar;%GA_HOME%\lib\jacorb;%GA_HOME%\lib\jacorb\avalon-framework.jar;%GA_HOME%\lib\jacorb\logkit.jar"
mkdir "%GA_HOME%\tmp\ns"
echo running nameserver
java -classpath %jacoClasspath% -D%jacoOrbClass% -D%jacoSingletonOrbClass% -D%iorOutput% "%mainClass%" %*
pause