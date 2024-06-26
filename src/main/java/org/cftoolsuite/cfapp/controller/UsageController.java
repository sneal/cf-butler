package org.cftoolsuite.cfapp.controller;

import java.time.LocalDate;

import org.cftoolsuite.cfapp.domain.accounting.application.AppUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.service.ServiceUsageReport;
import org.cftoolsuite.cfapp.domain.accounting.task.TaskUsageReport;
import org.cftoolsuite.cfapp.service.UsageCache;
import org.cftoolsuite.cfapp.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class UsageController {

    private final UsageCache cache;
    private final UsageService service;

    @Autowired
    public UsageController(
            UsageCache cache,
            UsageService service) {
        this.cache = cache;
        this.service = service;
    }

    @GetMapping(value = "/accounting/applications/{orgName}/{startDate}/{endDate}")
    public Mono<ResponseEntity<String>> getOrganizationApplicationUsageReport(
            @PathVariable("orgName") String organizationName,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("startDate") LocalDate startDate,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("endDate") LocalDate endDate
            ) {
        return service.getApplicationUsage(organizationName, startDate, endDate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/services/{orgName}/{startDate}/{endDate}")
    public Mono<ResponseEntity<String>> getOrganizationServiceUsageReport(
            @PathVariable("orgName") String organizationName,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("startDate") LocalDate startDate,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("endDate") LocalDate endDate
            ) {
        return service.getServiceUsage(organizationName, startDate, endDate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/tasks/{orgName}/{startDate}/{endDate}")
    public Mono<ResponseEntity<String>> getOrganizationTaskUsageReport(
            @PathVariable("orgName") String organizationName,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("startDate") LocalDate startDate,
            @DateTimeFormat(iso = ISO.DATE) @PathVariable("endDate") LocalDate endDate
            ) {
        return service.getTaskUsage(organizationName, startDate, endDate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/applications")
    public Mono<ResponseEntity<AppUsageReport>> getSystemWideApplicationUsageReport() {
        return Mono.justOrEmpty(cache.getApplicationReport())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/services")
    public Mono<ResponseEntity<ServiceUsageReport>> getSystemWideServiceUsageReport() {
        return Mono.justOrEmpty(cache.getServiceReport())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/accounting/tasks")
    public Mono<ResponseEntity<TaskUsageReport>> getSystemWideTaskUsageReport() {
        return Mono.justOrEmpty(cache.getTaskReport())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
