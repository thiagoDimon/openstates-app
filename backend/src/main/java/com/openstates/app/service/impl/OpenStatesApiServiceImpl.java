package com.openstates.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.openstates.app.dto.openstates.OpenStatesApiResponse;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.exception.OpenStatesApiException;
import com.openstates.app.service.OpenStatesApiService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OpenStatesApiServiceImpl implements OpenStatesApiService {

    private static final int PER_PAGE = 100;

    private static final List<String> US_STATE_CODES = List.of(
            "al", "ak", "az", "ar", "ca", "co", "ct", "de", "fl", "ga",
            "hi", "id", "il", "in", "ia", "ks", "ky", "la", "me", "md",
            "ma", "mi", "mn", "ms", "mo", "mt", "ne", "nv", "nh", "nj",
            "nm", "ny", "nc", "nd", "oh", "ok", "or", "pa", "ri", "sc",
            "sd", "tn", "tx", "ut", "vt", "va", "wa", "wv", "wi", "wy", "dc"
    );

    private final WebClient webClient;

    public OpenStatesApiServiceImpl(WebClient openStatesWebClient) {
        this.webClient = openStatesWebClient;
    }

    @Override
    public List<OpenStatesPersonResponse> fetchAllPoliticians() {
        List<OpenStatesPersonResponse> allPoliticians = new ArrayList<>();

        for (String stateCode : US_STATE_CODES) {
            try {
                log.info("Fetching politicians for state: {}", stateCode);
                allPoliticians.addAll(fetchAllPagesForState(stateCode));
            } catch (OpenStatesApiException e) {
                log.warn("Skipping state {} due to API error: {}", stateCode, e.getMessage());
            }
        }

        log.info("Total politicians fetched: {}", allPoliticians.size());
        return allPoliticians;
    }

    private List<OpenStatesPersonResponse> fetchAllPagesForState(String stateCode) {
        List<OpenStatesPersonResponse> results = new ArrayList<>();

        OpenStatesApiResponse firstPage = fetchPage(stateCode, 1);
        if (firstPage == null || firstPage.results() == null) {
            log.warn("No results returned for state: {}", stateCode);
            return results;
        }

        if (firstPage.pagination() == null) {
            log.warn("No pagination info returned for state: {}", stateCode);
            return results;
        }

        results.addAll(firstPage.results());

        int maxPage = firstPage.pagination().maxPage();
        for (int page = 2; page <= maxPage; page++) {
            OpenStatesApiResponse response = fetchPage(stateCode, page);
            if (response != null && response.results() != null) {
                results.addAll(response.results());
            } else {
                log.warn("Empty response for state: {}, page: {}", stateCode, page);
            }
        }

        return results;
    }

    private OpenStatesApiResponse fetchPage(String stateCode, int page) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/people")
                            .queryParam("jurisdiction", stateCode)
                            .queryParam("page", page)
                            .queryParam("per_page", PER_PAGE)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new OpenStatesApiException(
                                    "Client error " + response.statusCode().value() + " fetching state: " + stateCode)))
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new OpenStatesApiException(
                                    "Server error " + response.statusCode().value() + " fetching state: " + stateCode)))
                    .bodyToMono(OpenStatesApiResponse.class)
                    .block();
        } catch (OpenStatesApiException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenStatesApiException(
                    "Network error fetching state " + stateCode + ", page " + page, e);
        }
    }
}
