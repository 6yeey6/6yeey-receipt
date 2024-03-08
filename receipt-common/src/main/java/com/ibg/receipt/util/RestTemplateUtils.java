package com.ibg.receipt.util;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class RestTemplateUtils {
    private static RestTemplate restTemplate;
    static {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(30000);
        httpRequestFactory.setReadTimeout(120000);
        restTemplate = new RestTemplate(httpRequestFactory);
    }

    public static <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public static <T> ResponseEntity<T> getForEntity(String url,Class<T> responseType) throws URISyntaxException {
        return restTemplate.getForEntity(new URI(url) , responseType);
    }
}
