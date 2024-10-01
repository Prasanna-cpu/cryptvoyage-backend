package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.*;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.OrderItem;
import com.kumar.backend.Model.User;
import com.kumar.backend.Utils.Enums.OrderType;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws NonExistentOrderException;

    List<Order> getAllOrdersOfUser(Long userId,OrderType orderType,String assetSymbol) throws NonExistentUserException;

    Order processOrder(Coin coin,double quantity,OrderType orderType,User user) throws InsufficientBalanceException, InsufficientQuantityException, InvalidOrderTypeException, NonExistentAssetException, NonExistentUserException;




}
