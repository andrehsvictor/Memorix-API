package andrehsvictor.memorix.card;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardProcessorFactory {

    private final Map<String, CardProcessor> processors;

    public CardProcessor create(CardType cardType) {
        switch (cardType) {
            case FLASHCARD:
                return processors.get("flashcardProcessor");
            case MULTIPLE_CHOICE:
                return processors.get("multipleChoiceCardProcessor");
            case BOOLEAN:
                return processors.get("booleanCardProcessor");
            default:
                throw new IllegalArgumentException("Invalid card type");
        }
    }

}
