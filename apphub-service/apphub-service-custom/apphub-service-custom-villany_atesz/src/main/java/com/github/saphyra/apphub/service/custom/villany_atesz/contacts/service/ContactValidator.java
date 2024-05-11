package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ContactValidator {
    void validate(ContactModel model) {
        ValidationUtil.notBlank(model.getName(), "name");
        ValidationUtil.notNull(model.getCode(), "code");
        ValidationUtil.notNull(model.getPhone(), "phone");
        ValidationUtil.notNull(model.getAddress(), "address");
        ValidationUtil.notNull(model.getNote(), "note");
    }
}
