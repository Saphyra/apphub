package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment;

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
@Table(schema = "utils", name = "sql_generator_segment")
class SegmentEntity implements EncryptedEntity {
    @Id
    private String segmentId;
    private String userId;
    private String externalReference;
    private String segmentType;
    private String order;

    @Override
    public String getId() {
        return segmentId;
    }
}
