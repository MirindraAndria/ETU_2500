
set "jar=jakarta.servlet-api-6.1.0-M2.jar"

javac -d . -cp "%jar%" *.java

rem création du .war

jar -cvf FrontController.jar servlet annotation mapping 

pause
