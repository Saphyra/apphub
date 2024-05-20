package com.github.saphyra.apphub.ci.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class ServicePinger {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public Optional<Exception> pingLocal(int port) {
        return pingService(60, () -> singlePingLocal(port));
    }

    public Optional<Exception> singlePingLocal(int port) {
        return singlePing("http://localhost:%s/platform/health".formatted(port));
    }

    public Optional<Exception> pingRemote(int port, int timeoutSeconds) {
        String url = "http://localhost:%s/web".formatted(port);
        log.info("Pinging {} for {} seconds...", url, timeoutSeconds);
        return pingService(timeoutSeconds, () -> singlePing(url));
    }

    public Optional<Exception> pingService(int timeoutSeconds, Supplier<Optional<Exception>> apiCall) {
        Exception cause = null;
        for (int i = 0; i < timeoutSeconds; i++) {
            Optional<Exception> maybeException = apiCall.get();
            if (maybeException.isPresent()) {
                cause = maybeException.get();
            } else {
                return Optional.empty();
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

    private Optional<Exception> singlePing(String url) {
        try {
            if (REST_TEMPLATE.getForEntity(url, Void.class).getStatusCode() == HttpStatus.OK) {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.of(e);
        }

        return Optional.of(new IllegalStateException("Ping failed without exception."));
    }
}
