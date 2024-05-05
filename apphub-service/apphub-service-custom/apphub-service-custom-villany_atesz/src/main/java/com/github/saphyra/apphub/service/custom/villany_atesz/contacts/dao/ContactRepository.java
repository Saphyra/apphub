package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ContactRepository extends CrudRepository<ContactEntity, String> {
    void deleteByUserId(String userId);

    List<ContactEntity> getByUserId(String userId);

    void deleteByUserIdAndContactId(String userId, String contactId);
}
