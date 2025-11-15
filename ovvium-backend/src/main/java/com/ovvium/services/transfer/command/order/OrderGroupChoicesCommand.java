package com.ovvium.services.transfer.command.order;

import java.util.Optional;
import java.util.UUID;

public record OrderGroupChoicesCommand(
        UUID productId,
        String notes
) {

    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }
}
