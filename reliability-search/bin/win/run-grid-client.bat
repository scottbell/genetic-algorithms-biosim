@echo off
@echo starting grid client
set mainClass=com.traclabs.biosim.ga.grid.JGAPClient
set buildDir="%GA_HOME%\build"
set jacoOrbClass=org.omg.CORBA.ORBClass=org.jacorb.orb.ORB
set jacoSingletonOrbClass=org.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
set jacoNameIOR="ORBInitRef.NameService=file:%GA_HOME%\tmp\ns\ior.txt"
set jacorbClasspath="%BIOSIM_HOME%\lib\jacorb\jacorb.jar;%BIOSIM_HOME%\lib\jacorb;%BIOSIM_HOME%\lib\jacorb\avalon-framework.jar;%BIOSIM_HOME%\lib\jacorb\logkit.jar"
set logClasspath="%BIOSIM_HOME%\lib\log4j\log4j.jar"
set gaClasspath="%GA_HOME%\lib\jgap\jgap.jar;%GA_HOME%\lib\jgap\commons-cli.jar;%GA_HOME%\lib\jgap\jcgrid.jar"
set biosimClasspath="%BIOSIM_HOME%\build"
set resourceDir="%BIOSIM_HOME%\resources"
set plotClasspath="%GA_HOME%\lib\ptolemy\plot.jar"
java -classpath %buildDir%;%resourceDir%;%gaClasspath%;%logCLasspath%;%jacorbClasspath%;%biosimClasspath%;%plotClasspath% -D%jacoOrbClass% -D%jacoSingletonOrbClass% -D%jacoNameIOR% "%mainClass%" %*
pause