
set "jar=jakarta.servlet-api-6.1.0-M2.jar"

javac -d "src" -cp "%jar% "src/*.java"

rem création du .war
cd src 
jar -cvf FrontController.jar servlet annotation mapping utility modelview exception

pause
