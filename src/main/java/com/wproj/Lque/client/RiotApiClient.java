package com.wproj.Lque.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "riotApiClient", url = "https://europe.api.riotgames.com")
public interface RiotApiClient {

    @GetMapping("riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
        JsonNode findPuuidByName(
                @PathVariable("gameName") String gameName,
                @PathVariable("tagLine") String tagLine);

    @GetMapping("lol/match/v5/matches/by-puuid/{PUUID}/ids?type=ranked&start=0&count=2")
        JsonNode findMatches(@PathVariable("PUUID") String puuid);

    @GetMapping("lol/match/v5/matches/{matchId}")
        JsonNode findStats(@PathVariable("matchId") String matchId);

}
