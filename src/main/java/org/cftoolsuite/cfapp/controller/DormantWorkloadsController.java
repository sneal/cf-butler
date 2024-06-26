package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.domain.Workloads;
import org.cftoolsuite.cfapp.domain.Workloads.WorkloadsBuilder;
import org.cftoolsuite.cfapp.service.DormantWorkloadsService;
import org.cftoolsuite.cfapp.service.TimeKeeperService;
import org.cftoolsuite.cfapp.service.TkServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class DormantWorkloadsController {

    private final DormantWorkloadsService service;
    private final TkServiceUtil util;

    @Autowired
    public DormantWorkloadsController(
            DormantWorkloadsService service,
            TimeKeeperService tkService) {
        this.service = service;
        this.util = new TkServiceUtil(tkService);
    }

    @GetMapping(value = { "/snapshot/detail/dormant/{daysSinceLastUpdate}" } )
    public Mono<ResponseEntity<Workloads>> getDormantWorkloads(@PathVariable("daysSinceLastUpdate") Integer daysSinceLastUpdate) {
        final WorkloadsBuilder builder = Workloads.builder();
        return service
                .getDormantApplications(daysSinceLastUpdate)
                .map(list -> builder.applications(list))
                .then(service.getDormantServiceInstances(daysSinceLastUpdate))
                .map(list -> builder.serviceInstances(list))
                .flatMap(dwb -> util
                        .getHeaders()
                        .map(h -> new ResponseEntity<Workloads>(dwb.build(), h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
