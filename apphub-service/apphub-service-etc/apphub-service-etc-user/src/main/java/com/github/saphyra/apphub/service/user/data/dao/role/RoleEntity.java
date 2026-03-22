package com.github.saphyra.apphub.service.user.data.dao.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "apphub_user", name = "apphub_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoleEntity {
    @Id
    private String roleId;
    private String userId;

    @Column(name = "apphub_role")
    private String role;
}
