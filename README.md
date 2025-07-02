# CapeInjector
CapeInjector is a JavaAgent that allows you to give yourself a custom cape.
## Supported versions
- 1.8.9
- TO COME..
## Usage
1. Download it.
2. Add -javaagent:PATH_TO_JAR.jar=1.8.9 to your JVM arguments.
3. Launch Minecraft 1.8.9.
## Building
To build the project, you'll need your own Minecraft 1.8.9 deobfuscated jar file. In the root of the project make a folder called `lib`, rename the jar to `mcp-mapped.jar` and put it in the folder. <br >
Then run `gradlew shadowJar` and the jar will be in `build/libs/`.
## Wait, how do I actually get a deobfuscated jar?
Use Recaf, use the tiny mappings in resources, and map and save the jar.
## Tools used
Reference when lost: LynithDev's Zabu <br >
Dependencies: NotEvenJoking's Mappings Util 