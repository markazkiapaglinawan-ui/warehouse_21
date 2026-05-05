public enum UserRole {
    ADMIN,
    STAFF,
    CUSTOMER;

    public String getDisplayName() {
        return switch (this) {
            case ADMIN -> "Admin";
            case STAFF -> "Staff";
            case CUSTOMER -> "Customer";
        };
    }
}
