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

import java.math.BigDecimal;
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
        double price=orderItem.getCoin().getCurrentPrice();
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid coin price. It must be greater than 0.");
        }
        Order order=new Order();
        order.setUser(user);
        order.setOrderType(orderType);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        order.setOrderItem(orderItem);
        orderItem.setOrder(order);

        Order savedOrder=orderRepository.save(order);
        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        return savedOrder;
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
    public Order sellAsset(Coin coin,double quantity,User user) throws InsufficientQuantityException, NonExistentAssetException, NonExistentUserException, InsufficientBalanceException, ExcessQuantityException {
        if(quantity<=0){
            throw new InsufficientQuantityException("Quantity must be greater than 0");
        }

        double sellPrice=coin.getCurrentPrice();

        // TODO : implement sellAsset function

        Asset assetToSell=assetService.getAssetByUserIdAndCoinId(user.getId(),coin.getId());

        if(assetToSell!=null){

            double buyPrice=assetToSell.getBuyPrice();

            OrderItem orderItem=createOrderItem(coin,quantity,buyPrice,sellPrice);

            Order order=createOrder(user,orderItem,OrderType.SELL);

            orderItem.setOrder(order);

            if(assetToSell.getQuantity()>=quantity){
                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);
                Order savedOrder=orderRepository.save(order);

                walletService.orderPayment(order,user);

                Asset updatedAsset=assetService.updateAsset(assetToSell.getId(),-quantity);
                if(updatedAsset.getQuantity()*coin.getCurrentPrice()<=1){
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            }
            throw new ExcessQuantityException("Selling asset quantity must be less than your current asset quantity");

        }



        throw new NonExistentAssetException("Asset not found");
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
        if(oldAsset==null){
            Asset assetCreated=assetService.createAsset(order.getUser(),order.getOrderItem().getCoin(),order.getOrderItem().getQuantity());
        }
        else{
            Asset assetUpdated=assetService.updateAsset(oldAsset.getId(),order.getOrderItem().getQuantity());
        }

        return savedOrder;
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws InsufficientBalanceException, InsufficientQuantityException, InvalidOrderTypeException, NonExistentAssetException, NonExistentUserException, ExcessQuantityException {
        if(orderType.equals(OrderType.BUY)){
            return buyAsset(coin,quantity,user);
        }
        else if(orderType==OrderType.SELL){
            return sellAsset(coin,quantity,user);
        }
        throw new InvalidOrderTypeException("Invalid order type");
    }
}
