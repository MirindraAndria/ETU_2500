Configuration Framework : 

	>Etape 1 : initialisation de framework 
		-- Copier tous les .jar du framework dans le repertoire lib de votre project de travail 
		-- Copier aussi les .jar du framework dans le repertoire lib dans web le repertoire a déployer dans votre serveur web 

	>Etape 2 : Configuration dans le .xml 
		-- configurer le fichier .xml dans votre project dans le repertoire web  a déployer
 			- Utiliser servlet-name , servlet-class , init-param , servlet-mapping  , serlvet-name  ,url-patrerm ...

			//Sprint 0 
			 - vos servlet-name devraient etre le nom vos .jar
			 - de meme pour servlet-classe mais avec le nom de son package 

			//Sprint 1 
			 - Utiliser init-param pour ajouter le path en chemin relatif contenant vos .classe 
			 - Ecrire dans le path le nom du package de votre .class avec un "/" apres 

            		//Sprint 2 
			 - url-pattern doit avoir le meme non d'annotation de votre methode
			 - le servlet-mapping utilise aussi le non de vos .jar 

	    		//Sprint4    
			- Inscrire l'annotation dans le path pour acceder au view