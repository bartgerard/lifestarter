package be.gerard.lifestarter.registration.adapter

import be.gerard.lifestarter.registration.domain.Registration
import be.gerard.lifestarter.registration.domain.RegistrationAdded
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.event.EventListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

/** Wording of the confirmation e-mail; kept out of the code so it survives a change of plans. */
@ConfigurationProperties(prefix = "lifestarter.mail")
data class MailProperties(
    val enabled: Boolean = true,
    val subject: String = "Trouwfeest Kim Bassens & Bart Gerard",
    val signature: String = "Kim Bassens & Bart Gerard",
    val introduction: String = "Op 18 mei 2019 hopen wij onderstaande gasten te kunnen verwelkomen:",
)

/**
 * Sends the confirmation e-mail once a registration was accepted.
 *
 * [mailSender] is optional: without `spring.mail.host` configured Spring Boot creates no sender, in
 * which case notifications are simply skipped rather than breaking the RSVP flow.
 */
@Component
class MailRegistrationNotifier(
    private val properties: MailProperties,
    private val mailSender: JavaMailSender? = null,
) {

    @EventListener
    fun on(event: RegistrationAdded) {
        if (!properties.enabled) return

        val recipients = event.registration.mailboxes()
        if (recipients.isEmpty()) return

        val sender = mailSender
        if (sender == null) {
            log.warn("No mail sender configured; skipping confirmation for {} recipient(s)", recipients.size)
            return
        }

        runCatching {
            val message = sender.createMimeMessage()

            MimeMessageHelper(message, false, Charsets.UTF_8.name()).apply {
                setTo(recipients.toTypedArray())
                setSubject(properties.subject)
                setText(bodyFor(event.registration), true)
            }

            sender.send(message)
        }.onFailure { failure ->
            // Never let a mail problem fail the registration: it is already stored.
            log.error("Failed to send registration confirmation to {} recipient(s)", recipients.size, failure)
        }
    }

    private fun bodyFor(registration: Registration): String = buildString {
        append("<h3>Registratie voltooid!</h3>")
        append("<p>").append(properties.introduction).append("</p>")
        append("<ul>")
        registration.guests.forEach { guest -> append("<li><b>").append(guest.name.fullName).append("</b></li>") }
        append("</ul>")
        append("<p>Gelieve (tijdig) te laten weten indien u toch niet aanwezig zal zijn door op deze mail te antwoorden.</p>")
        append("<p>Met vriendelijke groeten,</p>")
        append("<p>").append(properties.signature).append("</p>")
    }

    private companion object {
        val log = LoggerFactory.getLogger(MailRegistrationNotifier::class.java)
    }
}
