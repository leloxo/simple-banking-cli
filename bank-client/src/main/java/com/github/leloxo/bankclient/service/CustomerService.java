package com.github.leloxo.bankclient.service;

import com.github.leloxo.bankclient.model.customer.Customer;
import com.github.leloxo.bankclient.model.customer.CustomerDto;
import com.github.leloxo.bankclient.model.customer.LoginRequestPayload;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CustomerService {
    private final WebClient.Builder webClientBuilder;

    public CustomerService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private final String BASE_URL = "http://localhost:8080/customers";

    public List<CustomerDto> getCustomers() {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CustomerDto>>() {})
                .block();
    }

    public void updateCustomer(String email, CustomerDto updatedCustomer) {
        webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/update/" + email)
                .bodyValue(updatedCustomer)
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .block();
    }

    public void deleteCustomer(String email) {
        webClientBuilder.build()
                .delete()
                .uri(BASE_URL + "/delete/" + email)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public CustomerDto getCustomerByEmail(String email) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/get/" + email)
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .block();
    }

    public void register(Customer customerData) {
        webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/register")
                .bodyValue(customerData)
                .retrieve()
                .bodyToMono(Customer.class)
                .block();
    }

    public boolean login(LoginRequestPayload loginRequest) {
        return Boolean.TRUE.equals(webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/login")
                .bodyValue(loginRequest)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return response.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException(errorMessage)));
                    }
                })
                .block());
    }

}
