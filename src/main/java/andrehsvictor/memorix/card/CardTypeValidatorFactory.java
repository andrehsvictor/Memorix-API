package andrehsvictor.memorix.card;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardTypeValidatorFactory {

    private final Map<String, CardTypeValidator> validators;

    public CardTypeValidator get(CardType cardType) {
        switch (cardType) {
            case FLASHCARD:
                return validators.get("flashcardTypeValidator");
            case MULTIPLE_CHOICE:
                return validators.get("multipleChoiceTypeValidator");
            case BOOLEAN:
                return validators.get("booleanTypeValidator");
            default:
                throw new IllegalArgumentException("Invalid card type");
        }
    }

}
