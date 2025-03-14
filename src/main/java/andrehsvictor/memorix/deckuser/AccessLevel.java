package andrehsvictor.memorix.deckuser;

public enum AccessLevel {
    OWNER, EDITOR, VIEWER;

    public static AccessLevel fromString(String accessLevel) {
        if (accessLevel == null) {
            return null;
        }
        return AccessLevel.valueOf(accessLevel.toUpperCase().trim().replace(" ", "_"));
    }
}