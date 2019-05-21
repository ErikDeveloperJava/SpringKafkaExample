package module.accept.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import module.common.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageKafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageKafkaService.class);

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Getter
    @Setter
    private Optional<List<Message>> optionalMessageList = Optional.empty();

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Value("${kafka.sign.topic}")
    private String KafkaSignTopic;

    @Value("${kafka.accept.topic}")
    private String KafkaAcceptTopic;

    @SneakyThrows
    public void sendMessage(Message message){
        String messageJson = objectMapper.writeValueAsString(message);
        //here message is sending to MessageSaveModule for save through kafka which have waited for that topic
        String value = kafkaTemplate.send(kafkaTopic, messageJson).completable().get().getProducerRecord().value();
        LOGGER.info("received : " + value);
    }

    public void sendSignMessage(){
        LOGGER.info("sent sign message");
        //here send sign message for returning message list
        kafkaTemplate.send(KafkaSignTopic,"send message list");
    }

    // this method accepts message list
    @SneakyThrows
    @KafkaListener(topics = "message_accept_topic",groupId = "message_accept_group_id")
    public void acceptMessage(String messageListJson){
        LOGGER.info("accept message list : " + messageListJson);
        List<Message> messageList = objectMapper.readValue(messageListJson, List.class);
        optionalMessageList = Optional.of(messageList);
    }
}
