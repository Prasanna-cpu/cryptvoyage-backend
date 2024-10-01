package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentAssetException;
import com.kumar.backend.Model.Asset;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;

import java.util.List;

public interface AssetService {

    Asset createAsset(User user, Coin coin,double quantity);

    Asset getAssetById(Long assetId) throws NonExistentAssetException;

    Asset getAssetByUserId(Long userId);

    List<Asset> getUsersAssets(Long userId);

    Asset updateAsset(Asset asset);

    void deleteAsset(Asset asset);
}
