package com.proje.elektrikli_arac_sarj_sistemi.payment;

import com.iyzipay.HttpClient;
import com.iyzipay.IyziAuthV2Generator;
import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Payment;
import com.iyzipay.model.PaymentCard;
import com.iyzipay.request.CreatePaymentPostAuthRequest;
import com.iyzipay.request.CreatePaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.iyzipay.request.CreateCancelRequest;

import java.math.BigDecimal;
import java.util.*;

@Component
public class IyzicoPaymentClient implements PaymentProviderClient {

    private final Options options;

    public IyzicoPaymentClient(
            @Value("${iyzico.api-key}") String apiKey,
            @Value("${iyzico.secret-key}") String secretKey,
            @Value("${iyzico.base-url}") String baseUrl) {

        this.options = new Options();
        this.options.setApiKey(apiKey);
        this.options.setSecretKey(secretKey);
        this.options.setBaseUrl(baseUrl);
    }

    @Override
    public ProvisionAuthorizationResult authorizeProvision(
            BigDecimal amount,
            PaymentCardInfo cardInfo) {

        String conversationId = UUID.randomUUID().toString();
        String basketId = "PROVISION-" + UUID.randomUUID();

        CreatePaymentRequest request = new CreatePaymentRequest();

        request.setLocale("tr");
        request.setConversationId(conversationId);

        request.setPrice(amount);
        request.setPaidPrice(amount);

        request.setCurrency("TRY");
        request.setInstallment(1);

        request.setBasketId(basketId);
        request.setPaymentChannel("WEB");
        request.setPaymentGroup("PRODUCT");

        /*
         * Kart bilgileri
         */
        PaymentCard paymentCard = new PaymentCard();

        paymentCard.setCardHolderName(
                cardInfo.getCardHolderName()
        );
        paymentCard.setCardNumber(
                cardInfo.getCardNumber()
        );
        paymentCard.setExpireMonth(
                cardInfo.getExpireMonth()
        );
        paymentCard.setExpireYear(
                cardInfo.getExpireYear()
        );
        paymentCard.setCvc(
                cardInfo.getCvc()
        );
        paymentCard.setRegisterCard(0);

        request.setPaymentCard(paymentCard);

        /*
         * Buyer
         */
        Buyer buyer = new Buyer();

        buyer.setId("USER-" + UUID.randomUUID());
        buyer.setName(cardInfo.getCardHolderName());
        buyer.setSurname("Customer");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("test@example.com");
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress("Istanbul");
        buyer.setIp("127.0.0.1");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34000");

        request.setBuyer(buyer);

        /*
         * Billing address
         */
        Address billingAddress = new Address();

        billingAddress.setContactName(
                cardInfo.getCardHolderName()
        );
        billingAddress.setCity("Istanbul");
        billingAddress.setCountry("Turkey");
        billingAddress.setAddress("Istanbul");
        billingAddress.setZipCode("34000");

        request.setBillingAddress(billingAddress);

        /*
         * Shipping address
         */
        Address shippingAddress = new Address();

        shippingAddress.setContactName(
                cardInfo.getCardHolderName()
        );
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Istanbul");
        shippingAddress.setZipCode("34000");

        request.setShippingAddress(shippingAddress);

        /*
         * Basket
         */
        BasketItem basketItem = new BasketItem();

        basketItem.setId("CHARGING-" + UUID.randomUUID());
        basketItem.setName("Electric Vehicle Charging");
        basketItem.setCategory1("Electric Vehicle");
        basketItem.setCategory2("Charging Service");
        basketItem.setItemType("VIRTUAL");
        basketItem.setPrice(amount);

        request.setBasketItems(
                Collections.singletonList(basketItem)
        );

        /*
         * Iyzico V2 authentication
         */
        String randomKey = String.valueOf(
                System.currentTimeMillis()
        );

        String path = "/payment/preauth";

        String authorizationContent =
                IyziAuthV2Generator.generateAuthContent(
                        path,
                        options.getApiKey(),
                        options.getSecretKey(),
                        randomKey,
                        request
                );

        Map<String, String> headers = new HashMap<>();

        headers.put(
                "Authorization",
                "IYZWSv2 " + authorizationContent
        );

        headers.put(
                "x-iyzi-rnd",
                randomKey
        );

        /*
         * Gerçek PreAuth çağrısı
         */
        Payment payment =
                HttpClient.create().post(
                        options.getBaseUrl() + path,
                        null,
                        headers,
                        request,
                        Payment.class
                );

        if (!"success".equalsIgnoreCase(
                payment.getStatus())) {

            return new ProvisionAuthorizationResult(
                    false,
                    null
            );
        }

        /*
         * Iyzico paymentId bizim providerReferenceId'miz.
         */
        return new ProvisionAuthorizationResult(
                true,
                payment.getPaymentId()
        );
    }

