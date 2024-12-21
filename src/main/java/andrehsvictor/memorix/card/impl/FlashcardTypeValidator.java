package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.CardTypeValidator;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class FlashcardTypeValidator implements CardTypeValidator {

    @Override
    public void validate(PostCardDto postCardDto) {
        if (postCardDto.getAnswer() == null) {
            throw new MalformedRequestException("Answer is required for flashcard type");
        }
    }

}
