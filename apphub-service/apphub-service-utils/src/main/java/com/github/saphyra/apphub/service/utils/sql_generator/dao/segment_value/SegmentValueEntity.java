package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment_value;

import com.github.saphyra.apphub.lib.encryption_dao.EncryptedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "utils", name = "sql_generator_segment_value")
class SegmentValueEntity implements EncryptedEntity {
    @Id
    private String segmentValueId;
    private String userId;
    private String externalReference;
    private String segmentValue;

    @Override
    public String getId() {
        return segmentValueId;
    }
}
