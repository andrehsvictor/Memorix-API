package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardProcessor;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class MultipleChoiceCardProcessor implements CardProcessor {

    @Override
    public void process(Card card) {
        if (card.getOptions() == null) {
            throw new MalformedRequestException("Options are required for multiple choice type");
        }
        if (card.getCorrectOptionIndex() == null) {
            throw new MalformedRequestException("Correct option index is required for multiple choice type");
        }
        if (card.getCorrectOptionIndex() < 0
                || card.getCorrectOptionIndex() >= card.getOptions().size()) {
            throw new MalformedRequestException("Correct option index is out of bounds");
        }
        card.setAnswer(null);
        card.setBooleanAnswer(null);
    }

}
