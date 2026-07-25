package com.professional.b07legendaryproject2026.data;

public enum PeriodNum {
    UNKNOWN(0, "Unknown Dynasty"),
    SHANG(1, "Shang Dynasty (c. 1600-1046 BCE)"),
    WESTERN_ZHOU(2, "Western Zhou Dynasty (c. 1046-771 BCE)"),
    EASTERN_ZHOU(3, "Eastern Zhou Dynasty (770-256 BCE)"),
    QIN(4, "Qin Dynasty (221-206 BCE)"),
    HAN(5, "Han Dynasty (206 BCE-220 CE)"),
    THREE_KINGDOMS(6, "Three Kingdoms Period (220-280 CE)"),
    JIN1(7, "Jin Dynasty (266-420 CE)"),
    SOUTHERN_AND_NORTHERN(8, "Southern and Northern Dynasties (420-589 CE)"),
    SUI(9, "Sui Dynasty (581-618 CE)"),
    TANG(10, "Tang Dynasty (618-907 CE)"),
    FIVE_DYNASTIES_AND_TEN_KINGDOMS(11, "Five Dynasties and Ten Kingdoms (907-960 CE)"),
    SONG(12, "Song Dynasty (960-1279 CE)"),
    LIAO(13, "Liao Dynasty (907-1125 CE)"),
    JIN2(14, "Jin Dynasty (1115-1234 CE)"),
    YUAN(15, "Yuan Dynasty (1271-1368 CE)"),
    MING(16, "Ming Dynasty (1368-1644 CE)"),
    QING(17, "Qing Dynasty (1644-1912 CE)"),
    REPUBLIC_OF_CHINA(18, "Republic of China Period (1912-1949 CE)");

    private final int id;
    private final String description;

    PeriodNum(int id, String description) {
        if (id > 0 && id < 19) {
            this.id = id;
        } else {
            this.id = 0;
        }
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return this == UNKNOWN ? -1 : id - 1;
    }

    public static PeriodNum fromId(int id) {
        for (PeriodNum value : values()) {
            if (value != UNKNOWN && value.id - 1 == id) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
