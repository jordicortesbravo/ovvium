package com.ovvium.services.util.mail;

import com.ovvium.services.util.util.container.KeyValue;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Mail {

    private String subject;
    private String html;
    private String text;
    private String from;
    private String personalName;

    private List<KeyValue<String, String>> headers = new ArrayList<>();
    private List<KeyValue<RecipientKind, String>> recipients = new ArrayList<>();
    private List<MailResource> resources = new ArrayList<>();
    private List<CalendarEvent> calendars = new ArrayList<>();

    public Mail addAttachment(String name, byte[] content) {
        resources.add(new MailResource(MailResourceType.ATTACHMENT, name, content));
        return this;
    }

    public Mail addInline(String name, byte[] content) {
        resources.add(new MailResource(MailResourceType.INLINE, name, content));
        return this;
    }

    public Mail addHeader(String name, String value) {
        headers.add(KeyValue.of(name, value));
        return this;
    }

    public Mail addRecipients(RecipientKind kind, String... recipients) {
        for (String recipient : recipients) {
            this.recipients.add(KeyValue.of(kind, recipient));
        }
        return this;
    }

    public Mail addRecipients(String... recipients) {
        return addRecipients(RecipientKind.TO, recipients);
    }

    public CalendarEvent addEvent(String summary) {
        val cal = new CalendarEvent().setSummary(summary);
        calendars.add(cal);
        return cal;
    }

    public boolean isMultipart() {
        return resources.size() > 0 || calendars.size() > 0;
    }

}