    @Override
    public CaptureResult captureAmount(
            String providerReferenceId,
            BigDecimal amount) {

        CreatePaymentPostAuthRequest request =
                new CreatePaymentPostAuthRequest();

        String conversationId =
                UUID.randomUUID().toString();

        request.setLocale("tr");
        request.setConversationId(conversationId);
        request.setPaymentId(providerReferenceId);
        request.setPaidPrice(amount);
        request.setCurrency("TRY");
        request.setIp("127.0.0.1");

        String randomKey = String.valueOf(
                System.currentTimeMillis()
        );

        String path = "/payment/postauth";

        String authorizationContent =
                IyziAuthV2Generator.generateAuthContent(
                        path,
                        options.getApiKey(),
                        options.getSecretKey(),
                        randomKey,
                        request
                );

        Map<String, String> headers = new HashMap<>();

        headers.put(
                "Authorization",
                "IYZWSv2 " + authorizationContent
        );

        headers.put(
                "x-iyzi-rnd",
                randomKey
        );

        /*
         * Gerçek PostAuth çağrısı
         */
        Payment payment =
                HttpClient.create().post(
                        options.getBaseUrl() + path,
                        null,
                        headers,
                        request,
                        Payment.class
                );

        if (!"success".equalsIgnoreCase(
                payment.getStatus())) {

            return new CaptureResult(
                    false,
                    null
            );
        }

        return new CaptureResult(
                true,
                payment.getPaymentId()
        );
    }


    @Override
    public RefundResult refundAmount(
        String paymentId,
        BigDecimal amount) {

    IyzicoRefundRequest request = new IyzicoRefundRequest();

    String conversationId = UUID.randomUUID().toString();

    request.setLocale("tr");
    request.setConversationId(conversationId);
    request.setPaymentId(paymentId);
    request.setPrice(amount);
    request.setCurrency("TRY");
    request.setIp("127.0.0.1");

    String randomKey = String.valueOf(
            System.currentTimeMillis()
    );

    String path = "/v2/payment/refund";

    String authorizationContent =
            IyziAuthV2Generator.generateAuthContent(
                    path,
                    options.getApiKey(),
                    options.getSecretKey(),
                    randomKey,
                    request
            );

    Map<String, String> headers = new HashMap<>();

    headers.put(
            "Authorization",
            "IYZWSv2 " + authorizationContent
    );

    headers.put(
            "x-iyzi-rnd",
            randomKey
    );

    Payment payment =
            HttpClient.create().post(
                    options.getBaseUrl() + path,
                    null,
                    headers,
                    request,
                    Payment.class
            );

    if (!"success".equalsIgnoreCase(
            payment.getStatus())) {

        return new RefundResult(
                false,
                null,
                BigDecimal.ZERO
        );
    }

    return new RefundResult(
            true,
            payment.getPaymentId(),
            amount
    );
  }

@Override
public boolean cancelProvision(String providerReferenceId) {

    CreateCancelRequest request = new CreateCancelRequest();

    String conversationId = UUID.randomUUID().toString();

    request.setLocale("tr");
    request.setConversationId(conversationId);
    request.setPaymentId(providerReferenceId);
    request.setIp("127.0.0.1");

    String randomKey = String.valueOf(
            System.currentTimeMillis()
    );

    String path = "/payment/cancel";

    String authorizationContent =
            IyziAuthV2Generator.generateAuthContent(
                    path,
                    options.getApiKey(),
                    options.getSecretKey(),
                    randomKey,
                    request
            );

    Map<String, String> headers = new HashMap<>();

    headers.put(
            "Authorization",
            "IYZWSv2 " + authorizationContent
    );

    headers.put(
            "x-iyzi-rnd",
            randomKey
    );

    Payment payment =
            HttpClient.create().post(
                    options.getBaseUrl() + path,
                    null,
                    headers,
                    request,
                    Payment.class
            );

    return "success".equalsIgnoreCase(
            payment.getStatus()
    );
}
}