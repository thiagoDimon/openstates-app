package com.openstates.app.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openstates.app.dto.PoliticianPageDTO;
import com.openstates.app.service.PoliticianService;

@WebMvcTest(PoliticianController.class)
class PoliticianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PoliticianService politicianService;

    @Test
    void findAll_withAllParams_returns200WithBody() throws Exception {
        PoliticianPageDTO dto = new PoliticianPageDTO(List.of(), 0, 10, false);
        when(politicianService.findAll("ca", "Democratic", 0, 10)).thenReturn(dto);

        mockMvc.perform(get("/api/politicians")
                .param("state", "ca")
                .param("party", "Democratic")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    void findAll_withNoParams_returns200WithDefaults() throws Exception {
        when(politicianService.findAll(null, null, 0, 10)).thenReturn(
                new PoliticianPageDTO(List.of(), 0, 10, false));

        mockMvc.perform(get("/api/politicians")).andExpect(status().isOk());
        verify(politicianService).findAll(null, null, 0, 10);
    }

    @Test
    void syncState_returns200() throws Exception {
        mockMvc.perform(post("/api/politicians/sync/ca")).andExpect(status().isOk());
        verify(politicianService).syncNextPageForState("ca");
    }
}
