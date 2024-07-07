
set "jar=import jakarta.servlet.*;
import jakarta.servlet.http.*;"

javac -d "src" -cp "%jar%" "src/*.java"

rem création du .war
cd src 
jar -cvf FrontController.jar servlet annotation mapping utility modelview exception session

pause
