package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CheckedItemFactory {
    public CheckedItem create(UUID userId, UUID checkedItemId, Boolean checked) {
        return CheckedItem.builder()
            .checkedItemId(checkedItemId)
            .userId(userId)
            .checked(checked)
            .build();
    }
}
