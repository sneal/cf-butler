package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.task.ProductsAndReleasesTask;
import org.cftoolsuite.cfapp.task.TkTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@Profile("on-demand")
@RestController
public class OnDemandCollectorTriggerController {

    @Autowired
    private TkTask tkCollector;

    @Autowired(required = false)
    private ProductsAndReleasesTask productsAndReleasesCollector;

    @PostMapping("/collect")
    public Mono<ResponseEntity<Void>> triggerCollection() {
        tkCollector.collect();
        if (productsAndReleasesCollector != null) {
            productsAndReleasesCollector.collect();
        }
        return Mono.just(ResponseEntity.accepted().build());
    }

}
