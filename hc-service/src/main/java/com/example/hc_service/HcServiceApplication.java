package com.example.hc_service;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class HcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HcServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner hazelcast (){
        var config = new Config () ;
        config.setClusterName("mogul") ;
        return args -> Hazelcast. newHazelcastInstance( config);
    }

 /*   @Bean
    HazelcastLifecycle mogulHazlecastCluster(@Value("${hazelcast.cluster.name}") String clusterName) {
        return new HazelcastLifecycle(clusterName);
    }*/
}
/*

*/
/**
 * ideal for running a Hazelcast-as-a-service bootstrapped within Spring.
 *//*

class HazelcastLifecycle implements SmartLifecycle {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AtomicReference<Config> config = new AtomicReference<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final AtomicReference<HazelcastInstance> instance = new AtomicReference<>();


    HazelcastLifecycle(String clusterName) {

        // Create a new Hazelcast configuration
        var config = new Config();

        // Configure network settings
        var networkConfig = config.getNetworkConfig();
        networkConfig.setPort(5701); // Set the port
        networkConfig.setPortAutoIncrement(false); // Disable auto-increment

        // Optionally configure other settings such as interfaces, join mechanisms, etc.
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig()
                .setEnabled(true)
                .addMember("127.0.0.1"); // Use TCP/IP join with localhost

        // Start the Hazelcast instance


        this.config.set(config);

    }


    @Override
    public void start() {
        this.log.debug("start(). running? {}", this.running.get());
        Assert.notNull(this.config.get(), "the configuration should not be null");
        if (this.running.compareAndSet(false, true)) {
            this.log.debug("wasn't running, but will be soon..");
            this.instance.set(Hazelcast.newHazelcastInstance(this.config.get()));
            this.log.debug("started Hazelcast instance");
        } //
        else {
            this.log.debug("couldn't exchange to running.");
        }
    }

    @Override
    public void stop() {
        Assert.notNull(this.config.get(), "the configuration should not be null");
        if (this.running.compareAndSet(true, false)) {
            this.instance.get().shutdown();
            this.log.debug("stopped Hazelcast instance");
        }
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }


}*/
