package com.github.saphyra.apphub.service.utils.sql_generator.dao.schema_name;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class SchemaName {
    private final UUID schemaNameId;
    private final UUID userId;
    private final UUID externalReference;
    private final String schemaName;
}
