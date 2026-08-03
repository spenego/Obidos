/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package test.com.spenego.obidos;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;

/**
 * @author spgdev@spenego.com - Apr 23, 2017
 */
public class TestSpringMailSender
{
	private final static Logger logger = LoggerFactory.getLogger(TestSpringMailSender.class);


	public TestSpringMailSender() {}

	// spring bean
	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    private JavaMailSenderImpl ms;
    private final String to = "spgdev@spenego.com";
    private final String smtpUserPass = "SWNldmFyYkF4MiQ=";
    private final String smtpUsername = "Privacy.Schutz@gmail.com";
    private final String from = smtpUsername;
    private final String smtpServer = "smtp.gmail.com";
    private final int smtpPort = 587;
    private EmailActions emailActions = null;

    @Before()
    public void before()
    {
        ms = new JavaMailSenderImpl();
        ms.setHost("smtp.gmail.com");
        ms.setUsername(smtpUsername);
        String pass = new String(ServerUtils.decodeFromBase64(smtpUserPass));
        ms.setPassword(pass);

        // instantiate spring
		context.scan("com.spenego.Obidos.server");
		context.refresh();
		emailActions = (EmailActions) context.getBean("emailActions");
    }

    /**
     * Use Spring's SimpleMailMessgae
     * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html
     *
     * @param to
     * <p>
     * @author spgdev@spenego.com - Apr 23, 2017
     */
    private void sendTheMail(String to, String subject)
    {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText("This is a test");

        ms.send(msg);
    }

    //@Test
    public void testSendMailSSL()
    {
        ms.setPort(465);  // <<<<--------------
        ms.setProtocol("smtp");

        Properties p = new Properties();
        p.setProperty("mail.from", smtpUsername);
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.ssl.enable", "true"); // <<<<<-----------------------
        p.setProperty("mail.debug", "true");

        ms.setJavaMailProperties(p);

        sendTheMail(to, "Using Spring Mail SSL");
    }

    //@Test
    //@Async
    public void testSendMailStartTLS()
    {
        ms.setPort(587);  // <<<<--------------
        ms.setProtocol("smtp");

        Properties p = new Properties();
        p.setProperty("mail.from", smtpUsername);
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true"); // <<<---------------------------
        p.setProperty("mail.debug", "true");

        ms.setJavaMailProperties(p);

        sendTheMail(to, "Using Spring Mail StartTLS");

    }

    // I really want to use org.simplejavamail. The API feels very nice.
    // But this thing is a POS! Half of the it does not work!!!
    // I am thinking to dump the garbage.
    // Update: it is dumped. Look at ServerUtils.java which uses server/mail/EmailSender.java,
    // which uses Spring's mail component asynchronously
    // -- spgdev@spenego.com May-13-2018
    //@Test
    public void testSendEmailWithSimpleJavamailShit()
    {
        String pass = new String(ServerUtils.decodeFromBase64(smtpUserPass));
                Email email = EmailBuilder.startingBlank()
                .to(to)
                .from(from)
                .withSubject("This is a test from org.simplejavamila")
                .withHTMLText("<b>This is HTML bold</b>")
                .withPlainText("This is a text")
                .buildEmail();

        Mailer mailer = MailerBuilder
                .withSMTPServer(smtpServer, smtpPort, smtpUsername, pass)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer();

        mailer.sendMail(email);
    }

    // does not seem to work as unit test after I made spring async thing work
    // update: have to wait for the job to finish

    // this method is broken, it does not send mail in async mode.
    //  emailActions.sendSimple(envelope, smtpConfig) does not use asyc

    // update: emailActons.sendSimple is async now.
    // spgdev@spenego.com, Aug 25, 2018
    //@Test
    public void testMyAsyncEmailSender()
    {
        logger.info("EmailActions bean: " + emailActions);
		// initialize smtpconfig

		SmtpConfigDTO smtpConfig = new SmtpConfigDTO();
		smtpConfig.setSmtpServer(smtpServer);
		smtpConfig.setSmtpPort(smtpPort);
		smtpConfig.setSmtpUsername(smtpUsername);
        String pass = new String(ServerUtils.decodeFromBase64(smtpUserPass));
		smtpConfig.setSmtpPassword(pass);

		// initialize smtp envelope
		SmtpEnvelope envelope = new SmtpEnvelope();
		envelope.setTos(to);
		envelope.setFrom(from);
		envelope.setSubject("This is a test of EmailSender");
        String html = "<b>This is a html text in bold</b>";
        String text = "This is a plain text";
        envelope.setTextMessage(text);
        envelope.setHtmlMessage(html);
        logger.info("Send mail..");
        try
        {
            emailActions.sendSimple(envelope, smtpConfig);
        }catch (Exception e)
        {
            logger.info("Exception: " + e.getMessage());
        }

        logger.info("If you see this line immediately before mail sending starts .. it worked");
        try
        {
        	// email sender sleeps to 20 seconds for testing async at this time
        	logger.info("Waining for 30 seconds... for async job to finish");
            Thread.sleep(30000);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //@Test
    public void testSmtpEmailSender()  { /* no implementation */ }

    @After()
    public void after()
    {
        logger.info("Done");
    }
}
