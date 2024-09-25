package com.kumar.backend.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumar.backend.Exception.NonExistentCoinException;
import com.kumar.backend.Model.Coin;
import com.kumar.backend.Repository.CoinRepository;
import com.kumar.backend.Service.Abstraction.CoinService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class CoinServiceImplementation implements CoinService {

    private final CoinRepository coinRepository;

    private final ObjectMapper objectMapper;

    private double convertToDouble(Object value) {
        return switch (value) {
            case Integer i -> i.doubleValue();
            case Long l -> l.doubleValue();
            case Double v -> v;
            case null, default -> {
                assert value != null;
                throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getName());
            }
        };
    }

    @Override
    public List<Coin> getCoinList(int page) throws Exception {

        String url="https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page="+page;

        RestTemplate restTemplate=new RestTemplate();

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            List<Coin> coins=objectMapper.readValue(response.getBody(), new TypeReference<List<Coin>>() {
            });

            return coins;
        } catch (JsonProcessingException | HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url="https://api.coingecko.com/api/v3/coins/"+coinId+"/market_chart?vs_currency=usd&days="+days;

        RestTemplate restTemplate=new RestTemplate();

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch(HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {

        String url= "https://api.coingecko.com/api/v3/coins/"+coinId;

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>(headers);

            RestTemplate restTemplate=new RestTemplate();

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode jsonNode=objectMapper.readTree(response.getBody());

            Coin coin=new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            JsonNode marketData=jsonNode.get("market_data");

            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(jsonNode.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setCirculatingSupply(marketData.get("circulating_supply").asLong());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            coinRepository.save(coin);
            return response.getBody();
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public Coin findById(String coinId) throws NonExistentCoinException {
        Coin coin=coinRepository.findById(coinId).orElseThrow(()->new NonExistentCoinException("Coin does not exist"));
        return coin;
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        String url="https://api.coingecko.com/api/v3/search?query="+keyword;

        RestTemplate restTemplate=new RestTemplate();

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch(HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTop50CoinsByMarketCapRank() throws Exception {
        String url="https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&page=1&per_page=50";

        RestTemplate restTemplate=new RestTemplate();

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch(HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTradingCoins() throws Exception {
        String url="https://api.coingecko.com/api/v3/search/trending";

        RestTemplate restTemplate=new RestTemplate();

        try{
            HttpHeaders headers=new HttpHeaders();

            HttpEntity<String> entity=new HttpEntity<>("parameters",headers);

            ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch(HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }
}
