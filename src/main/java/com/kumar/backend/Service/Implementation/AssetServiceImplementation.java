package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentAssetException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.Asset;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.AssetRepository;
import com.kumar.backend.Repository.UserRepository;
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
    private final UserRepository userRepository;

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
    public Asset getAssetByUserIdAndCoinId(Long userId, String coinId) throws NonExistentAssetException, NonExistentUserException {
        User user=userRepository.findById(userId).orElseThrow(
                ()->new NonExistentUserException("User with id " + userId + " not found")
        );

        Asset asset=assetRepository.findByUserIdAndCoinId(user.getId(),coinId).orElseThrow(
                ()->new NonExistentAssetException("Asset not found")
        );

        return asset;

    }

    @Override
    public List<Asset> getUsersAssets(Long userId) throws NonExistentUserException, NonExistentAssetException {
        User user=userRepository.findById(userId).orElseThrow(
                ()->new NonExistentUserException("User with id " + userId + " not found")
        );

        List<Asset> assets=assetRepository.findAllByUserId(user.getId()).orElseThrow(
                ()->new NonExistentAssetException("Assets with user id " + userId + " not found")
        );

        return assets;
    }

    @Override
    public Asset updateAsset(Long assetId, double quantity) throws NonExistentAssetException {
        Asset oldAsset=getAssetById(assetId);
        oldAsset.setQuantity(quantity+oldAsset.getQuantity());
        return assetRepository.save(oldAsset);
    }

    @Override
    public void deleteAsset(Long assetId) throws NonExistentAssetException {
        Asset asset=getAssetById(assetId);

        assetRepository.deleteById(asset.getId());

    }
}
