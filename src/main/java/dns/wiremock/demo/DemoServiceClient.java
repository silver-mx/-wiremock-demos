package dns.wiremock.demo;

import org.springframework.beans.factory.annotation.Value;
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
        return restClient.get().uri("/simple-response").retrieve().body(String.class);
    }
}
