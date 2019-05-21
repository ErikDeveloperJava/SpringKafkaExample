package module.accept.message.controller;

import module.accept.message.service.MessageKafkaService;
import module.common.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageKafkaService messageSenderService;


    //This method accepted request and send message to MessageSaveModule for save
    @PostMapping
    public ResponseEntity save(@RequestBody Message message){
        messageSenderService.sendMessage(message);
        LOGGER.info("message sent successfully : " + message);
        return ResponseEntity
                .ok("message sent successfully");
    }

    //This method at first send message to MessageSaveModule then it return messageList through kafka
    @GetMapping
    public ResponseEntity getAll(){
        messageSenderService.sendSignMessage();
        while (!messageSenderService.getOptionalMessageList().isPresent()){}
        List<Message> messages = messageSenderService.getOptionalMessageList().get();
        messageSenderService.setOptionalMessageList(Optional.empty());
        return ResponseEntity.ok(messages);
    }
}
