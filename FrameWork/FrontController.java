package servlet ; 

import java.util.*; 
import java.text.* ; 
import java.io.* ; 
import java.lang.reflect.* ;
import mapping.* ; 
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import utility.Utility ; 

public class FrontController extends HttpServlet {
    

    private String Source ;  
    private HashMap<String, Mapping> HashmapUtility =  new HashMap<>(); 

    public void init() throws ServletException 
    {
        try
        {
            //Prendre  les controllers dans le WEB.xml 
            this.Source = this.getInitParameter("Source")  ;
            this.configMap(); 
        }catch(Exception e )
        {
            e.printStackTrace() ; 
        }
    } 

    public boolean pathVerification(String path , String transformed)
    {
        if(path.equals(transformed))
        {
            return true ; 
        }
        return false ;
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

    public void configMap() throws Exception 
    {
        Utility util = new Utility()  ; 
        ServletContext context = getServletContext() ; 
        String classpath = context.getResource(util.PathWithoutPackageName(this.Source)).getPath() ; 
        String normalizedPath = util.normalizePath(classpath);   
        String packageName = util.transformPath(this.Source) ;   
        util.AddMethodeAnnotation( normalizedPath , packageName , this.HashmapUtility) ; 
    } 

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
    {  
        try{
            PrintWriter out = response.getWriter() ; 
            StringBuffer url = request.getRequestURL();
            String urlString = url.toString();
            String transformed = this.transformPath(urlString) ; 
               
            Set<String> keysEncountered = new HashSet<>(); // Pour stocker les clés déjà rencontrées
            for (Map.Entry<String, Mapping> entry : this.HashmapUtility.entrySet()) {
                String keyUrl = entry.getKey();
                    Mapping value = entry.getValue();
                    if (this.pathVerification(transformed, keyUrl)) {
                        out.print("Classe Name : " + value.getClasseName() + " , " + "Methode Name : " + value.getMethodeName() + "\n");
                  
                        Class myClass = Class.forName(value.getClasseName());
                        Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) ; 
                        Method myMethod = myClass.getDeclaredMethod(value.getMethodeName() , new Class[0]) ; 
                        String result  = (String)myMethod.invoke( myObject , new Object[0]);

                        out.print("Valeur du methode :" + result + "\n") ; 

                    } else {
                        out.print("Aucune Methode annote present dans les classes \n");
                    }
                }
        }catch( Exception e ) 
        {
            System.out.println(e);
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


