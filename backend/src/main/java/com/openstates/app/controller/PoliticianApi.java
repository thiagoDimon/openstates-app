package com.openstates.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.openstates.app.dto.ErrorResponseDTO;
import com.openstates.app.dto.PoliticianPageDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Politicians", description = "Endpoints for querying and synchronizing US politician data")
public interface PoliticianApi {

    @Operation(summary = "List politicians", description = "Returns a paginated list of politicians filtered by state and optionally by party")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "429", description = "OpenStates API rate limit reached", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "502", description = "OpenStates API unavailable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PoliticianPageDTO> findAll(
            @Parameter(description = "State code (e.g. ca, tx, ny)") @RequestParam(required = false) String state,
            @Parameter(description = "Party name (e.g. Democratic, Republican)") @RequestParam(required = false) String party,
            @Parameter(description = "Page number, zero-based") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Sync state data", description = "Fetches the next page of politician data from the OpenStates API for the given state and persists it")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sync triggered successfully"),
            @ApiResponse(responseCode = "429", description = "OpenStates API rate limit reached", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "502", description = "OpenStates API unavailable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> syncState(@Parameter(description = "State code to sync (e.g. ca, tx, ny)") @PathVariable String stateCode);
}
