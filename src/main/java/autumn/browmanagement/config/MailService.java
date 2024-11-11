package autumn.browmanagement.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) throws MessagingException{

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        // 이메일 설정
        helper.setTo(to); // 수신자
        helper.setSubject(subject); // 메일 제목
        helper.setText(text, true); // 내용과 HTML 여부

        // 메일 발송
        mailSender.send(message);

    }
}
