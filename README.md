# bytecode-instrumental-example
This is simple example for bytecode instrumental of java. This example print execution time for "java.util.ArrayList.indexOf".<br>
The subject is showing APM tools how to collect information of a application without their parts of source code.

# Modules
## example-application
This is main application. This just run ArrayList.indexOf

## time-logging-agent
This is javaagent and using bytecode instrumental. This agent change ArrayList.indexOf method for execution time logging.
A javaagent require META-INF/MANIFEST.MF and this settings include time-logging-agent/build.gradle because our moudle use gradle build.<br>
A jar artfact of this module will include with META-INF/MANIFEST.MF follwing content after gradle build.
~~~
Can-Redefine-Classes: true
Can-Retransform-Classes: true
Premain-Class: com.kakaopay.pete.agent.TimeLoggingAgent
~~~

# Running
First, build project using gradle.
~~~
gradle build
~~~
Run the example-application with VM options.
~~~
-javaagent:time-logging-agent/build/libs/time-logging-agent-1.0-SNAPSHOT.jar
~~~
