package com.github.saphyra.apphub.lib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAccountEvent {
    public static final String EVENT_NAME = "delete-account-event";

    @NonNull
    private UUID userId;
}