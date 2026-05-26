package com.openstates.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openstates.app.dto.PoliticianPageDTO;
import com.openstates.app.service.PoliticianService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/politicians")
@RequiredArgsConstructor
public class PoliticianController implements PoliticianApi {

    @NonNull private final PoliticianService politicianService;

    @Override
    @GetMapping
    public ResponseEntity<PoliticianPageDTO> findAll(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String party,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(politicianService.findAll(state, party, page, size));
    }

    @Override
    @PostMapping("/sync/{stateCode}")
    public ResponseEntity<Void> syncState(@PathVariable String stateCode) {
        politicianService.syncNextPageForState(stateCode);
        return ResponseEntity.ok().build();
    }
}
