@echo off
setlocal
set root=%~dp0
set classpath=%root%\dist\kcae.jar
set classpath=%classpath%;%root%\ext\commons-lang3-3.1.jar
set classpath=%classpath%;%root%\ext\commons-logging-1.1.1.jar
set classpath=%classpath%;%root%\ext\commons-logging-adapters-1.1.1.jar
set classpath=%classpath%;%root%\ext\commons-logging-api-1.1.1.jar
set classpath=%classpath%;%root%\ext\jackson-core-asl-1.9.7.jar
set classpath=%classpath%;%root%\ext\jackson-mapper-asl-1.9.7.jar
set classpath=%classpath%;%root%\ext\jython-2.7a2.jar
set classpath=%classpath%;%root%\ext\log4j-1.2.9.jar
set logger=org.apache.commons.logging.impl.Log4JLogger
set java=java
set args=

:setupArgs
if ""%1""=="""" goto doneSetupArgs
set args=%args% %1
shift
goto setupArgs

:doneSetupArgs
@echo on
"%java%" -Dorg.apache.commons.logging.Log="%logger%" -Dpython.verbose=debug -classpath "%classpath%" %args%

endlocal
