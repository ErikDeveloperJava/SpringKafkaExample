package module.message.save;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("module.common.model")
@SpringBootApplication
public class MessageSaveModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageSaveModuleApplication.class, args);
    }

}
