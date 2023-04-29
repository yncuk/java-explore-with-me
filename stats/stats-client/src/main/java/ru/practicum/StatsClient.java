package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class StatsClient {

    private final String application;
    private final String serverUrl;
    private final ObjectMapper json;
    private final HttpClient httpClient;


    public StatsClient(@Value("ewm-main-service") String application,
                       @Value("${stats-server.url}") String serverUrl,
                       ObjectMapper json) {
        this.application = application;
        this.serverUrl = serverUrl;
        this.json = json;
        this.httpClient = HttpClient.newBuilder().build();
    }

    public void create() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(application);

        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest
                    .BodyPublishers
                    .ofString(json.writeValueAsString(endpointHitDto));

            HttpRequest createRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/hit"))
                    .POST(bodyPublisher)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<Void> response = httpClient.send(createRequest, HttpResponse.BodyHandlers.discarding());
            log.info(response.toString());
        } catch (Exception e) {
            log.warn(String.valueOf(e));
        }
    }

    public void search(String start, String end, String[] uris, Boolean unique) {
        URI uri = URI.create(serverUrl + "/stats?start=" + start + "&end=" + end);
        if (uris != null && unique != null) {
            uri = URI.create(serverUrl + "/stats?start=" + start + "&end=" + end + toStringArrayUris(uris) + "&unique=" + unique);
        } else if (uris != null) {
            uri = URI.create(serverUrl + "/stats?start=" + start + "&end=" + end + toStringArrayUris(uris));
        } else if (unique != null) {
            uri = URI.create(serverUrl + "/stats?start=" + start + "&end=" + end + "&unique=" + unique);
        }
        try {
            HttpRequest createRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<Void> response = httpClient.send(createRequest, HttpResponse.BodyHandlers.discarding());
            log.info(response.toString());
        } catch (Exception e) {
            log.warn(String.valueOf(e));
        }
    }

    private StringBuilder toStringArrayUris(String[] uris) {
        StringBuilder str = new StringBuilder();
        for (String currentUri: uris) {
            str.append("&uris=").append(currentUri);
        }
        return str;
    }
}
