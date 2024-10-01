package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentWatchListException;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.WatchList;

public interface WatchListService {

    WatchList findUserWatchList(Long userId) throws NonExistentWatchListException;

    WatchList createWatchList(User user);

    WatchList findById(Long id) throws NonExistentWatchListException;

    Coin addCoinToWatchList(Coin coin , User user) throws NonExistentWatchListException;

}
