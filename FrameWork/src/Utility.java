package utility ; 

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import annotation.AnnotationGet;
import mapping.Mapping;
import annotation.* ; 

public class Utility {
    public Utility()
    {

    }
    //L'instabiliter de tomcat modifie le path c'est la raison de cette fonction 
    public String normalizePath(String path) {
       //Enlever le premier / 
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        // Remplace tous les % en espace 
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public String PathWithoutPackageName(String path)
    {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            } 
            // Find the last occurrence of '/'
            int lastSlashIndex = path.lastIndexOf('/');
            // Extract the substring up to the last '/'
            String transformed = path.substring(0, lastSlashIndex + 1);
            return transformed;
    }


    public String transformPath(String path) {
        //Verifier si le dernier caracter est un "/" 
        if (path.endsWith("/")) {
            //suppression du "/""
            path = path.substring(0, path.length() - 1);
        }
        String[] parts = path.split("/");
        //Dernier element du path 
        String lastElement = parts[parts.length - 1];
        // Concatenange avec un point 
        String transformed =  lastElement + "." ; 
        return transformed;
    }


    //Concatenage du package et classeName 
    public String fusionPackageAndClassName(String className , String packageName)
    {
        String path =  packageName + className ; 
        return path ; 
    }


    public void AddMethodeAnnotation( String normalizedPath , String packageName  , HashMap HashmapUtility) throws Exception 
    {  
        File classpathDirectory = new File(normalizedPath) ; 
        for ( File file : classpathDirectory.listFiles() )   
        {
        //Prendre tous files avec avec un .class a la fin  
             if(file.isFile() && file.getName().endsWith(".class"))
             {   
             
                //Prendre le nom de la classe 
                String className = file.getName().substring( 0 , file.getName().length() - 6 ) ; 
                String trueClassName = this.fusionPackageAndClassName(className , packageName); 
               
                //Transformation en classe
                 Class<?> myclass = Thread.currentThread().getContextClassLoader().loadClass(trueClassName) ; 
                    if(myclass.isAnnotationPresent(AnnotationController.class))
                    {
                            Method [] methods = myclass.getDeclaredMethods() ;
                            //Liste de Methode pour chaque Classe 
                            for (Method method : methods)
                                if(method.isAnnotationPresent(AnnotationGet.class))
                                {
                                    AnnotationGet annotation = method.getAnnotation(AnnotationGet.class);
                                    String url = annotation.name();
                                    Mapping mapping = new Mapping( trueClassName, method.getName() )  ;
                                    //Ajout des information dans le Hashmap 
                                    HashmapUtility.put( url , mapping ) ;
                                }
                    }
            } 
        }
    } 
}
