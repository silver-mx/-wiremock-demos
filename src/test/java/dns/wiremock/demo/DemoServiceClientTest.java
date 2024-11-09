package dns.wiremock.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@EnableWireMock({
        @ConfigureWireMock(name = "demo-service", baseUrlProperties = "demo-service.base-url", port = 8045)
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
    void simpleWiremockServiceResponse() throws JSONException {
        demoServiceWireMock.stubFor(
                get("/simple-response")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBodyFile("demo-service/simple-response.json")
                        ));

        String response = demoServiceClient.getSimpleResponseDemo();

        JSONAssert.assertEquals("""
                        {
                          "firstName": "TheName",
                          "lastName": "TheLastName"
                        }
                """, response, true);
    }
}