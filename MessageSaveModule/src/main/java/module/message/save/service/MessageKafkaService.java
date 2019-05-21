package module.message.save.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import module.common.model.Message;
import module.message.save.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageKafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageKafkaService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.send.message.topic}")
    private String sendMessagesTopic;

    //this method accepts message and save to database
    @SneakyThrows
    @KafkaListener(topics = "message_topic",groupId = "group_id")
    public String acceptMessage(String messageJson){
        LOGGER.info("message accepted : " + messageJson);
        Message message = objectMapper.readValue(messageJson, Message.class);
        messageRepository.save(message);
        return "message saved successfully";
    }

    //this method accepts sign message and send all message list to MessageAcceptModule
    @SneakyThrows
    @KafkaListener(topics = "message_sign_topic",groupId = "group_id")
    public void acceptSignMessage(String message){
        LOGGER.info("accepted sign message : " + message);
        List<Message> messageList = messageRepository.findAll();
        String messagesJson = objectMapper.writeValueAsString(messageList);
        kafkaTemplate.send(sendMessagesTopic,messagesJson);
    }
}
