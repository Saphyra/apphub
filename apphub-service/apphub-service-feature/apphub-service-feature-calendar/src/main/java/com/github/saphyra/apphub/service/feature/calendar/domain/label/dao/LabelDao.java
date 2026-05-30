package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class LabelDao {
    public List<Label> getByLabelIds(UUID userId, List<UUID> labelIds) {
        return null;
    }

    public void save(UUID userId, Label label) {

    }

    public Label findByIdValidated(UUID userId, UUID labelId) {
        return null;
    }

    public List<Label> getByUserId(UUID userId) {
        return null;
    }
}
