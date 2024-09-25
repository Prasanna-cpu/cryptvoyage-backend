package com.kumar.backend.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/coins")
@RestController
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    private final ObjectMapper objectMapper;

    @GetMapping("/coinlist")
    public ResponseEntity<ApiResponse> getCoinList(@RequestParam("page") int page)  {

        try {

            List<Coin> coinList =coinService.getCoinList(page);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(coinList,HttpStatus.OK.value(),"Coins retrieved successfully"));

        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }


    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<ApiResponse> getMarketChart(@PathVariable String coinId,@RequestParam("days") int days)  {

        try{

            String marketChart=coinService.getMarketChart(coinId,days);

            JsonNode jsonNode=objectMapper.readTree(marketChart);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(jsonNode,HttpStatus.OK.value(),"Market chart retrieved successfully"));

        }

        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }

    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchCoin(@RequestParam("q") String keyword){
        try{
            String coin=coinService.searchCoin(keyword);

            JsonNode jsonNode=objectMapper.readTree(coin);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(jsonNode,HttpStatus.OK.value(),"Coin retrieved successfully"));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<ApiResponse> getCoinDetails(@PathVariable String coinId){
        try{
            String coin=coinService.getCoinDetails(coinId);

            JsonNode jsonNode=objectMapper.readTree(coin);


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(jsonNode,HttpStatus.OK.value(),"Coin details retrieved successfully"));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/top-50")
    public ResponseEntity<ApiResponse> getTop50CoinsByMarketCapRank(){
        try{
            String coin=coinService.getTop50CoinsByMarketCapRank();

            JsonNode jsonNode=objectMapper.readTree(coin);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(jsonNode,HttpStatus.OK.value(),"Top 50 coins retrieved successfully"));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/trading")
    public ResponseEntity<ApiResponse> getTradingCoins(){
        try{
            String coin=coinService.getTradingCoins();

            JsonNode jsonNode=objectMapper.readTree(coin);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(jsonNode,HttpStatus.OK.value(),"Trading coins retrieved successfully"));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }


}
