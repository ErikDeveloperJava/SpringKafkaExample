package module.message.save.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import module.common.model.Message;

public interface MessageRepository extends JpaRepository<Message,Integer> {
}
