package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "villany_atesz", name = "contact")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class ContactEntity {
    @Id
    private String contactId;
    private String userId;
    private String name;
    private String code;
    private String phone;
    private String address;
    private String note;
}
