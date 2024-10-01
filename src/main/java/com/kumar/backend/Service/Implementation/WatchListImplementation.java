package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentWatchListException;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.WatchList;
import com.kumar.backend.Repository.WatchListRepository;
import com.kumar.backend.Service.Abstraction.WatchListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class WatchListImplementation implements WatchListService {

    private final WatchListRepository watchListRepository;

    @Override
    public WatchList findUserWatchList(Long userId) throws NonExistentWatchListException {
        WatchList watchList=watchListRepository.findByUserId(userId).orElseThrow(()->new NonExistentWatchListException("WatchList not found"));
        return watchList;
    }

    @Override
    public WatchList createWatchList(User user) {
        WatchList watchList=new WatchList();
        watchList.setUser(user);
        WatchList newWatchList=watchListRepository.save(watchList);
        return newWatchList;
    }

    @Override
    public WatchList findById(Long id) throws NonExistentWatchListException {
        WatchList watchList=watchListRepository.findById(id).orElseThrow(()->new NonExistentWatchListException("WatchList not found"));
        return watchList;
    }

    @Override
    public Coin addCoinToWatchList(Coin coin, User user) throws NonExistentWatchListException {
        WatchList watchlist=findUserWatchList(user.getId());
        if(watchlist.getCoins().contains(coin)){
            watchlist.getCoins().remove(coin);
        }
        else watchlist.getCoins().add(coin);
        watchListRepository.save(watchlist);
        return coin;
    }
}
