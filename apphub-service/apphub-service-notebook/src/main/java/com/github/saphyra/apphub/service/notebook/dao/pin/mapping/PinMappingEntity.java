package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "notebook", name = "pin_mapping")
class PinMappingEntity {
    @Id
    private String pinMappingId;
    private String userId;
    private String pinGroupId;
    private String listItemId;
}
