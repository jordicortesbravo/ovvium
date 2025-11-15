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


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class SortTransfer implements Serializable{
    
    private static final long serialVersionUID = -4742422086949083728L;
    
    @XmlElement(name="order")
    @XmlElementWrapper(name="orders")
    private List<OrderTransfer> orders;
}
