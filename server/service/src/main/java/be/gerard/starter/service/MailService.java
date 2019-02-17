package be.gerard.starter.service;

import be.gerard.starter.event.RegistrationAdded;
import be.gerard.starter.model.ContactOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * MailService
 * <p>
 * based on https://www.baeldung.com/spring-email --> Thx!
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMessage(
            final String to,
            final String subject,
            final String text,
            final String attachmentUri
    ) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        /*
        final MimeMessage message = mailSender.createMimeMessage();

        final MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (Objects.nonNull(attachmentUri)) {
                final FileSystemResource file = new FileSystemResource(new File(attachmentUri));
                helper.addAttachment("Invoice", file);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("mail.failed", e);
        }
        */
    }

    @EventListener
    public void handle(
            final RegistrationAdded event
    ) {
        final String[] tos = event.getRegistration()
                .getContactOptions()
                .stream()
                .filter(method -> method.getContactMethod() == ContactOption.Method.EMAIL)
                .map(ContactOption::getEmail)
                .toArray(String[]::new);

        if (tos.length == 0) {
            return;
        }

        final String subject = "Trouwfeest Kim Bassens & Bart Gerard";

        final StringBuilder message = new StringBuilder("<h3>Registratie voltooid!</h3>");
        message.append("<p>Op 18 mei 2019 hopen wij onderstaande gasten te kunnen verwelkomen:</p>");
        message.append("<ul>");

        event.getRegistration()
                .getGuests()
                .forEach(guest -> message.append(String.format(
                        "<li><b>%s %s</b></li>",
                        guest.getFirstName(),
                        guest.getLastName()
                )));

        message.append("</ul>");
        message.append("<p>Gelieve (tijdig) te laten weten indien u toch niet aanwezig zal zijn door op deze mail te antwoorden.</p>");
        message.append("<p>Met vriendelijke groeten,</p>");
        message.append("<p>Kim Bassens & Bart Gerard</p>");

        final MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            mimeMessage.setContent(message.toString(), MediaType.TEXT_HTML_VALUE);

            helper.setTo(tos);
            helper.setSubject(subject);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("mail.failed", e);
        }
    }

}
