package com.sapient.football.football;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sapient.football.football.exceptions.NoDataFoundException;
import com.sapient.football.football.request.Request;
import com.sapient.football.football.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class AppService implements Service {

    @Autowired
    RestTemplate restTemplate;

    @Value("${footall.service.url}")
    private String footballServiceURL;


    @Override
    public FinalResponse getTeamStandings(Request json) throws JsonProcessingException, NoDataFoundException {

        Country countryData = new Country();
        League leagueData = new League();
        Teams teamData = new Teams();
        Standings rankingData = new Standings();

        //call Country API to get league ID
        try {
            ResponseEntity<Country[]> countryResponse =
                    restTemplate.getForEntity(footballServiceURL + "?action=get_countries&APIkey=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978",
                            Country[].class);

            List<Country> countries = Arrays.stream(countryResponse.getBody()).filter(c -> c.getCountryName().equals(json.getCountryName())).collect(Collectors.toList());

            countryData = countries.stream().findFirst().get();

            ResponseEntity<League[]> leagueResponse =
                restTemplate.exchange(footballServiceURL + "?action=get_leagues&country_id=" + countryData.getCountryId() + "&APIkey=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978", HttpMethod.GET, getObjectHttpEntity(), League[].class);
        List<League> leagues = Arrays.stream(leagueResponse.getBody()).filter(c -> c.getLeagueName().equals(json.getLeagueName())).collect(Collectors.toList());

        leagueData = leagues.stream().findFirst().get();

        //call teams API to get details
        ResponseEntity<Teams[]> teamsResponse =
                restTemplate.exchange(footballServiceURL + "?action=get_teams&league_id=" + leagueData.getLeagueId() + "&APIkey=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978", HttpMethod.GET, getObjectHttpEntity(), Teams[].class);

        List<Teams> teams = Arrays.stream(teamsResponse.getBody()).filter(t -> t.getTeamName().equals(json.getTeamName())).collect(Collectors.toList());

        teamData = teams.stream().findFirst().get();

        ResponseEntity<Standings[]> standingsResponse =
                restTemplate.exchange(footballServiceURL + "?action=get_standings&league_id=" + leagueData.getLeagueId() + "&APIkey=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978", HttpMethod.GET, getObjectHttpEntity(), Standings[].class);

        List<Standings> rankings = Arrays.stream(standingsResponse.getBody()).filter(s -> s.getTeamName().equals(json.getTeamName())).collect(Collectors.toList());
        rankingData = rankings.stream().findFirst().get();

    }catch (NoSuchElementException e){
            throw new NoDataFoundException("The Country Name/League Name or the team name are incorrect");
        }
        /*ResponseEntity<Standings> standingsResponse = restTemplate.getForEntity(footballServiceURL+"?action=get_standings&league_id=149&APIkey=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978",
                Standings.class);*/

        return findRankOfTeam(countryData, leagueData, teamData, rankingData);
    }

    private HttpEntity<Object> getObjectHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private FinalResponse findRankOfTeam(Country countryResponse, League leagueResponse, Teams teamsResponse, Standings standingsResponse) throws JsonProcessingException {
        FinalResponse response = new FinalResponse();
        response.setCountryId(countryResponse.getCountryId());
        response.setCountryName(countryResponse.getCountryName());
        response.setLeagueId(leagueResponse.getLeagueId());
        response.setLeagueName(leagueResponse.getLeagueName());
        response.setTeamId(teamsResponse.getTeamKey());
        response.setTeamName(teamsResponse.getTeamName());
        response.setOverAllLeaguePosition(standingsResponse.getOverallLeaguePosition());

        return response;
    }

}
