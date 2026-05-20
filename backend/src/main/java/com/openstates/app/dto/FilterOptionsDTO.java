package com.openstates.app.dto;

import java.util.List;

public record FilterOptionsDTO(
        List<String> states,
        List<String> parties
) {}
