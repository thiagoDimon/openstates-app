package com.openstates.app.service.impl;

import java.time.Duration;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.openstates.app.dto.openstates.OpenStatesApiResponse;
import com.openstates.app.dto.openstates.OpenStatesErrorResponse;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.exception.OpenStatesApiException;
import com.openstates.app.exception.RateLimitException;
import com.openstates.app.service.OpenStatesApiService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
public class OpenStatesApiServiceImpl implements OpenStatesApiService {

    private static final int MAX_RETRIES = 1;
    private static final int MAX_PAGES = 2;
    private static final int PER_PAGE = 30;
    private static final Duration REQUEST_DELAY = Duration.ofSeconds(1);
    private static final Duration RETRY_DELAY = Duration.ofSeconds(60);

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
        return Flux.fromIterable(US_STATE_CODES)
                .concatMap(stateCode -> Mono.delay(REQUEST_DELAY)
                    .doOnNext(ignored -> log.info("Fetching politicians for state: {}", stateCode))
                    .flatMapMany(ignored -> fetchAllPagesForState(stateCode)
                            .onErrorResume(RateLimitException.class, e -> {
                                log.warn("Rate limit reached for state {}: {}", stateCode, e.getMessage());
                                return Flux.empty();
                            })
                            .onErrorResume(OpenStatesApiException.class, e -> {
                                log.warn("Skipping state {} due to API error: {}", stateCode, e.getMessage());
                                return Flux.empty();
                            })
                            .onErrorResume(e -> {
                                log.warn("Skipping state {} due to unexpected error: {}", stateCode, e.getMessage());
                                return Flux.empty();
                            })))
                .collectList()
                .doOnSuccess(list -> log.info("Total politicians fetched: {}", list.size()))
                .block();
    }

    private Flux<OpenStatesPersonResponse> fetchAllPagesForState(String stateCode) {
        return fetchPage(stateCode, 1)
                .flatMapMany(firstPage -> {
                    if (firstPage.results() == null || firstPage.results().isEmpty()) {
                        log.warn("No results returned for state: {}", stateCode);
                        return Flux.empty();
                    }
                    if (firstPage.pagination() == null) {
                        log.warn("No pagination info returned for state: {}", stateCode);
                        return Flux.fromIterable(firstPage.results());
                    }

                    int maxPage = firstPage.pagination().maxPage();
                    Flux<OpenStatesPersonResponse> firstResults = Flux.fromIterable(firstPage.results());

                    if (maxPage <= 1) {
                        return firstResults;
                    }

                    Flux<OpenStatesPersonResponse> remainingResults = Flux.range(2, Math.min(maxPage, MAX_PAGES) - 1)
                            .concatMap(page -> Mono.delay(REQUEST_DELAY)
                                    .then(fetchPage(stateCode, page))
                                    .flatMapMany(response -> {
                                        if (response.results() == null) {
                                            log.warn("Empty response for state: {}, page: {}", stateCode, page);
                                            return Flux.empty();
                                        }
                                        return Flux.fromIterable(response.results());
                                    }));

                    return firstResults.concatWith(remainingResults);
                })
                .retryWhen(Retry.fixedDelay(MAX_RETRIES, RETRY_DELAY)
                        .filter(RateLimitException.class::isInstance)
                        .doBeforeRetry(signal -> log.info("Retrying state after rate limit delay...")));
    }

    private Mono<OpenStatesApiResponse> fetchPage(String stateCode, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/people")
                        .queryParam("jurisdiction", stateCode)
                        .queryParam("page", page)
                        .queryParam("per_page", PER_PAGE)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 429, response ->
                        response.bodyToMono(OpenStatesErrorResponse.class)
                                .defaultIfEmpty(OpenStatesErrorResponse.unknown())
                                .flatMap(error -> Mono.error(new RateLimitException(
                                        "Rate limit for state " + stateCode + ": " + error.detail()))))
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(OpenStatesErrorResponse.class)
                                .defaultIfEmpty(OpenStatesErrorResponse.unknown())
                                .flatMap(error -> Mono.error(new OpenStatesApiException(
                                        "Client error " + response.statusCode().value() + " fetching state " + stateCode + ": " + error.detail()))))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(OpenStatesErrorResponse.class)
                                .defaultIfEmpty(OpenStatesErrorResponse.unknown())
                                .flatMap(error -> Mono.error(new OpenStatesApiException(
                                        "Server error " + response.statusCode().value() + " fetching state " + stateCode + ": " + error.detail()))))
                .bodyToMono(OpenStatesApiResponse.class)
                .onErrorMap(ex -> !(ex instanceof OpenStatesApiException),
                        ex -> new OpenStatesApiException(
                                "Network error fetching state " + stateCode + ", page " + page, ex));
    }
}
