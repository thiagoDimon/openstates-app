package com.openstates.app.dto;

import java.util.List;

public record PoliticianPageDTO(
        List<PoliticianDTO> content,
        int page,
        int size,
        boolean hasNext
) {}
