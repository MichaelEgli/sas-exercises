package org.example;

import org.springframework.stereotype.Component;

@Component
public class TodoProxy {
    private final RestClient restClient;

    public TodoProxy(RestClient restClient) {
        this.restClient = restClient;
    }

}
