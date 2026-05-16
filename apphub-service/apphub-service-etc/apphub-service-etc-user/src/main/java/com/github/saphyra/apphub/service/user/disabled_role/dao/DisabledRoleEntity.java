package com.github.saphyra.apphub.service.user.disabled_role.dao;

import com.github.saphyra.apphub.lib.common_domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "apphub_user", name = "disabled_role")
public class DisabledRoleEntity {
    @Id
    @Column(name = "disabled_role")
    @Enumerated(EnumType.STRING)
    private Role role;
}
