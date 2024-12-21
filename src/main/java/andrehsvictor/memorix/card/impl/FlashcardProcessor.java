package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardProcessor;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class FlashcardProcessor implements CardProcessor {

    @Override
    public void process(Card card) {
        if (card.getAnswer() == null) {
            throw new MalformedRequestException("Answer is required for flashcard type");
        }
        card.setCorrect(null);
        card.setAlternatives(null);
        card.setAnswerIndex(null);
    }

}
