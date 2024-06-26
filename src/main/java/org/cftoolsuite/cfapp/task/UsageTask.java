package org.cftoolsuite.cfapp.task;

import org.cftoolsuite.cfapp.event.TkRetrievedEvent;
import org.cftoolsuite.cfapp.service.UsageCache;
import org.cftoolsuite.cfapp.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UsageTask implements ApplicationListener<TkRetrievedEvent> {

    private final UsageService service;
    private final UsageCache cache;
    private final ObjectMapper mapper;

    @Autowired
    public UsageTask(
            UsageService service,
            UsageCache cache,
            ObjectMapper mapper) {
        this.service = service;
        this.cache = cache;
        this.mapper = mapper;
    }

    public void collect() {
        log.info("UsageTask started");
        service
            .getApplicationReport()
            .doOnNext(r -> {
                log.trace(mapWithException("AppUsageReport", r));
                cache.setApplicationReport(r);
            })
            .then(service.getServiceReport())
            .doOnNext(r -> {
                log.trace(mapWithException("ServiceUsageReport", r));
                cache.setServiceReport(r);
            })
            .then(service.getTaskReport())
            .doOnNext(r -> {
                log.trace(mapWithException("TaskUsageReport", r));
                cache.setTaskReport(r);
            })
            .subscribe(
                result -> log.info("UsageTask completed"),
                error -> log.error("UsageTask terminated with error", error)
            );
    }

    private String mapWithException(String type, Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException("Problem mapping " + type);
        }
    }

    @Override
    public void onApplicationEvent(TkRetrievedEvent event) {
        collect();
    }

}
