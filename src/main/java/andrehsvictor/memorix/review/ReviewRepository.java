package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, UUID> {

}
