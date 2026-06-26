package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LinkColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;

    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.LINK == type;
    }

    @Override
    public void validateData(Object data) {
        Link link = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, Link.class), "link");
        ValidationUtil.notBlank(link.getLabel(), "link.label");
        ValidationUtil.maxLength(link.getLabel(), NotebookConstants.MAX_CONTENT_LENGTH, "link.label");
        ValidationUtil.notNull(link.getUrl(), "link.url");
        ValidationUtil.maxLength(link.getUrl(), NotebookConstants.MAX_CONTENT_LENGTH, "link.url");
    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        return Optional.of(new BiWrapper<>(objectMapper.writeValueAsString(data), Optional.empty()));
    }

    @Override
    public Object deserialize(String data) {
        return objectMapper.readValue(data, Link.class);
    }
}
