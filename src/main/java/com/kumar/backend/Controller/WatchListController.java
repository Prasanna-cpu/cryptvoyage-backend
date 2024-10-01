package com.kumar.backend.Controller;


import com.kumar.backend.Exception.NonExistentCoinException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentWatchListException;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.WatchList;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.CoinService;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Service.Abstraction.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchListController {
    private final WatchListService watchListService;
    private final CoinService coinService;
    private final UserService userService;


    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserWatchList(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            WatchList watchList = watchListService.findUserWatchList(user.getId());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(watchList, HttpStatus.OK.value(), "WatchList retrieved successfully"));
        } catch (NonExistentUserException | NonExistentWatchListException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }


    @GetMapping("/{watchListId}")
    public ResponseEntity<ApiResponse> getWatchList(@RequestHeader("Authorization") String jwt, @PathVariable Long watchListId) {
        try {
            WatchList watchList = watchListService.findById(watchListId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(watchList, HttpStatus.OK.value(), "WatchList retrieved successfully"));
        } catch (NonExistentWatchListException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }

    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createWatchList(@RequestHeader("Authorization") String jwt) {
        try{
            User user = userService.findUserProfileByJwt(jwt);
            WatchList createdWatchList=watchListService.createWatchList(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(createdWatchList, HttpStatus.OK.value(), "WatchList created successfully"));
        }
        catch (NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<ApiResponse> addCoinToWatchList(@RequestHeader("Authorization") String jwt, @PathVariable String coinId) {
        try{
            User user = userService.findUserProfileByJwt(jwt);

            Coin coin=coinService.findById(coinId);

            Coin addedCoin = watchListService.addCoinToWatchList(coin, user);


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(addedCoin, HttpStatus.OK.value(), "Coin added to watchlist successfully"));
        }
        catch (NonExistentUserException | NonExistentWatchListException | NonExistentCoinException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
