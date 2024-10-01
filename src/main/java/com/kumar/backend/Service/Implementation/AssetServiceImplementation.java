package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentAssetException;
import com.kumar.backend.Model.Asset;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.AssetRepository;
import com.kumar.backend.Service.Abstraction.AssetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset=new Asset();

        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());
        Asset savedAsset=assetRepository.save(asset);
        return savedAsset;
    }

    @Override
    public Asset getAssetById(Long assetId) throws NonExistentAssetException {
        Asset asset=assetRepository.findById(assetId).orElseThrow(
                ()->new NonExistentAssetException("Asset with id " + assetId + " not found")
        );
        return asset;
    }

    @Override
    public Asset getAssetByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Asset> getUsersAssets(Long userId) {
        return List.of();
    }

    @Override
    public Asset updateAsset(Asset asset) {
        return null;
    }

    @Override
    public void deleteAsset(Asset asset) {

    }
}
