package com.ovvium.services.util.mail;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ovvium.services.util.util.basic.Traverser;

@Data
@Accessors(chain = true)
public class CalendarEvent {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
    
    public enum Method {
        PUBLISH, REQUEST, ADD, CANCEL,
        // REPLY, REFRESH, COUNTER, DECLINECOUNTER // Són methods vàlids, però referents a la comunicació entre assistents i organitzador
    }

    private Method method = Method.REQUEST;
    private int sequence = 0;

    private Date start;
    private Date end;

    private Long id;
    private String organizer;
    private String organizerName;
    private String summary;
    private String description;
    private String location;
    private List<String> receivers;

    @Override
    public String toString() {
        String s = "";
        s += "BEGIN:VCALENDAR\n";
        s += "PRODID:SEGES\n";
        s += "VERSION:2.0\n";
        s += "CALSCALE:GREGORIAN\n";
        s += "METHOD:" + method + "\n";
        s += "BEGIN:VEVENT\n";
        s += "DTSTART:" + DATE_FORMAT.format(start) + "\n";
        s += "DTEND:" + DATE_FORMAT.format(end) + "\n";
        s += "DTSTAMP:" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "\n";
        s += "ORGANIZER;CN=" + organizerName + ":mailto:" + organizer + "\n";
        if (id != null) {
            s += "UID:" + id + "\n";
        }
        s += "ATTENDEE;ROLE=CHAIR;PARTSTAT=ACCEPTED;CN=" + (organizerName != null ? organizerName : organizer) + ":mailto:" + organizer
                + "\n";
        for (val receiver : Traverser.of(receivers)) {
            s += "ATTENDEE;RSVP=TRUE;CUTYPE=INDIVIDUAL;CN=" + receiver + ":mailto:" + receiver + "\n";
        }
        if (location != null) {
            s += "LOCATION:" + location + "\n";
        }
        
        s += "DESCRIPTION:" + (description == null ? "" : description) + "\n";
        s += "SEQUENCE:" + sequence + "\n";
        s += "STATUS:CONFIRMED\n";
        if (summary != null) {
            s += "SUMMARY:" + summary + "\n";
        }
        s += "TRANSP:OPAQUE\n";
        s += "END:VEVENT\n";
        s += "END:VCALENDAR\n";

        return s;
    }
}
