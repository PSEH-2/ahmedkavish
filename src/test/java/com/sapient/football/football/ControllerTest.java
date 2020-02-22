package com.sapient.football.football;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sapient.football.football.exceptions.NoDataFoundException;
import com.sapient.football.football.request.Request;
import com.sapient.football.football.response.FinalResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ControllerTest {

    @InjectMocks
    AppController controller;
    @Mock
    Service service;
    @Test
    void contextLoads() {
    }

    @Test
    void testController() throws JsonProcessingException, NoDataFoundException {
        FinalResponse finalResponse = new FinalResponse();

        when(service.getTeamStandings(any())).thenReturn(new FinalResponse());
        finalResponse = controller.getTeamStandings(new Request());
        Assert.notNull(finalResponse);
    }
}
