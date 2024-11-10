package dns.wiremock.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DemoServiceClient {

    private final RestClient restClient;

    public DemoServiceClient(RestClient.Builder restClientBuilder,
                             @Value("${demo-service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public String getSimpleResponseDemo() {
        return restClient.get().uri("/api/simple-response").retrieve().body(String.class);
    }

    public String getEchoDemo(String id) {
        return restClient.get().uri("/api/echo/%s".formatted(id)).retrieve().body(String.class);
    }

    public String createJwtDemo() {
        return restClient.get().uri("/api/jwt").retrieve().body(String.class);
    }

    public void timeoutDemo() {
        restClient.get().uri("/api/timeout").retrieve().toBodilessEntity();
    }

    public HttpStatusCode stubHttpStatusDemo(HttpStatus status) {
        return restClient.get().uri("/api/http-status/%d".formatted(status.value())).retrieve().toBodilessEntity().getStatusCode();
    }

    public String importBodyFileDemo() {
        return restClient.get().uri("/api/import-body-file").retrieve().body(String.class);
    }
}
