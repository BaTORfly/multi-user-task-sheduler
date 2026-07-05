package io.github.batorfly.config_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.server.git.clone-on-start=false",
        "spring.cloud.config.server.git.uri=file:./config"
})
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
