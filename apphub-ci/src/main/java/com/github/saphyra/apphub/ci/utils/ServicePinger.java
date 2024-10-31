package com.github.saphyra.apphub.ci.utils;

import com.github.saphyra.apphub.ci.value.Constants;
import lombok.SneakyThrows;
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
    private static final RestTemplate REST_TEMPLATE = restTemplate();

    @SneakyThrows
    public static RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public Optional<Exception> pingLocal(int port, String protocol) {
        return pingService(60, () -> singlePingLocal(port, protocol));
    }

    public Optional<Exception> singlePingLocal(int port) {
        return singlePingLocal(port, Constants.PROTOCOL_UNSECURE);
    }

    public Optional<Exception> singlePingLocal(int port, String protocol) {
        String url = "%s://localhost:%s/platform/health".formatted(protocol, port);
        return singlePing(url);
    }

    public Optional<Exception> pingRemote(int port, int timeoutSeconds) {
        String url = "http://localhost:%s/web".formatted(port);
        log.info("Pinging {} for {} seconds...", url, timeoutSeconds);
        return pingService(timeoutSeconds, () -> singlePing(url));
    }

    public Optional<Exception> pingService(int timeoutSeconds, Supplier<Optional<Exception>> apiCall) {
        Exception cause = null;
        for (int i = 0; i < timeoutSeconds * 10; i++) {
            Optional<Exception> maybeException = apiCall.get();
            if (maybeException.isPresent()) {
                cause = maybeException.get();
            } else {
                return Optional.empty();
            }

            try {
                Thread.sleep(100);
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

    public Optional<Exception> singlePing(String url) {
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
