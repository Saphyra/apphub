package com.github.saphyra.apphub.service.training.service;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SampleRestController {
    private final SleepService sleepService;

    @GetMapping(Endpoints.TRAINING_SAMPLE_REST_ENDPOINT)
    public Map<String, String> getEndpoint(HttpServletRequest request) {
        sleepService.sleep(5000);
        return Arrays.stream(request.getQueryString().split("&"))
            .map(s -> s.split("="))
            .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
    }
}
