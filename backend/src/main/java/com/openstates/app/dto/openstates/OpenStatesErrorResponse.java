package com.openstates.app.dto.openstates;

public record OpenStatesErrorResponse(String detail) {

    public static OpenStatesErrorResponse unknown() {
        return new OpenStatesErrorResponse("No details provided");
    }
}
