package servlet ; 

import java.util.*; 
import java.text.* ; 
import java.io.* ; 
import java.lang.reflect.* ;
import annotation.* ; 
import mapping.* ; 
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FrontController extends HttpServlet {
    

    private String Source ;  
    private HashMap<String, Mapping> HashmapUtility =  new HashMap<>(); 
   

    public void init() throws ServletException 
    {
        try
        {
            //Prendre  les controllers dans le WEB.xml 
            this.Source = this.getInitParameter("Source")  ;
            this.AddMethodeAnnotation() ; 
        }catch(Exception e )
        {
            e.printStackTrace() ; 
        }
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
        String transformed = "/" + lastElement ; 
        return transformed;
    }
    public String transformPath1(String path) {
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

    public boolean pathVerification(String path , String transformed)
    {
        if(path.equals(transformed))
        {
            return true ; 
        }
        return false ;
    }

    public void AddMethodeAnnotation() throws Exception 
    {
        ServletContext context = getServletContext() ; 
        String classpath = context.getResource(this.PathWithoutPackageName(this.Source)).getPath() ; 
        String normalizedPath = this.normalizePath(classpath);   
        String packageName = this.transformPath1(this.Source) ;   
      
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
                                    this.HashmapUtility.put( url , mapping ) ;
                                }
                    }
                } 
             }
    } 
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        try
        {
            PrintWriter out = response.getWriter() ; 
            StringBuffer url = request.getRequestURL();
            String urlString = url.toString();
            String transformed = this.transformPath(urlString) ; 
               
            Set<String> keysEncountered = new HashSet<>(); // Pour stocker les clés déjà rencontrées
            for (Map.Entry<String, Mapping> entry : this.HashmapUtility.entrySet()) {
                String keyUrl = entry.getKey();
                // Vérifie si la clé a déjà été rencontrée
                if (keysEncountered.contains(keyUrl)) {
                    // Affichez l'erreur
                    out.println("Erreur :   Annotation dupliquée rencontrée dans diffrente classe : " + keyUrl);
                    // Vous pouvez choisir d'arrêter le traitement ici ou de continuer
                } else {
                    // Ajoute la clé à l'ensemble des clés rencontrées
                    keysEncountered.add(keyUrl);
                    Mapping value = entry.getValue();
                    if (this.pathVerification(transformed, keyUrl)) {
                        out.print("Classe Name : " + value.getClasseName() + " , " + "Methode Name : " + value.getMethodeName() + "\n");
                    } else {
                        out.print("Aucune Methode annote present dans les classes \n");
                    }
                }
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


