package com.openstates.app.dto.openstates;

import java.util.List;

public record OpenStatesApiResponse(
        List<OpenStatesPersonResponse> results,
        OpenStatesPagination pagination
) {}
