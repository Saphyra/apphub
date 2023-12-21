package com.github.saphyra.apphub.service.platform.main_gateway;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RootController {
    private final ErrorReporterService errorReporterService;

    @GetMapping("/api")
    @ResponseBody
    public void availabilityCheck() {
        log.info("Availability check was executed by a mobile device.");
    }


    @GetMapping("/")
    public String rootMapping() {
        log.info("Root was called. Redirecting to index page.");
        return String.format("redirect:%s", Endpoints.INDEX_PAGE);
    }

    @GetMapping("/report-error")
    public String reportError() {
        log.info("Error report endpoint was called.");
        errorReporterService.report("Test-error", new RuntimeException("Test exception"));
        return rootMapping();
    }
}
