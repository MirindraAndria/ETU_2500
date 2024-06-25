package servlet ; 

import java.util.*;

import annotation.*;
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
import modelview.ModelView ; 
import exception.* ;
import java.lang.annotation.Annotation;

public class FrontController extends HttpServlet {
    

    private String Source ;  
    private HashMap<String, Mapping> HashmapUtility =  new HashMap<>(); 
    private Utility util = new Utility(); 

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
        try{
            ServletContext context = getServletContext() ; 
            if( context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() != null){
                String classpath = context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() ; 
                String normalizedPath = this.util.normalizePath(classpath);   
                String packageName = this.util.transformPath(this.Source) ;   
                this.util.AddMethodeAnnotation( normalizedPath , packageName , this.HashmapUtility) ; 
            }else {  throw new Exception("Error package Scan verify our file xml \n") ;  } 
        }catch(Exception e )
        { e.printStackTrace(); }   
    } 

    public void dispacthModelView( ModelView mv , HttpServletRequest request  ,  HttpServletResponse response )
    {
        try{
            Set<String> keyMap= mv.getData().keySet(); 
            for(String keymap : keyMap)
            {
                request.setAttribute( keymap , mv.getData().get(keymap)) ; 
            }
            RequestDispatcher dispatch = request.getRequestDispatcher( mv.getUrl());
            dispatch.forward(request, response);
        }catch(Exception e )
        { 
            System.out.println(e);
        }
    }

    public ArrayList<Object> verifyCorrespondence( HttpServletRequest request  , Method myMethod   , PrintWriter out ) throws Exception
    {
        try{ 
            ArrayList<Object> valueArg = new ArrayList<>() ; 
            Parameter[] parameters = myMethod.getParameters();
              for (Parameter parameter : parameters) {

                        Annotation paramAnnotations = parameter.getAnnotation( AnnotationParam.class) ;  
                        String paramName = parameter.getName();
                        Class<?> paramType = parameter.getType();
                        if( paramAnnotations != null)
                        {       
                                out.print("paramType : " + paramType.getSimpleName() +  " \n") ; 
                                if( this.util.identifyType(paramType.getSimpleName() ) == false ){    //Si c'est un object 
                                    Object objParam = paramType.getDeclaredConstructor().newInstance();
                                    valueArg.add( objParam )  ; 
                                    out.print("Add obj \n") ;   
                                }if( this.util.identifyType(paramType.getSimpleName() ) ){
                                    AnnotationParam annotationParam = (AnnotationParam) paramAnnotations;  //Si c'est Annottee
                                    valueArg.add( request.getParameter(  annotationParam.name() ) ) ;  
                                    out.print("AnnotParma namer : " + annotationParam.name() +  "\n") ; 
                                }
                        }else { 
                                if( this.util.identifyType(paramType.getSimpleName() ) == false ){    //Si c'est un object 
                                    Object objParam = paramType.getDeclaredConstructor().newInstance();
                                    valueArg.add( objParam )  ; 
                                    out.print(" Add valuesArg Emp  no annotation \n ") ;
                                }if( this.util.identifyType(paramType.getSimpleName() )  ){
                                    valueArg.add( request.getParameter( paramName ) ) ; //Si c'est pas Annotter
                                    out.print(" Add valuesArg String  no annotation \n ") ;
                                }
                        } 
                }

                return valueArg ; 
             
            }catch(Exception e )
            {
                e.printStackTrace() ;
            }
            return null ; 
    } 
   
    public void setObjectParam ( Method myMethod  , ArrayList<Object> valueArg  ,HttpServletRequest request  , PrintWriter out ) throws Exception 
    {
         try{ 
                Enumeration<String> parameterNames = request.getParameterNames() ;   
                String[] part = null ; 
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    String[] partiesInput = paramName.split("\\.");
                    if( partiesInput.length > 1 ) 
                    { 
                        this.util.SetAttributeObject( myMethod , valueArg, partiesInput , request , out); 
                        throw new Exception("SetAttribut Active \n") ;
                    }
                }       

         }catch(Exception e)
         { e.printStackTrace(); } 
    } 
 
    public void ShowResult(Mapping value  , PrintWriter out  , HttpServletRequest request  ,  HttpServletResponse response  )throws TypeErrorException ,Exception
    {
        try { 
                out.print("Classe Name : " + value.getClasseName() + " , " + "Methode Name : " + value.getMethodeName() + "\n");
                Class myClass = Class.forName(value.getClasseName());
                Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) ; 
                Method myMethod = this.util.checkMethod(myClass, value.getMethodeName()  ) ;  
                ArrayList<Object> valueArg = this.verifyCorrespondence( request , myMethod  , out ) ;
                Object res = this.util.invokingMethod( valueArg , myObject , myMethod ) ;    
                this.setObjectParam(myMethod, valueArg, request , out);    
                if( res instanceof String)
                {  out.print("Valeur de la methode String :" + res + "\n") ; } 
                else if( res instanceof ModelView)
                {  
                    this.dispacthModelView( (ModelView)res , request , response); 
                }
                else { throw new TypeErrorException(" Error Type of return incorrect methode "); }   
        }catch(Exception e )
        {  e.printStackTrace() ; }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws TypeErrorException ,Exception
    {  
        try{
            PrintWriter out = response.getWriter() ; 
            StringBuffer url = request.getRequestURL();
            String urlString = url.toString();
            String transformed = this.util.transformPath2(urlString) ;   
            Mapping mapping = HashmapUtility.get(transformed);
            if(mapping == null) {
                throw new Exception("404 error , url incorrect");
            }
            else
            { this.ShowResult(mapping  , out , request , response ); }

        }catch( Exception e ) 
        {
           System.out.println(e) ; 
        }
    }   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter() ; 

        try{
            processRequest(request, response);
        }catch(Exception e){
                e.printStackTrace(out) ;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter() ; 
        try{
            processRequest(request, response);
        }catch(Exception e )
        {
            e.printStackTrace(out) ;
        }
    }
}


