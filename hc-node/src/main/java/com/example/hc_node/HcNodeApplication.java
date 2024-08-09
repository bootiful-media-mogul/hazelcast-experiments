package com.example.hc_node;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.GeneratedBuildProperties;
import com.hazelcast.internal.networking.nio.NioOutboundPipeline;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ImportRuntimeHints(HcNodeApplication.Hints.class)
@SpringBootApplication
public class HcNodeApplication {

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            for (var c : new Class<?>[]{NioOutboundPipeline.class, GeneratedBuildProperties.class})
                hints.reflection().registerType(c, MemberCategory.values());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(HcNodeApplication.class, args);
    }

}


/**
 * read: http://localhost:8080/read?key=name
 * write: http://localhost:8080/write?key=name&value=josh
 */

@ResponseBody
@Controller
class ClientController {

    private final Client client;

    ClientController(Client client) {
        this.client = client;
    }

    @GetMapping("/write")
    String write(@RequestParam String key, @RequestParam String value) {
        this.client.write(key, value);
        return this.client.read(key);
    }


    @GetMapping("/read")
    String read(@RequestParam String key) {
        return this.client.read(key);
    }


}

@Configuration
class HazlecastClientConfiguration {

    @Bean(destroyMethod = "shutdown")
    HazelcastInstance HazelcastInstanceClientConfig(
            @Value("${hazelcast.cluster.name}") String clusterName) {
        var clientConfig = new ClientConfig();
        clientConfig.setClusterName(clusterName);
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
        return HazelcastClient
                .newHazelcastClient(clientConfig);
    }

    @Bean
    Client client(HazelcastInstance hz) {
        var map = hz.getMap("mogul");
        return new Client(map);
    }

}


class Client {

    private final Map<Object, Object> messages;

    Client(Map<Object, Object> messages) {
        this.messages = messages;
    }

    void write(String k, String v) {
        this.messages.put(k, v);
    }

    String read(String k) {
        return (String) this.messages.get(k);
    }
}
