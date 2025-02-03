Configuration Framework : 

	>Etape 1 : initialisation de framework 
		-- Copier tous les .jar du framework dans le repertoire lib de votre project de travail 
		-- Copier aussi les .jar du framework dans le repertoire lib dans web le repertoire a déployer dans votre serveur web 

	>Etape 2 : Configuration dans le .xml 
		-- configurer le fichier .xml dans votre project dans le repertoire web  a déployer

 			- Utiliser servlet-name , servlet-class , init-param , servlet-mapping  , serlvet-name  , url-patrerm ...

			//Sprint 0 
			 - vos servlet-name devraient etre le nom vos .jar
			 - de meme pour servlet-classe mais avec le nom de son package 

			//Sprint 1 
			 - Utiliser init-param pour ajouter le path en chemin relatif contenant vos .classe 
			 - Ecrire dans le path le nom du package de votre .class avec un "/" apres 

            //Sprint 3 
			 -Entrer juste l'url de votre project avec l'annotation de votre methode apres



			 - le servlet-mapping utilise aussi le non de vos .jar 

	   //Sprint 6  des inputs de formulaire doivent avoir le meme nom que le nom des paramatres de votre methode dans le controller ou le meme nom que votre Annotation de parametre que vous creez
	   //Sprint 8  Pour utiliser une session , utiliser un variable de type MySession dans l'atttribute de votre class controller ou 
	   comme parametre de la methode de votre class controller ; 
	   //Sprint 9 Annotater le method par AnnotationRestApi pour retourner les Valeur en JSON
	   

	   //Sprint 14 Validation :: ajouter une annotation sur un champ de votre class : 
 	   @AnnotationDecimal(min = "10.00", max = "20.00")
    	   int age ; 
	   verifier ensuite votre valeur dans une page ex :  request.getAttribute("Emp.age_error")  
	
	   //Sprint 15 Authentification :: ajouter une annotation ex : @AnnotationAuth( name="dg") sur un controller et faire une session avant d'y acceder ex :   				   session.addSession( "role" , "dg") ;   
	   configurer le non du session dans le web.xml et l'authentification et leur niveau dans un fichier .txt 

	