package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardProcessor;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class BooleanCardProcessor implements CardProcessor {

    @Override
    public void process(Card card) {
        if (card.getBooleanAnswer() == null || card.getBooleanAnswer().toString().isEmpty()) {
            throw new MalformedRequestException("Answer is required for boolean type");
        }
        card.setAnswer(null);
        card.setOptions(null);
        card.setCorrectOptionIndex(null);
    }

}
