package andrehsvictor.memorix.deck;

public enum DeckVisibility {
    PUBLIC, PRIVATE;

    public static DeckVisibility fromString(String visibility) {
        if (visibility == null) {
            return null;
        }
        return DeckVisibility.valueOf(visibility.toUpperCase().trim().replace(" ", "_"));
    }
}
