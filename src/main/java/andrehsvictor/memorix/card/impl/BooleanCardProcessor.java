package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardProcessor;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class BooleanCardProcessor implements CardProcessor {

    @Override
    public void process(Card card) {
        if (card.getCorrect() == null || !(card.getCorrect() instanceof Boolean)) {
            throw new MalformedRequestException("Correct field is required for boolean type");
        }
        card.setAnswer(null);
        card.setAlternatives(null);
        card.setAnswerIndex(null);
    }

}
