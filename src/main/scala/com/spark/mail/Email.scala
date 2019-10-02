package com.spark.mail

import java.io.File
import java.util.{Calendar, Properties}
import javax.activation.{CommandMap, MailcapCommandMap}
import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}
import com.typesafe.config.ConfigFactory

class Email(Conf:String) {

  var Email_host:String = ""
  var Email_port :String= ""
  var Email_username :String= ""
  var Email_password :String= ""
  var Email_recipient:String=""
  var SparkAppName:String=""

  def ConfLoader():Unit = {
    try{
    val config = ConfigFactory.parseFile(new File(Conf))
    Email_username = config.getString("email.email_username")
    Email_password = config.getString("email.email_password")
    Email_recipient=config.getString("email.email_recipient")
    Email_host=config.getString("email.email_host")
   Email_port=config.getString("email.email_port")
    SparkAppName = config.getString("spark.appName")
    } catch {
      case e:Exception => println("[Email:ConfLoader]........Error Occurred:..." + e)
    }
  }

  def sendMail(text: String,appId:String = "",Subject:String ="",MailType:String="F"):Unit = {
    ConfLoader()
    try{
    val mc = CommandMap.getDefaultCommandMap.asInstanceOf[MailcapCommandMap]
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html")
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml")
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain")
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed")
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822")
    CommandMap.setDefaultCommandMap(mc)
    // Thread.currentThread.setContextClassLoader(getClass.getClassLoader)
    val DateTime = Calendar.getInstance().getTime

    val properties = new Properties()
    properties.put("mail.smtp.port", Email_port)
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.ssl.enable", "false")


    val session = Session.getDefaultInstance(properties)
    val message = new MimeMessage(session)
    val recipientList:List[String] = Email_recipient.split(",").toList
    recipientList.foreach { recipient =>
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient))
    }
      if(Subject.isEmpty) {
        message.setSubject("Alert: Spark " + SparkAppName + " Job failed.")
      }else {
        message.setSubject(Subject)
      }

      var msg = ""
      if(MailType.toUpperCase() == "F") {
        msg = "<br>" +
          "<br><h3>Spark Job <b>" + SparkAppName + "( " + appId + " )" + "</b> has been Failed On Date Time " + DateTime + "</h3>" +
          "<br><u><b>Failed due to below reason:</b></u>" +
          "<br><p><font " + "face=" + "Lucida Console" + ">" + text + "</font></p>"
      } else if (MailType.toUpperCase() == "R") {
        msg="Hello ,<br>" +
          "<br><h3><b>Please find below Report:</b></h3>" +
          "<br>" + text
      } else {
        msg = "<br>" +
          "<br><p><font " + "face=" + "Lucida Console" + ">" + text + "</font></p>"
      }

    message.setContent(msg,"text/html")


    val transport = session.getTransport("smtp")

    // Thread.currentThread.setContextClassLoader(getClass.getClassLoader)
    transport.connect(Email_host, Email_username, Email_password)
    transport.sendMessage(message, message.getAllRecipients)

    transport.close()
    } catch {
      case e:Exception => println("[Email:sendMail]........Error Occurred:..." + e)
    }
  }

}
