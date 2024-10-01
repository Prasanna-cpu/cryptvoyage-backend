package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.*;
import com.kumar.backend.Model.*;
import com.kumar.backend.Repository.CoinRepository;
import com.kumar.backend.Repository.OrderItemRepository;
import com.kumar.backend.Repository.OrderRepository;
import com.kumar.backend.Service.Abstraction.AssetService;
import com.kumar.backend.Service.Abstraction.OrderService;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Utils.Enums.OrderStatus;
import com.kumar.backend.Utils.Enums.OrderType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class OrderServiceImplementation implements OrderService {
    private final CoinRepository coinRepository;

    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final OrderItemRepository orderItemRepository;
    private final AssetService assetService;

    private OrderItem createOrderItem(Coin coin, double quantity , double buyPrice , double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setCoin(coin);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price=orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();
        Order order=new Order();
        order.setUser(user);
        order.setOrderType(orderType);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    @Override
    public Order getOrderById(Long orderId) throws NonExistentOrderException {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new NonExistentOrderException("Order with id " + orderId + " not found"));
        return order;
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) throws NonExistentUserException {
       List<Order> orders=orderRepository.findByUserId(userId).orElseThrow(()->new NonExistentUserException("User with id " + userId + ""));
       return orders;
    }

    @Transactional
    public Order sellAsset(Coin coin,double quantity,User user) throws InsufficientQuantityException {
        if(quantity<=0){
            throw new InsufficientQuantityException("Quantity must be greater than 0");
        }

        double sellPrice=coin.getCurrentPrice();

        // TODO : implement sellAsset function

        return null;
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity,User user) throws InsufficientQuantityException, InsufficientBalanceException, NonExistentAssetException, NonExistentUserException {
        if(quantity<=0){
            throw new InsufficientQuantityException("Quantity must be greater than 0");
        }

        double buyPrice=coin.getCurrentPrice();

        OrderItem orderItem=createOrderItem(coin,quantity,buyPrice,0);
        Order order=createOrder(user,orderItem,OrderType.BUY);

        walletService.orderPayment(order,user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder=orderRepository.save(order);

        // TODO create asset

        Asset oldAsset=assetService.getAssetByUserIdAndCoinId(order.getUser().getId(),order.getOrderItem().getCoin().getId());

        return savedOrder;
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws InsufficientBalanceException, InsufficientQuantityException, InvalidOrderTypeException, NonExistentAssetException, NonExistentUserException {
        if(orderType.equals(OrderType.BUY)){
            return buyAsset(coin,quantity,user);
        }
        else if(orderType==OrderType.SELL){
            return sellAsset(coin,quantity,user);
        }
        throw new InvalidOrderTypeException("Invalid order type");
    }
}
