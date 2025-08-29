package com.business.foodie.api.serivce;

import com.business.foodie.api.constant.APIConstants;
import com.business.foodie.api.io.InitializePaymentResponse;
import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.io.PaymentVerificationResponse;
import com.business.foodie.api.model.OrderEntity;
import com.business.foodie.api.repository.OrderRepository;
import com.business.foodie.api.repository.PaystackPaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService{

    private final OrderRepository orderRepository;
    private final PaystackPaymentRepository paymentRepository;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    private static final String PAYSTACK_INITIALIZE_PAY = APIConstants.PAYSTACK_INITIALIZE_PAY;
    private static final String PAYSTACK_VERIFY = APIConstants.PAYSTACK_VERIFY;

    private static final int STATUS_CODE_OK = APIConstants.STATUS_CODE_OK;

//
//    public OrderServiceImp(PaystackPaymentRepository paymentRepository, OrderRepository  orderRepository) {
//        this.paymentRepository = paymentRepository;
//        this.orderRepository = orderRepository;
//    }

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) {
       OrderEntity newOrder =  convertToEntity(request);
       newOrder = orderRepository.save(newOrder);

        // Prepare response
        return OrderResponse.builder()
                .id(newOrder.getId())
                .userId(newOrder.getUserId())
                .userAddress(newOrder.getUserAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .amount(newOrder.getAmount())
                .paymentStatus("pending")
                .orderStatus("CREATED")
                .build();
    }

    @Override
    public InitializePaymentResponse initializePayment(OrderRequest request) {
        InitializePaymentResponse initializePaymentResponse = null;

        try {
            // Paystack expects amount in Kobo => multiply by 100
            request.setAmount(request.getAmount() * 100);

            Gson gson = new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(request));
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(PAYSTACK_INITIALIZE_PAY);

            post.setEntity(postingString);
            post.addHeader("Content-type", "application/json");
            post.addHeader("Authorization", "Bearer " + paystackSecretKey);

            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new Exception("Paystack is unable to initialize payment at the moment");
            }

            ObjectMapper mapper = new ObjectMapper();
            initializePaymentResponse = mapper.readValue(result.toString(), InitializePaymentResponse.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return initializePaymentResponse;

    }

    @Override
    public PaymentVerificationResponse paymentVerification(String reference, Long id) throws Exception {
        return null;
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userId(request.getUserId())
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .ordersItems(request.getOrderItems())
                .build();
    }
}
