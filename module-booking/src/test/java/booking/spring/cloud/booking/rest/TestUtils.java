package booking.spring.cloud.booking.rest;

import booking.spring.cloud.core.model.dto.JwtAuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

public final class TestUtils {

    private TestUtils() {}

    @SneakyThrows
    public static String signUp(MockMvc mockMvc, String login, String password) {
        final var answerSignUp = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/up")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\""+login+"\", \"password\":\""+password+"\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        return getTokenFromAnswer(answerSignUp);
    }

    @SneakyThrows
    public static String signIn(MockMvc mockMvc, String login, String password) {
        final var answerSignIn = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/in")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\""+login+"\", \"password\":\""+password+"\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        return getTokenFromAnswer(answerSignIn);
    }

    @SneakyThrows
    private static String getTokenFromAnswer(final byte[] answer) {
        final var str = new String(answer, StandardCharsets.UTF_8);
        final var objectMapper = new ObjectMapper();
        try (final var parser = objectMapper.createParser(str)) {
            final var jwt = parser.readValueAs(JwtAuthenticationResponse.class);
            return jwt.token();
        }
    }
}
