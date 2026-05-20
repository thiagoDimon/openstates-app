package com.openstates.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openstates.app.dto.FilterOptionsDTO;
import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.service.PoliticianService;

@RestController
@RequestMapping("/politicians")
public class PoliticianController {

    private final PoliticianService politicianService;

    public PoliticianController(PoliticianService politicianService) {
        this.politicianService = politicianService;
    }

    @GetMapping
    public ResponseEntity<List<PoliticianDTO>> findAll(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String party) {
        return ResponseEntity.ok(politicianService.findAll(state, party));
    }

    @GetMapping("/filters")
    public ResponseEntity<FilterOptionsDTO> getFilterOptions() {
        return ResponseEntity.ok(politicianService.getFilterOptions());
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> sync() {
        politicianService.syncFromApi();
        return ResponseEntity.ok().build();
    }
}
