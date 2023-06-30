package io.pivotal.cfapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cfapp.domain.Space;
import io.pivotal.cfapp.service.SpaceService;
import io.pivotal.cfapp.service.SpaceService;
import io.pivotal.cfapp.service.TimeKeeperService;
import io.pivotal.cfapp.service.TkServiceUtil;
import reactor.core.publisher.Mono;


@RestController
public class SpaceController {

    private final SpaceService spaceService;
    private final TkServiceUtil util;

    @Autowired
    public SpaceController(
            SpaceService spaceService,
            TimeKeeperService tkService) {
        this.spaceService = spaceService;
        this.util = new TkServiceUtil(tkService);
    }

    @GetMapping("/snapshot/spaces")
    public Mono<ResponseEntity<List<Space>>> listAllSpaces() {
        return util.getHeaders()
                .flatMap(h -> spaceService
                        .findAll()
                        .collectList()
                        .map(orgs -> new ResponseEntity<>(orgs, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/snapshot/spaces/count")
    public Mono<ResponseEntity<Long>> spacesCount() {
        return util.getHeaders()
                .flatMap(h -> spaceService
                        .findAll()
                        .count()
                        .map(count -> new ResponseEntity<>(count, h, HttpStatus.OK)))
                .defaultIfEmpty(ResponseEntity.ok(0L));
    }


}
