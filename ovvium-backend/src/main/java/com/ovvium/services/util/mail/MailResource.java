package com.ovvium.services.util.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailResource {

    private MailResourceType type;
    private String name;
    private byte[] content;

}
