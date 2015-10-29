javac -cp src/:lib/org.json.jar -d bin/ src/com/robot/AIManager.java
java -Xmx100m -cp bin/:lib/org.json.jar com.robot.AIManager
