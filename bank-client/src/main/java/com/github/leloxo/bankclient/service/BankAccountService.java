package com.github.leloxo.bankclient.service;

import com.github.leloxo.bankclient.model.bankaccount.BankAccount;
import com.github.leloxo.bankclient.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankclient.model.bankaccount.MoneyTransferRequestPayload;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BankAccountService {
    private final WebClient.Builder webClientBuilder;

    public BankAccountService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private final String BASE_URL = "http://localhost:8080/accounts";

    public BankAccount createBankAccount(String email) {
        return webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/create/" + email)
                .retrieve()
                .bodyToMono(BankAccount.class)
                .block();
    }

    public List<BankAccountDto> getBankAccountsByEmail(String email) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/get/" + email)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BankAccountDto>>() {})
                .block();
    }

    public boolean transferMoney(MoneyTransferRequestPayload transferRequest) {
        return Boolean.TRUE.equals(webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/transfer")
                .bodyValue(transferRequest)
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
