package com.professional.b07legendaryproject2026.data;

public enum MaterialNum {
    UNKNOWN(0, "Unknown Material"),
    BRONZE(1, "Bronze"),
    STONE(2, "Stone"),
    WOOD(3, "Wood"),
    JADE(4, "Jade"),
    CERAMIC(5, "Ceramic"),
    LACQUERWARE(6, "Lacquerware"),
    IVORY(7, "Ivory"),
    GOLD(8, "Gold"),
    SILVER(9, "Silver"),
    IRON(10, "Iron"),
    MIXED_MEDIA(11, "Mixed Media");

    private final int id;
    private final String displayName;

    MaterialNum(int id, String displayName) {
        if (id > 0 && id < 12) {
            this.id = id;
        } else {
            this.id = 0;
        }
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getId() {
        return this == UNKNOWN ? -1 : id - 1;
    }

    public static MaterialNum fromId(int id) {
        for (MaterialNum value : values()) {
            if (value != UNKNOWN && value.id - 1 == id) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
