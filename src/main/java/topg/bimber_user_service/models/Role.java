package topg.bimber_user_service.models;

import lombok.Getter;

@Getter
public enum Role {
    USER("Can search for available rooms and make bookings"),
    ADMIN("Can manage hotels, rooms, and bookings");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getRole() {
        return this.name();
    }
}
