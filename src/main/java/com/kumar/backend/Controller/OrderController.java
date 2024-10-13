package com.kumar.backend.Controller;

import com.kumar.backend.Exception.*;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Request.CreateOrderRequest;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.CoinService;
import com.kumar.backend.Service.Abstraction.OrderService;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Utils.Enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final UserService userService;

    private final CoinService coinService;

    @PostMapping("/pay")
    private ResponseEntity<ApiResponse> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest request
    ){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Coin coin=coinService.findById(request.getCoinId());

            Order order=orderService.processOrder(coin, request.getQuantity(),request.getOrderType(),user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            new ApiResponse(
                                    order,
                                    HttpStatus.OK.value(),
                                    "Payment Order created successfully"
                            )
                    );
        }
        catch(NonExistentUserException | NonExistentCoinException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
        catch(InsufficientQuantityException | InsufficientBalanceException | InvalidOrderTypeException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@RequestHeader("Authorization") String jwt, @PathVariable Long orderId) {
        try{
            User user=userService.findUserProfileByJwt(jwt);

            Order order=orderService.getOrderById(orderId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(
                                    order,
                                    HttpStatus.OK.value(),
                                    "Order retrieved successfully"
                            )
                    );
        }
        catch(NonExistentUserException | NonExistentOrderException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllOrdersForUsers(@RequestHeader("Authorization") String jwt
            ,@RequestParam(required = false) String orderType
            ,@RequestParam(required = false) String assetSymbol) {

        try{
            Long userId=userService.findUserProfileByJwt(jwt).getId();

            List<Order> orders=orderService.getAllOrdersOfUser(userId, OrderType.valueOf(orderType),assetSymbol);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            new ApiResponse(
                                    orders,
                                    HttpStatus.OK.value(),
                                    "Orders retrieved successfully"
                            )
                    );
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }

    }






}
