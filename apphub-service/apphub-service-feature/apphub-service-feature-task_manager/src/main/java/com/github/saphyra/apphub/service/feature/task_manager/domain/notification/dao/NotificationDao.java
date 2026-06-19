package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
public class NotificationDao {
    private final List<Notification> repository = Collections.synchronizedList(new ArrayList<>());

    public void save(List<Notification> notifications) {
        repository.addAll(notifications);
    }
}
