package com.tomattman.beerTapDispenser.web;

import com.tomattman.beerTapDispenser.model.Dispenser;
import com.tomattman.beerTapDispenser.model.DispenserUsage;
import com.tomattman.beerTapDispenser.repository.DispenserRepository;
import com.tomattman.beerTapDispenser.repository.DispenserUsageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DispenserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DispenserRepository dispenserRepository;
    @Autowired
    private DispenserUsageRepository dispenserUsageRepository;

    @Test
    void createDispenser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/dispenser")
                        .content("{\n" +
                                "  \"flow_volume\": 0.0653\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flow_volume").value(0.0653));
    }

    @Test
    void changeDispenserStatus() throws Exception {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.saveAndFlush(dispenser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/dispenser/{id}/status", dispenser.getId())
                        .content("{\n" +
                                "  \"status\": \"open\",\n" +
                                "  \"updated_at\": \"2022-01-01T02:00:00Z\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(202));
    }

    @Test
    void changeDispenserStatus_statusCode409_throwStatusException() throws Exception {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.saveAndFlush(dispenser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/dispenser/{id}/status", dispenser.getId())
                        .content("{\n" +
                                "  \"status\": \"close\",\n" +
                                "  \"updated_at\": \"2022-01-01T02:00:00Z\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    void getStatistic() throws Exception {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.saveAndFlush(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setDispenser(dispenser);
        usage.setOpenTime(LocalDateTime.now());
        usage.setCloseTime(LocalDateTime.now());
        usage.setTotalSpent(BigDecimal.valueOf(11.11));
        usage.setFlowVolume(1.1);

        dispenserUsageRepository.saveAndFlush(usage);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/dispenser/{id}/spending", dispenser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(11.11))
                .andExpect(MockMvcResultMatchers.jsonPath("$.usages[0].opened_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.usages[0].closed_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.usages[0].flow_volume").value(1.1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.usages[0].total_spent").value(11.11));

    }
    @Test
    void getStatistic_statusCode404_dispenserNotFound() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/dispenser/{id}/spending", 111)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }
}



