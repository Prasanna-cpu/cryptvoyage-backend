package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentAssetException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.Asset;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;

import java.util.List;

public interface AssetService {

    Asset createAsset(User user, Coin coin,double quantity);

    Asset getAssetById(Long assetId) throws NonExistentAssetException;

    Asset getAssetByUserIdAndCoinId(Long userId, String coinId) throws NonExistentAssetException, NonExistentUserException;

    List<Asset> getUsersAssets(Long userId) throws NonExistentUserException, NonExistentAssetException;


    Asset updateAsset(Long assetId, double quantity) throws NonExistentAssetException;

    void deleteAsset(Long assetId) throws NonExistentAssetException;
}
