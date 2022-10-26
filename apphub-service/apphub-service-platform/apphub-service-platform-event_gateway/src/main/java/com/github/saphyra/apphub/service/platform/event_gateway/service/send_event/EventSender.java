package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
class EventSender {
    private final EventProcessorDao eventProcessorDao;
    private final DateTimeUtil dateTimeUtil;
    private final RestTemplate restTemplate;
    private final UrlAssembler urlAssembler;
    private final ErrorReporterService errorReporterService;

    void sendEvent(EventProcessor processor, SendEventRequest<?> sendEventRequest, String locale) {
        try {
            Exception exception = null;
            for (int i = 1; i <= 3; i++) {
                try {
                    String url = urlAssembler.assemble(processor);
                    log.info("Url: {}", url);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(Constants.LOCALE_HEADER, locale);

                    HttpEntity<SendEventRequest<?>> entity = new HttpEntity<>(sendEventRequest, headers);
                    restTemplate.postForEntity(url, entity, Void.class);

                    processor.setLastAccess(dateTimeUtil.getCurrentDateTime());
                    eventProcessorDao.save(processor);
                    return;
                } catch (Exception e) {
                    exception = e;
                    log.warn("Failed sending event {} to processor {} for {} try.", sendEventRequest.getEventName(), processor, i, e);
                }
            }
            throw exception;
        } catch (Exception e) {
            log.warn("Failed sending event with name {} to processor {}", sendEventRequest.getEventName(), processor, e);
            errorReporterService.report("Failed sending event with name " + sendEventRequest.getEventName() + " to processor " + processor, e);
        }
    }
}
