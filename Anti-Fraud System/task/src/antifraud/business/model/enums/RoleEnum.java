package antifraud.business.model.enums;

public enum RoleEnum {
    ADMINISTRATOR("Administrator Role"),
    MERCHANT("Basic Merchant Role"),
    SUPPORT("Special Support Role");

    private final String description;

    private RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
