package servlet ; 

import java.util.*; 
import java.text.* ; 
import java.io.* ; 
import java.lang.reflect.* ;
import annotation.* ; 
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FrontController extends HttpServlet {
    
    private String Source ;  
    private Vector<Class> lsController ; 

    public void init() throws ServletException 
    {
        try
        {
            //Prendre le path dans le WEB.xml 
            this.Source = this.getInitParameter("Source")  ;
        }catch(Exception e )
        {
            e.printStackTrace() ; 
        }
    } 

    //Pour modifier les paths avec des "%"
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
        String transformed = lastElement + ".";
        return transformed;
    }
    //Concatenage du package et classeName 
    public String fusionPackageAndClassName(String className , String packageName)
    {
        String path =  packageName + className ; 
        return path ; 
    }
    //Prendre tous les controlers 
    public void ListeControllers() throws Exception 
    {
        ServletContext context = getServletContext() ; 
        String classpath = context.getResource(this.PathWithoutPackageName(this.Source)).getPath() ;
        //Correction du path 
        String normalizedPath = this.normalizePath(classpath);   
        //Prendre le nom du package
        String packageName = this.transformPath(this.Source) ;   
        File DirectoryClassPath = new File(normalizedPath) ; 

        this.lsController = new Vector<Class>() ;
        for ( File file : DirectoryClassPath.listFiles() )   
        {
        //Prendre tous files avec avec un .class a la fin  
             if(file.isFile() && file.getName().endsWith(".class"))
             {   
                //Prendre le nom de la classe 
                String className = file.getName().substring( 0 , file.getName().length() - 6 ) ; 
                //Prendre le nom de la classe avec le package
                String trueClassName = this.fusionPackageAndClassName(className , packageName); 
                //Transformation en classe
                 Class<?> myclass = Thread.currentThread().getContextClassLoader().loadClass(trueClassName) ; 
                if(myclass.isAnnotationPresent(AnnotationController.class))
                {
                    lsController.add(myclass) ; 
                } 
             }
        } 
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        try
        {
            PrintWriter out = response.getWriter() ; 
            this.ListeControllers() ; 
            out.print("Listes des controllers : ");
            for( int i = 0 ; i < this.lsController.size() ; i++ )
            {          
                out.print( (this.lsController.get(i)).getSimpleName() + "\n" ); 
            } 
        }catch( Exception e )
        {
            e.printStackTrace() ; 
            System.out.println(e) ; 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}


