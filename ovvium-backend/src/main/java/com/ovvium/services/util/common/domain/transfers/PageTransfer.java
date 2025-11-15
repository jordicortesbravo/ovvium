package com.ovvium.services.util.common.domain.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PageTransfer implements Serializable {

    private static final long serialVersionUID = -7769653093217024192L;

    @XmlElement(name = "element")
    @XmlElementWrapper(name = "content")
    private List<?> content;

    private int pageNumber;
    private int pageSize;
    private long totalElements;
}
