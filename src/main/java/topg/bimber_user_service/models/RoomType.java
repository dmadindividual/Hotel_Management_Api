package topg.bimber_user_service.models;

import lombok.Getter;

@Getter
public enum RoomType {
    SINGLE("A room assigned to one person. May have one or more beds."),
    DOUBLE("A room assigned to two people. May have one double bed or two single beds."),
    SUITE("A set of rooms with luxurious amenities, often including a living area."),
    DELUXE("A premium room offering additional comfort and upscale features.");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }

}
