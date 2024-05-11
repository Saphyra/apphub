package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Contact {
    private final UUID contactId;
    private final UUID userId;
    private String name;
    private String code;
    private String phone;
    private String address;
    private String note;
}
