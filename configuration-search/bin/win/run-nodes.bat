@echo off
@echo starting handler
set mainClass=com.traclabs.ga.server.nodes.GANodeServer
set buildDir="%GA_HOME%\build"
set jacoOrbClass=org.omg.CORBA.ORBClass=org.jacorb.orb.ORB
set jacoSingletonOrbClass=org.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
set jacoNameIOR="ORBInitRef.NameService=file:%GA_HOME%\tmp\ns\ior.txt"
set resourceDir="%GA_HOME%\resources"
set jacoClasspath="%GA_HOME%\lib\jacorb\jacorb.jar;%GA_HOME%\lib\jacorb;%GA_HOME%\lib\jacorb/avalon-framework.jar;%GA_HOME%\lib\jacorb\logkit.jar"
set logCLasspath="%GA_HOME%\lib\log4j\log4j.jar"
set biosimClasspath="%GA_HOME%\lib\biosim\biosim.jar"
java -classpath %buildDir%;%resourceDir%;%logCLasspath%;%jacoClasspath%;%biosimClasspath% %machineTypeEnv% -D%jacoOrbClass% -D%jacoSingletonOrbClass% -D%jacoNameIOR% "%mainClass%" %*
pause