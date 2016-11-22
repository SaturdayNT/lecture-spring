package study.spring.helper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailHelper {

	// --> import org.springframework.mail.javamail.JavaMailSender;
	JavaMailSender mailSender;

	public MailHelper(JavaMailSender sender) {
		this.mailSender = sender;
	}

	/**
	 * 메일을 발송한다.
	 * @param sender - 발송자 메일 주소
	 * @param receiver - 수신자 메일 주소
	 * @param subject - 제목
	 * @param content - 내용
	 * @throws MessagingException
	 */
	// --> import javax.mail.MessagingException;
	public void sendMail(String sender, String receiver, String subject, String content)
			throws MessagingException {

		// --> import javax.mail.internet.MimeMessage;
		MimeMessage message = mailSender.createMimeMessage();
		// --> import org.springframework.mail.javamail.MimeMessageHelper;
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setSubject(subject);
		helper.setText(content, true);
		helper.setFrom(new InternetAddress(sender));
		helper.setTo(new InternetAddress(receiver));
		mailSender.send(message);

	}
}
