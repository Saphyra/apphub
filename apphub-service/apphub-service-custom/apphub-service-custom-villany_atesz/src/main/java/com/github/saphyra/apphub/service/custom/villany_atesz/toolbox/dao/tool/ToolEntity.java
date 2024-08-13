package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(schema = "villany_atesz", name = "tool")
public class ToolEntity {
    @Id
    private String toolId;
    private String userId;
    private String storageBoxId;
    private String toolTypeId;
    private String brand;
    private String name;
    private String cost;
    private String acquiredAt;
    private String warrantyExpiresAt;
    @Enumerated(EnumType.STRING)
    private ToolStatus status;
    private String scrappedAt;
}
