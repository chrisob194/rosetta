package io.rosetta.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RosettaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEntities_returns200WithEntityList() throws Exception {
        mockMvc.perform(get("/rosetta/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entities").isArray())
                .andExpect(jsonPath("$.entities[0].name").value("OrderEntity"));
    }

    @Test
    void getEntities_orderEntityHasCorrectStructure() throws Exception {
        mockMvc.perform(get("/rosetta/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entities[0].table").value("orders"))
                .andExpect(jsonPath("$.entities[0].inscription").value("Represents a customer order"))
                .andExpect(jsonPath("$.entities[0].fields").isArray());
    }

    @Test
    void getEntity_returnsOrderByName() throws Exception {
        mockMvc.perform(get("/rosetta/entities/OrderEntity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("OrderEntity"))
                .andExpect(jsonPath("$.table").value("orders"));
    }

    @Test
    void getEntity_returns404ForUnknownName() throws Exception {
        mockMvc.perform(get("/rosetta/entities/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEntities_idFieldIsPrimaryKey() throws Exception {
        mockMvc.perform(get("/rosetta/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entities[0].fields[?(@.name == 'id')].primaryKey").value(true))
                .andExpect(jsonPath("$.entities[0].fields[?(@.name == 'id')].nullable").value(false));
    }

    @Test
    void getEntities_statusFieldHasGlyph() throws Exception {
        mockMvc.perform(get("/rosetta/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entities[0].fields[?(@.name == 'status')].glyph")
                        .value("Order status: PENDING, CONFIRMED, SHIPPED"));
    }
}
