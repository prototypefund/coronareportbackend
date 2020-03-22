package de.wevsvirushackathon.coronareport.diary;

import de.wevsvirushackathon.coronareport.user.Client;
import de.wevsvirushackathon.coronareport.user.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class DiaryEntryControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DiaryEntryRepository DiaryEntryRepository;

    @Test
    public void givenEmployees_whenGetEmployees_thenStatus200()
            throws Exception {

        createTestData();

        MvcResult result = mvc.perform(get("/dep/1/diary_entry/csv").header("Origin","*")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(new MediaType("text", "csv")))
                .andReturn();

        Assertions.assertEquals(
                new Scanner(new File("src/test/resources/expected_csv.csv")).useDelimiter("\\Z").next().trim(),
                result.getResponse().getContentAsString().trim());
    }

    private void createTestData() {
        final Client client = Client.builder().firstname("Bob").surename("Korona").healthDepartmentId("1").build();
        clientRepository.save(client);
        DiaryEntryRepository.save(DiaryEntry.builder().client(client)
                .dateTime(Timestamp.valueOf("2020-01-10 00:00:00")).bodyTemperature(23).build());
        DiaryEntryRepository.save(DiaryEntry.builder().client(client)
                .dateTime(Timestamp.valueOf("2020-01-10 00:00:00")).bodyTemperature(30).build());

        final Client client2 = Client.builder().firstname("Alice").surename("Wonderland").healthDepartmentId("2").build();
        clientRepository.save(client2);
        DiaryEntryRepository.save(DiaryEntry.builder().client(client)
                .dateTime(Timestamp.valueOf("2020-01-10 00:00:00")).bodyTemperature(23).build());
        DiaryEntryRepository.save(DiaryEntry.builder().client(client)
                .dateTime(Timestamp.valueOf("2020-01-11 00:00:00")).bodyTemperature(23).build());
    }

    public Date dateOf(int year, int month, int day) {
        return java.util.Date.from(LocalDate.of(year, month, day).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}