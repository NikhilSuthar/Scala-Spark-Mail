# ScalaMail
This utility uses to send Spark job failure and Success message in mail. It uses smtp protocol to send mail.
It is very easy to use, you just need to create a object and provide a complete path of Configuratin file where you need to mentioned all properties mentioned in below application.conf file.

https://github.com/NikhilSuthar/ScalaMail/blob/master/src/main/resource/application.conf

**Compiled Version**
* Scala "2.11.12"
* SBT 1.2.8

# How to Use in Program
* Download complete Jar file directly from below path or else clone it and compile it using command `sbt clean assembly`
 
    https://github.com/NikhilSuthar/ScalaMail/tree/master/Jar
  
* Add this Jar dependencies in Spark Project (in build.sbt) like below:
  
  `"com.spark.mail" %% "Email" % "0.0.1" from "file:///<path of Jar>/Scala_Spark_Mail.jar"`
 
* Edit configuration file and add below properties 
  	
		email {
		email_host="smtp.gmail.com"
		email_port="587"
        	email_username= "userName"
        	email_password="welcome123"
        	email_recipient="<list of receipent mail with comma separated>"
        	}
	
		spark {
		appName = "Spark_Test_Job"
		}


* Import method in Spark Scala Object or Class
* Initialize Email Object as below
   
   `val msg = "Spark Job failed" //you can initialize msg as exception of failure`
   
   `val Emailobj = new Email(<path of Conf file>)`
   
   `Emailobj.sendMail(msg)`
   
   # sendMail Method
   sendMail method comes with four parameters, in which three parameters are optional. Please find below details:
    
    * **sendMail(message, SparkApplicationId, MailSubjectLine,MailType)**
		* ***sendMail(message,SparkApplicationId,"","F")***: Send message as Failure with subject as "Alert:Spark SparkAppName Job.".
		* ***sendMail(message,SparkApplicationId,MailSubjectLine,"F")***: Send message as Failure with subject as MailSubjectLine.
		* ***sendMail(message,SparkApplicationId,"","S")***: Send message as Sucess with subject as "Alert:Spark SparkAppName Job.".
		* ***sendMail(message,SparkApplicationId,MailSubjectLine,"S")***: Send message as Sucess with subject as MailSubjectLine.
		* ***sendMail(message,SparkApplicationId)***: Send message as Sucess with subject as "Alert:Spark SparkAppName Job.". 
		* ***sendMail(message)***: Send message as Sucess with subject as "Alert:Spark SparkAppName Job.". It can use for Scala Job without Spark. 
		
    * **Default Value**
		* SparkApplicationId = blank ("")
		* MailSubjectLine = "Alert:Spark *SparkAppName* Job."
		* MailType = "S"  
			* F - For Failure 
			* S or Blank("") - For Success
	

   
  # Sample Spark Code
    	
		object TestSPark {
		    def main(args: Array[String]): Unit = {
			try{
			      /* Logic of Spark Program */
			     val Emailobj = new Email(<path of Conf file>)
				 Emailobj.sendMail(msg)
			    } catch {
				    case e: Exception => 
					    val msg = e.toString
					    val Emailobj = new Email(<path of Conf file>)
					    Emailobj.sendMail(msg,appId,"","F")
				       }
			     }
		      }
 
