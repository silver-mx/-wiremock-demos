package dns.wiremock.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock({
        @ConfigureWireMock(name = "demo-service",
                filesUnderClasspath = "wiremock/demo-service",
                baseUrlProperties = "demo-service.base-url",
                port = 8045)
})
@SpringBootTest
class DemoServiceClientTest {

    @Autowired
    private Environment env;

    @Autowired
    private DemoServiceClient demoServiceClient;

    @InjectWireMock("demo-service")
    private WireMockServer demoServiceWireMock;

    @Test
    void manualWiremockStub() throws JSONException {
        demoServiceWireMock.stubFor(
                get("/api/simple-response")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBodyFile("simple-response.json")
                        ));

        String response = demoServiceClient.getSimpleResponseDemo();

        JSONAssert.assertEquals("""
                        {
                          "firstName": "TheName",
                          "lastName": "TheLastName"
                        }
                """, response, true);
    }

    @Test
    void echoDemoUsingAutomaticMappings() throws JSONException {
        String id = UUID.randomUUID().toString();
        String response = demoServiceClient.getEchoDemo(id);
        JSONAssert.assertEquals(response, "{\"echo\":\"%s\"}".formatted(id), true);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"OK", "NO_CONTENT"})
    void httpStatusCodeDemoUsingAutomaticMappings(HttpStatus status) {
        HttpStatusCode response = demoServiceClient.stubHttpStatusDemo(status);
        assertThat(response).isEqualTo(status);
    }

    @Test
    void importBodyFileDemoUsingAutomaticMappings() throws JSONException {
        String response = demoServiceClient.importBodyFileDemo();
        JSONAssert.assertEquals(response, "{\"description\":\"Import body file in Wiremock\"}", true);
    }
}