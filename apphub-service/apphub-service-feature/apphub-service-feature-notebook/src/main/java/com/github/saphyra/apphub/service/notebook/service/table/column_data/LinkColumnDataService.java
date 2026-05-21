package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LinkColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;

    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.LINK == type;
    }

    @Override
    public Optional<String> serialize(Object data) {
        return Optional.of(objectMapper.writeValueAsString(data));
    }

    @Override
    public Object deserialize(String data) {
        return objectMapper.readValue(data, Link.class);
    }

    @Override
    public <T> T deserialize(String data, Class<T> clazz) {
        if (clazz.equals(Link.class)) {
            return (T) deserialize(data);
        }

        throw new UnsupportedOperationException("Link cannot be deserialized to " + clazz.getName());
    }

    @Override
    public void validateData(Object data) {
        Link link = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, Link.class), "link");
        ValidationUtil.notBlank(link.getLabel(), "link.label");
        ValidationUtil.notNull(link.getUrl(), "link.url");
    }
}
