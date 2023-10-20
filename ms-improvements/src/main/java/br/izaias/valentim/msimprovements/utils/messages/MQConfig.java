package br.izaias.valentim.msimprovements.utils.messages;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    @Value("${mq.queues.monitoring-improvements-status}")
    private String queueToSend;

    @Bean
    public Queue queueStatusImprovement(){
        return new Queue(queueToSend,true);
    }

}
