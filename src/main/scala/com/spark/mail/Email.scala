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
  var Email_Auth:String =""
  var email_ssl_enable:String = "false"
  var email_starttls_enable:String = "true"

  def ConfLoader():Unit = {
    try{
    val config = ConfigFactory.parseFile(new File(Conf))
    Email_username = config.getString("email.email_username")
    Email_password = config.getString("email.email_password")
    Email_recipient=config.getString("email.email_recipient")
    Email_host=config.getString("email.email_host")
    Email_port=config.getString("email.email_port")
    Email_Auth = config.getString("email.email_auth").toLowerCase
    SparkAppName = config.getString("spark.appName")
    email_ssl_enable=config.getString("email.email_ssl_enable").toLowerCase
    email_starttls_enable=config.getString("email.email_starttls_enable").toLowerCase
    } catch {
      case e:Exception => println("[Email:ConfLoader]........Error Occurred:..." + e)
    }
  }

  def sendMail(text: String,appId:String = "",Subject:String ="",MailType:String="",defaultMsg:String =""):Unit = {
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
    properties.put("mail.smtp.auth", Email_Auth)
    properties.put("mail.smtp.ssl.enable", email_ssl_enable)
      properties.put("mail.smtp.starttls.enable", email_starttls_enable)


    val session = Session.getDefaultInstance(properties)
    val message = new MimeMessage(session)
    val recipientList:List[String] = Email_recipient.split(",").toList
    recipientList.foreach { recipient =>
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient))
    }
      if(Subject.isEmpty) {
        message.setSubject("Alert: Spark " + SparkAppName + " Job.")
      } else {
        message.setSubject(Subject)
      }

      var SparkJobName:String = ""

      if (appId.isEmpty || appId.trim != ""){
        SparkJobName = SparkAppName
      } else {
        SparkJobName = SparkAppName + "( " + appId + " )"
      }

      var defMsg:String = ""

      var msg = ""
      if(MailType.toUpperCase() == "F") {
        if (defaultMsg.isEmpty || defaultMsg.trim() == ""){
          defMsg = "<br><h3>Spark Job <b>" + SparkJobName + "</b> has been Failed On Date Time" + DateTime + "</h3>" + "<br><u><b>Failed due to below reason:</b></u>"
        } else {
          defMsg = "<br><h3>" + defaultMsg + "</b></h3>"
        }

        msg = "<br>" +
          defMsg +
          "<br><p><font " + "face=" + "Lucida Console" + ">" + text + "</font></p>"
      } else if (MailType.toUpperCase() == "R") {
        if (defaultMsg.isEmpty || defaultMsg.trim() == ""){
          defMsg = "<br><h3><b>Please find below Report:</b></h3>"
        } else defMsg = "<br><h3>" + defaultMsg + "</b></h3>"
          msg = defMsg + "<br>" + text
      } else {
        defMsg = "<br><h3>" + defaultMsg + "</b></h3>"

        msg = "<br>" + defMsg +
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
