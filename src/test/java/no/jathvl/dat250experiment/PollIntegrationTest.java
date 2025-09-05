package no.jathvl.dat250experiment;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PollIntegrationTest {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();

    @Test
    @Order(1)
    void canCreateUser() {
        var res = restClient.post()
                .uri("/user/create")
                .body("{ \"username\": \"Alice\", \"email\": \"alice@example.com\" }")
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(2)
    void canListFirstUser() {
        var res = restClient.get()
                .uri("/user/all")
                .retrieve()
                .toEntity(String.class);

        assertEquals("[{\"id\":1,\"username\":\"Alice\",\"email\":\"alice@example.com\"}]", res.getBody());
    }

    @Test
    @Order(3)
    void canCreateSecondUser() {
        var res = restClient.post()
                .uri("/user/create")
                .body("{ \"username\": \"Bob\", \"email\": \"bob@example.com\" }")
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(4)
    void canListBothUsers() {
        var res = restClient.get()
                .uri("/user/all")
                .retrieve()
                .toEntity(String.class);

        assertEquals("[{\"id\":1,\"username\":\"Alice\",\"email\":\"alice@example.com\"},{\"id\":2,\"username\":\"Bob\",\"email\":\"bob@example.com\"}]",
                res.getBody());
    }

    @Test
    @Order(5)
    void canCreatePoll() {
        var res = restClient.post()
                .uri("/poll/create")
                .body("{ \"creatorUserId\": 1, \"question\": \"Do you like music?\", \"durationHours\": 2, \"options\": [\"yes\", \"no\"] }")
                .retrieve()
                .toEntity(String.class);

        System.out.println(res.getBody());
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(6)
    void canListPolls() {
        var res = restClient.get()
                .uri("/poll/all")
                .retrieve()
                .toEntity(String.class);

        System.out.println(res.getBody());
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(7)
    void canVoteAndChangeIt() {
        var res = restClient.put()
                .uri("/vote")
                .body("{ \"userId\": \"2\", \"voteOptionId\": \"1\" }")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, res.getStatusCode());

        var res2 = restClient.put()
                .uri("/vote")
                .body("{ \"userId\": \"2\", \"voteOptionId\": \"2\" }")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, res2.getStatusCode());
    }

    @Test
    @Order(8)
    void canListPollsWithVote() {
        var res = restClient.get()
                .uri("/poll/all")
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().contains("{\"id\":2,\"caption\":\"no\",\"presentationOrder\":1,\"votes\":[{\"id\":2"));
    }

    @Test
    @Order(9)
    void canDeletePoll() {
        var res = restClient.delete()
                .uri("/poll/1")
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(10)
    void canListNoPolls() {
        var res = restClient.get()
                .uri("/poll/all")
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("[]", res.getBody());
    }
}
