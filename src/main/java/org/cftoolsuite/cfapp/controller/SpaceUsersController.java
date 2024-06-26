package org.cftoolsuite.cfapp.controller;

import java.util.List;

import org.cftoolsuite.cfapp.domain.SpaceUsers;
import org.cftoolsuite.cfapp.service.SpaceUsersService;
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
public class SpaceUsersController {

    private final SpaceUsersService service;
    private final TkServiceUtil util;

    @Autowired
    public SpaceUsersController(
            SpaceUsersService service,
            TimeKeeperService tkService) {
        this.service = service;
        this.util = new TkServiceUtil(tkService);
    }

    @GetMapping("/snapshot/users")
    public Mono<ResponseEntity<List<String>>> allAccountNames() {
        return util.getHeaders()
                .flatMap(h -> service.obtainAccountNames()
                        .collectList()
                        .map(names -> new ResponseEntity<>(names, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = { "/snapshot/spaces/users" })
    public Mono<ResponseEntity<List<SpaceUsers>>> getAllSpaceUsers() {
        return util.getHeaders()
                .flatMap(h -> service
                        .findAll()
                        .collectList()
                        .map(users -> new ResponseEntity<>(users, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/snapshot/{organization}/{space}/users")
    public Mono<ResponseEntity<SpaceUsers>> getUsersInOrganizationAndSpace(
            @PathVariable("organization") String organization,
            @PathVariable("space") String space) {
        return util.getHeaders()
                .flatMap(h -> service
                        .findByOrganizationAndSpace(organization, space)
                        .map(users -> new ResponseEntity<>(users, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/snapshot/users/count")
    public Mono<ResponseEntity<Long>> totalAccounts() {
        return util.getHeaders()
                .flatMap(h -> service
                        .obtainAccountNames()
                        .count()
                        .map(c -> new ResponseEntity<>(c, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.ok(0L));
    }

}
