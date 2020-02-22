package com.sapient.football.football.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Request {

    @JsonProperty("country")
    private String countryName;

    @JsonProperty("league")
    private String leagueName;

    @JsonProperty("team")
    private String teamName;
}
