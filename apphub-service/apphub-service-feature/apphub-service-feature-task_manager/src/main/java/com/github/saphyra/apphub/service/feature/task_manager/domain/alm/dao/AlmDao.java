package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class AlmDao {
    private final List<Alm> repository = Collections.synchronizedList(new ArrayList<>());

    public void save(Alm alm) {
        repository.add(alm);
    }

    public List<Alm> getByUserIdAndObjectType(UUID userId, ObjectType objectType) {
        return repository.stream()
            .filter(alm -> alm.getPrincipal().equals(userId))
            .filter(alm -> alm.getObjectType().equals(objectType))
            .toList();
    }
}
