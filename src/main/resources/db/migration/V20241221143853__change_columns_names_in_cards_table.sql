ALTER TABLE cards
    RENAME COLUMN options TO alternatives;
ALTER TABLE cards
    RENAME COLUMN correct_option_index TO answer_index;