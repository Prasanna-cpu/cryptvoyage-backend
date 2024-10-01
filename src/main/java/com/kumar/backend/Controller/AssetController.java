package com.kumar.backend.Controller;

import com.kumar.backend.Exception.NonExistentAssetException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.Asset;
import com.kumar.backend.Model.User;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.AssetService;
import com.kumar.backend.Service.Abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    private final UserService userService;


    @GetMapping("/asset/{assetId}")
    public ResponseEntity<ApiResponse> getAssetById(@PathVariable Long assetId){
        try{
            Asset asset=assetService.getAssetById(assetId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(asset,HttpStatus.OK.value(),"Asset retrieved successfully")
                    );
        }
        catch(NonExistentAssetException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<ApiResponse> getAssetByUserIdAndCoinId(@PathVariable String coinId, @RequestHeader("Authorization") String jwt){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Asset asset=assetService.getAssetByUserIdAndCoinId(user.getId(),coinId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(asset,HttpStatus.OK.value(),"Asset retrieved successfully")
                    );
        }
        catch(NonExistentAssetException | NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }







}
