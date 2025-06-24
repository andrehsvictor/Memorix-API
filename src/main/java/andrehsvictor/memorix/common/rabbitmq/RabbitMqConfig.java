package andrehsvictor.memorix.common.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }

    @Bean
    Queue emailActionsVerifyEmailQueue() {
        return new Queue("email-actions.v1.verify-email", true);
    }

    @Bean
    Queue emailActionsResetPasswordQueue() {
        return new Queue("email-actions.v1.reset-password", true);
    }

    @Bean
    Queue emailActionsChangeEmailQueue() {
        return new Queue("email-actions.v1.change-email", true);
    }

    @Bean
    Queue cardsDeleteQueue() {
        return new Queue("cards.v1.delete", true);
    }

    @Bean
    Queue cardsReviewQueue() {
        return new Queue("cards.v1.review", true);
    }

    @Bean
    Queue cardsDeleteAllByUserIdQueue() {
        return new Queue("cards.v1.deleteAllByUserId", true);
    }

    @Bean
    Queue decksDeleteQueue() {
        return new Queue("decks.v1.delete", true);
    }

    @Bean
    Queue usersDeleteQueue() {
        return new Queue("users.v1.delete", true);
    }

    @Bean
    Queue reviewsDeleteAllByCardIdQueue() {
        return new Queue("reviews.v1.deleteAllByCardId", true);
    }

    @Bean
    Queue minioDeleteUrlQueue() {
        return new Queue("minio.v1.delete.url", true);
    }

    @Bean
    Queue minioDeleteMetadataQueue() {
        return new Queue("minio.v1.delete.metadata", true);
    }

}