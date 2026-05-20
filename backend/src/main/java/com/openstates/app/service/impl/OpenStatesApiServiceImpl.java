package com.openstates.app.service.impl;

import com.openstates.app.dto.openstates.OpenStatesApiResponse;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.service.OpenStatesApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

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
            log.info("Fetching politicians for state: {}", stateCode);
            allPoliticians.addAll(fetchAllPagesForState(stateCode));
        }

        log.info("Total politicians fetched: {}", allPoliticians.size());
        return allPoliticians;
    }

    private List<OpenStatesPersonResponse> fetchAllPagesForState(String stateCode) {
        List<OpenStatesPersonResponse> results = new ArrayList<>();

        OpenStatesApiResponse firstPage = fetchPage(stateCode, 1);
        if (firstPage == null || firstPage.results() == null) {
            return results;
        }

        results.addAll(firstPage.results());

        int maxPage = firstPage.pagination().maxPage();
        for (int page = 2; page <= maxPage; page++) {
            OpenStatesApiResponse response = fetchPage(stateCode, page);
            if (response != null && response.results() != null) {
                results.addAll(response.results());
            }
        }

        return results;
    }

    private OpenStatesApiResponse fetchPage(String stateCode, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/people")
                        .queryParam("jurisdiction", stateCode)
                        .queryParam("page", page)
                        .queryParam("per_page", PER_PAGE)
                        .build())
                .retrieve()
                .bodyToMono(OpenStatesApiResponse.class)
                .block();
    }
}
