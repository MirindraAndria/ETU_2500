
rem Compilation des fichiers java dans temp et renvoie dans bin 

set "lil=D:\IT UNIVERSITY\JAR\jakarta"
set "destinationFolder=C:\Users\Mirindra\Documents\GitHub\ETU_2785\Framework\FrontController\classes"
cd "C:\Users\Mirindra\Documents\GitHub\ETU_2785\Framework\FrontController"
javac -d "%destinationFolder%" -cp "%lib%\*" *.java

pause

