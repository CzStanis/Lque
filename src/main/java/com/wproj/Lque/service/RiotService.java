package com.wproj.Lque.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.AtomicDouble;
import com.wproj.Lque.client.RiotApiClient;
import com.wproj.Lque.dto.PlayerKDA;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Service
public class RiotService {
    private final RiotApiClient riotApiClient;

    public RiotService(RiotApiClient riotApiClient) {
        this.riotApiClient = riotApiClient;
    }

    public String getPuuidByName(String gameName, String tagLine) {
        JsonNode summoner = riotApiClient.findPuuidByName(gameName, tagLine);

        System.out.println("PUUID: " + summoner.get("puuid"));
        return summoner.get("puuid").asText();
    }
    public String getMatchId(String puuid) {
        JsonNode matches = riotApiClient.findMatches(puuid);
        System.out.println(matches.get(0).asText());
        return matches.get(0).asText();
    }

    public String getLastMatchOfOtherPlayer(String puuid) {
        JsonNode matches = riotApiClient.findMatches(puuid);
        return matches.get(1).asText();
    }
    public void getStats(String matchId, String puuid) {
        JsonNode matchData = riotApiClient.findStats(matchId);
        JsonNode participants = matchData.path("info").path("participants");

        Map<String, Map<String, PlayerKDA>> result = new HashMap<>();
        Map<String, PlayerKDA> kdaMapTeam1 = new HashMap<>();
        Map<String, PlayerKDA> kdaMapTeam2 = new HashMap<>();
        AtomicDouble team1avg = new AtomicDouble(0);
        AtomicDouble team2avg = new AtomicDouble(0);

        Optional<JsonNode> playerTeam = StreamSupport.stream(participants.spliterator(), false)
                .filter(participant -> participant.path("puuid").asText().equals(puuid))
                        .findFirst();
        int playerTeamId = playerTeam.get().get("teamId").asInt();

        StreamSupport.stream(participants.spliterator(), false)
                .filter(participant -> !participant.path("puuid").asText().equals(puuid))
                .forEach(participant -> {
                    String otherPuuid = participant.path("puuid").asText();
                    // Pobierz listę ostatnich meczów dla tego gracza
                    String lastMatchId = getLastMatchOfOtherPlayer(otherPuuid);
                    int teamId = participant.path("teamId").asInt();
                    // Pobierz dane tego meczu
                    JsonNode lastMatchData = riotApiClient.findStats(lastMatchId);
                    String summonerName = participant.path("summonerName").asText();

                    // Wyciągnij statystyki gracza z poprzedniego meczu
                    JsonNode lastMatchParticipants = lastMatchData.path("info").path("participants");
                    Optional<JsonNode> playerInLastMatch = StreamSupport.stream(lastMatchParticipants.spliterator(), false)
                            .filter(p -> p.path("puuid").asText().equals(otherPuuid))
                            .findFirst();

                    playerInLastMatch.ifPresent(player -> {
                        int kills = player.path("kills").asInt();
                        int deaths = player.path("deaths").asInt();
                        int assists = player.path("assists").asInt();

                        PlayerKDA playerKDA = new PlayerKDA(kills, deaths, assists);

                        if (teamId == 100) {
                            kdaMapTeam1.put(summonerName, playerKDA);
                            team1avg.addAndGet(playerKDA.getKda());
                        } else if (teamId == 200) {
                            kdaMapTeam2.put(summonerName, playerKDA);
                            team2avg.addAndGet(playerKDA.getKda());
                        }

                        result.put("team1", kdaMapTeam1);
                        result.put("team2", kdaMapTeam2);
                    });
                });
        double avg1 = team1avg.get();
        double avg2 = team2avg.get();
        if(playerTeamId==100 && avg1<avg2) {
            System.out.println("You are in loosers que");
        } else if(playerTeamId==200 && avg2<avg1) {
            System.out.println("You are in loosers que");
        } else {
            System.out.println("You are not in loosers que");
        }

        System.out.println(avg1/5);
        System.out.println(avg2/5);

        result.forEach((team, playersKdaMap) -> {
            System.out.println("Drużyna: " + team + playersKdaMap);
            playersKdaMap.forEach((pid, kda) -> {
                System.out.println("  Gracz: " + pid + " -> " + kda);
            });
        });
    }


}
