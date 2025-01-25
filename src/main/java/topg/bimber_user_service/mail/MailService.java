package topg.bimber_user_service.mail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import topg.bimber_user_service.exceptions.MailNotSentException;
import topg.bimber_user_service.models.NotificationEmail;

@Slf4j
@Service
@AllArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private  final MailContentBuilder mailContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("Temitope@gmail.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try{
            javaMailSender.send(mimeMessagePreparator);
            log.info("Activation email sent");

        }catch (MailException mailException){
            throw new MailNotSentException("Exception occurred when sending mail to " + notificationEmail.getRecipient());

        }
    }

}
