package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

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
@Table(schema = "villany_atesz", name = "tool_type")
class ToolTypeEntity {
    @Id
    private String toolTypeId;
    private String userId;
    private String name;
}
