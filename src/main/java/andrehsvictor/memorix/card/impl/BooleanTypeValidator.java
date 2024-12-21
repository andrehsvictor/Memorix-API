package andrehsvictor.memorix.card.impl;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.card.CardTypeValidator;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.exception.MalformedRequestException;

@Component
public class BooleanTypeValidator implements CardTypeValidator {

    @Override
    public void validate(PostCardDto postCardDto) {
        if (postCardDto.getBooleanAnswer() == null) {
            throw new MalformedRequestException("Boolean answer is required for boolean type");
        }
    }

}
