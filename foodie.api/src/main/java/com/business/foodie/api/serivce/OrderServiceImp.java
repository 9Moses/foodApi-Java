package com.business.foodie.api.serivce;

import com.business.foodie.api.constant.APIConstants;
import com.business.foodie.api.io.InitializePaymentResponse;
import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.io.PaymentVerificationResponse;
import com.business.foodie.api.model.OrderEntity;
import com.business.foodie.api.model.PaymentPaystackEntity;
import com.business.foodie.api.repository.CartRepository;
import com.business.foodie.api.repository.OrderRepository;
import com.business.foodie.api.repository.PaystackPaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService{

    private final OrderRepository orderRepository;
    private final PaystackPaymentRepository paymentRepository;
    private final UserService userService;
    private final CartRepository cartRepository;

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
                .data(OrderResponse.Data.builder()
                        .id(newOrder.getId())

                        .amount(newOrder.getAmount())

                        .build())
                .userId(newOrder.getUserId())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .paymentStatus("pending")
                .orderStatus("CREATED")
                .build();
    }

    @Override
    public InitializePaymentResponse initializePayment(OrderRequest request) {
        InitializePaymentResponse initializePaymentResponse = null;

        try {
            // Paystack expects amount in Kobo
            request.setAmount(request.getAmount() * 100);

            Gson gson = new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(request));

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(PAYSTACK_INITIALIZE_PAY);

            post.setEntity(postingString);
            post.addHeader("Content-type", "application/json");
            post.addHeader("Authorization", "Bearer " + paystackSecretKey);

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            StringBuilder result = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // Log what Paystack returns
            System.out.println("Paystack response code: " + statusCode);
            System.out.println("Paystack response body: " + result);

            if (statusCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                initializePaymentResponse = mapper.readValue(result.toString(), InitializePaymentResponse.class);
            } else {
                throw new Exception("Paystack failed with status " + statusCode + " - " + result.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return initializePaymentResponse;
    }

    @Override
    @Transactional
    public PaymentVerificationResponse paymentVerification(String reference, String id) throws Exception {

        // Update order with payment success
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentVerificationResponse paymentVerificationResponse = null;

        try{

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(PAYSTACK_VERIFY + reference);
            request.addHeader("Content-type", "application/json");
            request.addHeader("Authorization", "Bearer " + paystackSecretKey);

            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(request);


            if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new Exception("Paystack is unable to verify payment at the moment");
            }

            ObjectMapper mapper = new ObjectMapper();
            paymentVerificationResponse = mapper.readValue(result.toString(), PaymentVerificationResponse.class);

            if (paymentVerificationResponse != null
                    && "success".equals(paymentVerificationResponse.getData().getStatus())) {


                order.setPaymentStatus("PAID");
                order.setOrderStatus("Preparing");
                order = orderRepository.save(order);

                cartRepository.deleteByUserId(order.getUserId());

                // Save payment info
                PaymentPaystackEntity payment = PaymentPaystackEntity.builder()
                        .userId(order.getUserId())
                        .reference(paymentVerificationResponse.getData().getReference())
                        .amount(BigDecimal.valueOf(paymentVerificationResponse.getData().getAmount().longValue()))
                        .gatewayResponse(paymentVerificationResponse.getData().getGatewayResponse())
                        .paidAt(paymentVerificationResponse.getData().getPaidAt())
                        .createdAt(paymentVerificationResponse.getData().getCreatedAt())
                        .channel(paymentVerificationResponse.getData().getChannel())
                        .currency(paymentVerificationResponse.getData().getCurrency())
                        .ipAddress(paymentVerificationResponse.getData().getIpAddress())
                        .createdOn(new Date())
                        .build();

                paymentRepository.save(payment);

                String loggedInUserId = userService.findByUserId();
                order.setUserId(loggedInUserId);
                order = orderRepository.save(order);
                return convertToResponse(order);


            }

        }catch(Exception ex){
            throw new Exception("Paystack verification failed", ex);
        }

        return null;
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
       List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
      return list.stream().map(entity -> convertToOderResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
     orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getAllOrdersOfAllUsers() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToOderResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
       OrderEntity entity =  orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order not found"));
       entity.setOrderStatus(status);
       orderRepository.save(entity);
    }

    private OrderResponse convertToOderResponse(OrderEntity order) {
        return OrderResponse.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .email(order.getEmail())
                .userId(order.getUserId())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .phoneNumber(order.getPhoneNumber())
                .orderItemList(order.getOrdersItems())
                .build();
    }


    private PaymentVerificationResponse convertToResponse(OrderEntity order) {
        return PaymentVerificationResponse.builder()
                .id(order.getId())
                .status(order.getOrderStatus()) // e.g. "success", "pending", etc.
                .message("Payment verification result for order " + order.getId())
                .data(PaymentVerificationResponse.Data.builder()
                        .status(order.getOrderStatus())
                        .amount(BigDecimal.valueOf(order.getAmount())) // store in Naira
                        .updatedOn(new Date()) // or map from entity
                        .build())
                .build();
    }


    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .ordersItems(request.getOrderItems())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}
