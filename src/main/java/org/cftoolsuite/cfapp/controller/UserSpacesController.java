package org.cftoolsuite.cfapp.controller;

import org.cftoolsuite.cfapp.domain.UserSpaces;
import org.cftoolsuite.cfapp.service.TimeKeeperService;
import org.cftoolsuite.cfapp.service.TkServiceUtil;
import org.cftoolsuite.cfapp.service.UserSpacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class UserSpacesController {

    private final UserSpacesService service;
    private final TkServiceUtil util;

    @Autowired
    public UserSpacesController(
            UserSpacesService service,
            TimeKeeperService tkService) {
        this.service = service;
        this.util = new TkServiceUtil(tkService);
    }

    @GetMapping(value = { "/snapshot/spaces/users/{name}" })
    public Mono<ResponseEntity<UserSpaces>> getSpacesForAccountName(@PathVariable("name") String name) {
        return util.getHeaders()
                .flatMap(h -> service
                        .getUserSpaces(name)
                        .map(userSpaces -> new ResponseEntity<>(userSpaces, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
