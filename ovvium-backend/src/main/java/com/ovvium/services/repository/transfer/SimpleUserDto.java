package com.ovvium.services.repository.transfer;

import lombok.Data;

import java.util.UUID;

@Data
public class SimpleUserDto {

    private final UUID id;
    private final String email;
}
