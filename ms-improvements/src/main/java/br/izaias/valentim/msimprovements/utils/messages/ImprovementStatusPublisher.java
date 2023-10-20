package br.izaias.valentim.msimprovements.utils.messages;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.entities.messageModel.ImprovementToSendStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ImprovementStatusPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue queueStatusPublisher;

    public ImprovementStatusPublisher(RabbitTemplate rabbitTemplate, Queue queueStatusPublisher) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueStatusPublisher = queueStatusPublisher;
    }

    public void sendStatus(
            Improvement improvementToSend,
            Integer votesApproved,
            Integer votesRejected) throws JsonProcessingException {

        ImprovementToSendStatus createImprovementToSend = new ImprovementToSendStatus(

                improvementToSend.getName(),
                improvementToSend.getDescription(),
                improvementToSend.getResult().toString(),
                votesApproved,
                votesRejected
        );

        String statusOnJson = convertIntoJson(createImprovementToSend);
        rabbitTemplate.convertAndSend(queueStatusPublisher.getName(), statusOnJson);
    }

    private String convertIntoJson(ImprovementToSendStatus dataToJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String dataOnJson = mapper.writeValueAsString(dataToJson);
        return dataOnJson;
    }
}
