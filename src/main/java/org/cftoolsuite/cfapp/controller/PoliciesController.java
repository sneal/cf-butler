package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.domain.Policies;
import org.cftoolsuite.cfapp.service.PoliciesService;
import org.cftoolsuite.cfapp.task.PoliciesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class PoliciesController {

    private final PoliciesService policiesService;
    private final PoliciesLoader policiesLoader;

    public PoliciesController(
            PoliciesService policiesService,
            @Autowired(required = false) PoliciesLoader policiesLoader
            ) {
        this.policiesService = policiesService;
        this.policiesLoader = policiesLoader;
    }

    @GetMapping(value = { "/policies" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> listAllPolicies() {
        return policiesService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/application/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainApplicationPolicy(@PathVariable String id) {
        return policiesService.findApplicationPolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/endpoint/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainEndpointPolicy(@PathVariable String id) {
        return policiesService.findEndpointPolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/hygiene/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainHygienePolicy(@PathVariable String id) {
        return policiesService.findHygienePolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/legacy/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainLegacyPolicy(@PathVariable String id) {
        return policiesService.findLegacyPolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/query/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainQueryPolicy(@PathVariable String id) {
        return policiesService.findQueryPolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/policies/serviceInstance/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Policies>> obtainServiceInstancePolicy(@PathVariable String id) {
        return policiesService.findServiceInstancePolicyById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("policies/refresh")
    public Mono<ResponseEntity<Void>> refreshPolicies() {
        if (policiesLoader != null) {
            policiesLoader.load();
            return Mono.just(ResponseEntity.accepted().build());
        } else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

}
