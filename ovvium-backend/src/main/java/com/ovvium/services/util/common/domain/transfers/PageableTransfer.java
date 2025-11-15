package com.ovvium.services.util.common.domain.transfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import com.ovvium.services.util.common.domain.Sort;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableTransfer implements Serializable{

    private static final long serialVersionUID = -3011864542134780163L;
    
    private int pageNumber;
    private int pageSize;
    private Sort sort;
}
