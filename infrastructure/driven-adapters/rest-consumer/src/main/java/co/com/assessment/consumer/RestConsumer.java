package co.com.assessment.consumer;

import co.com.assessment.model.Confirmation;
import co.com.assessment.model.PurchaseDetails;
import co.com.assessment.model.gateways.PaymentGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RestConsumer implements PaymentGateway{
    private final WebClient client;
    private final ObjectMapper mapper;

    public RestConsumer(WebClient client,
                        ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Mono<Confirmation> processPayment(PurchaseDetails purchaseDetails) {
        PaymentRequest request = mapper.map(purchaseDetails, PaymentRequest.class);
        Mono<PaymentResponse> response = client.post()
                .uri("/pay")
                .body(Mono.just(request), PaymentRequest.class)
                .retrieve()
                .bodyToMono(PaymentResponse.class);

        return response.flatMap(r -> Mono.just(Confirmation.builder()
                    .status(r.getStatus())
                    .transactionDetails(
                            Confirmation.TransactionDetails.builder()
                            .transactionId(r.getTransactionDetails().getTransactionId())
                            .paymentMethod(r.getTransactionDetails().getPaymentMethod())
                            .amountPaid(r.getTransactionDetails().getAmountPaid())
                            .build())
                    .build()));
    }
}
