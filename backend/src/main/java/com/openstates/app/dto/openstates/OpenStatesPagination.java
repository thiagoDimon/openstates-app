package com.openstates.app.dto.openstates;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenStatesPagination(
        int page,
        @JsonProperty("max_page") int maxPage,
        @JsonProperty("per_page") int perPage,
        @JsonProperty("total_items") int totalItems
) {}
