package com.professional.b07legendaryproject2026.data;

public enum CategoryNum {
    UNKNOWN(0, "Unknown Category"),
    PAINTING_AND_CALIGRAPHY(1, "Painting and Calligraphy"),
    CERAMICS(2, "Ceramics"),
    BRONZEWARE(3, "Bronze Ware"),
    LACQUERWARE(4, "Lacquerware"),
    JADEWARE(5, "Jade Ware"),
    ENAMELWARE(6, "Enamel Ware"),
    GLASSWARE(7, "Glassware"),
    FURNITURE(8, "Furniture"),
    EMBROIDERY_AND_TEXTILES(9, "Embroidery and Textiles"),
    DOCUMENTS_AND_ARCHIVES(10, "Documents and Archives"),
    GOLD_AND_SILVERWARE(11, "Gold and Silverware"),
    CLOCKS_AND_WATCHES(12, "Clocks and Watches"),
    RELIGIOUS_ARTIFACTS(13, "Religious Artifacts"),
    DAILYUSE_ITEMS(14, "Daily-use Items"),
    WEAPONRY(15, "Weaponry"),
    MISCELLANEOUS(16, "Miscellaneous Artifacts");

    private final int id;
    private final String displayName;

    CategoryNum(int id, String displayName) {
        if (id > 0 && id < 17) {
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

    public static CategoryNum fromId(int id) {
        for (CategoryNum value : values()) {
            if (value != UNKNOWN && value.id - 1 == id) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
