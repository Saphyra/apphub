package com.github.saphyra.apphub.ci.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class ServicePinger {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public Optional<Exception> pingService(int port) {
        Exception cause = null;
        for (int i = 0; i < 60; i++) {
            try {
                if (REST_TEMPLATE.getForEntity("http://localhost:%s/platform/health".formatted(port), Void.class).getStatusCode() == HttpStatus.OK) {
                    return Optional.empty();
                }
            } catch (Exception e) {
                cause = e;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                cause = e;
                break;
            }
        }

        if (isNull(cause)) {
            return Optional.of(new IllegalStateException("Waiting timed out without an exception."));
        }

        return Optional.of(cause);
    }
}
