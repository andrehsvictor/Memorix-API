package andrehsvictor.memorix.card.impl;

import andrehsvictor.memorix.card.CardTypeValidator;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.exception.MalformedRequestException;

public class MultipleChoiceTypeValidator implements CardTypeValidator {

    @Override
    public void validate(PostCardDto postCardDto) {
        if (postCardDto.getOptions() == null) {
            throw new MalformedRequestException("Options are required for multiple choice type");
        }
        if (postCardDto.getCorrectOptionIndex() == null) {
            throw new MalformedRequestException("Correct option index is required for multiple choice type");
        }
        if (postCardDto.getCorrectOptionIndex() < 0
                || postCardDto.getCorrectOptionIndex() >= postCardDto.getOptions().size()) {
            throw new MalformedRequestException("Correct option index is out of bounds");
        }
    }

}
