package com.professional.b07legendaryproject2026.data;

public class Artifact {
    // Mandatory fields
    private String lotNumber;
    private String name;
    private String description;
    private CategoryNum categoryNum;
    private MaterialNum materialNum;
    private PeriodNum periodNum;

    // Optional fields
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String image;

    public enum CategoryNum {
        UNKNOWN(0),
        PAINTING_AND_CALIGRAPHY(1),
        CERAMICS(2),
        BRONZEWARE(3),
        LACQUERWARE(4),
        JADEWARE(5),
        ENAMELWARE(6),
        GLASSWARE(7),
        FURNITURE(8),
        EMBROIDERY_AND_TEXTILES(9),
        DOCUMENTS_AND_ARCHIVES(10),
        GOLD_AND_SILVERWARE(11),
        CLOCKS_AND_WATCHES(12),
        RELIGIOUS_ARTIFACTS(13),
        DAILYUSE_ITEMS(14),
        WEAPONRY(15),
        MISCELLANEOUS(16);

        private final int id;

        CategoryNum(int id) {
            if(id>0&&id<17)
                this.id = id;
            else 
                this.id=0;
        }

        public int getId() {
            return id;
        }

        public static CategoryNum fromId(int id) {
            for (CategoryNum value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    public enum MaterialNum {
        UNKNOWN(0),
        BRONZE(1),
        STONE(2),
        WOOD(3),
        JADE(4),
        CERAMIC(5),
        LACQUERWARE(6),
        IVORY(7),
        GOLD(8),
        SILVER(9),
        IRON(10),
        MIXED_MEDIA(11);

        private final int id;

        MaterialNum(int id) {
            if(id>0&&id<12)
                this.id = id;
            else 
                this.id=0;
        }

        public int getId() {
            return id;
        }

        public static MaterialNum fromId(int id) {
            for (MaterialNum value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    public enum PeriodNum {
        UNKNOWN(0),
        SHANG(1),
        WESTERN_ZHOU(2),
        EASTERN_ZHOU(3),
        QIN(4),
        HAN(5),
        THREE_KINGDOMS(6),
        JIN1(7),
        SOUTHERN_AND_NORTHERN(8),
        SUI(9),
        TANG(10),
        FIVE_DYNASTIES_AND_TEN_KINGDOMS(11),
        SONG(12),
        LIAO(13),
        JIN2(14),
        YUAN(15),
        MING(16),
        QING(17),
        REPUBLIC_OF_CHINA(18);

        private final int id;

        PeriodNum(int id) {
            if(id>0&&id<19)
                this.id = id;
            else 
                this.id=0;
        }

        public int getId() {
            return id;
        }

        public static PeriodNum fromId(int id) {
            for (PeriodNum value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    public Artifact() {
        this.lotNumber = null;
        this.name = null;
        this.description = null;
        this.categoryNum = CategoryNum.UNKNOWN;
        this.materialNum = MaterialNum.UNKNOWN;
        this.periodNum = PeriodNum.UNKNOWN;

        this.culturalOrigin = null;
        this.dimensions = null;
        this.conditionReport = null;
        this.currentLocation = null;
        this.acquisitionMethod = null;
        this.provenance = null;
        this.accessionNumber = null;
        this.notes = null;
        this.image = null;
    }

    public Artifact(String lotNumber, String name, String description, int categoryNum, int materialNum, int periodNum) {
        this.lotNumber = lotNumber;
        this.name = name;
        this.description = description;
        this.categoryNum = CategoryNum.fromId(categoryNum);
        this.materialNum = MaterialNum.fromId(materialNum);
        this.periodNum = PeriodNum.fromId(periodNum);

        this.culturalOrigin = null;
        this.dimensions = null;
        this.conditionReport = null;
        this.currentLocation = null;
        this.acquisitionMethod = null;
        this.provenance = null;
        this.accessionNumber = null;
        this.notes = null;
        this.image = null;
    }

    // Getters and setters
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CategoryNum getCategoryNum() { return categoryNum; }
    public void setCategoryNum(CategoryNum categoryNum) { this.categoryNum = categoryNum == null ? CategoryNum.UNKNOWN : categoryNum; }

    public MaterialNum getMaterialNum() { return materialNum; }
    public void setMaterialNum(MaterialNum materialNum) { this.materialNum = materialNum == null ? MaterialNum.UNKNOWN : materialNum; }

    public PeriodNum getPeriodNum() { return periodNum; }
    public String getPeriodDescription() {
        switch(periodNum) {
            case UNKNOWN: return "Unknown Dynasty";
            case SHANG: return "Shang Dynasty (c. 1600-1046 BCE)";
            case WESTERN_ZHOU: return "Western Zhou Dynasty (c. 1046-771 BCE)";
            case EASTERN_ZHOU: return "Eastern Zhou Dynasty (770-256 BCE)";
            case QIN: return "Qin Dynasty (221-206 BCE)";
            case HAN: return "Han Dynasty (206 BCE-220 CE)";
            case THREE_KINGDOMS: return "Three Kingdoms Period (220-280 CE)";
            case JIN1: return "Jin Dynasty (266-420 CE)";
            case SOUTHERN_AND_NORTHERN: return "Southern and Northern Dynasties (420-589 CE)";
            case SUI: return "Sui Dynasty (581-618 CE)";
            case TANG: return "Tang Dynasty (618-907 CE)";
            case FIVE_DYNASTIES_AND_TEN_KINGDOMS: return "Five Dynasties and Ten Kingdoms (907-960 CE)";
            case SONG: return "Song Dynasty (960-1279 CE)";
            case LIAO: return "Liao Dynasty (907-1125 CE)";
            case JIN2: return "Jin Dynasty (1115-1234 CE)";
            case YUAN: return "Yuan Dynasty (1271-1368 CE)";
            case MING: return "Ming Dynasty (1368-1644 CE)";
            case QING: return "Qing Dynasty (1644-1912 CE)";
            case REPUBLIC_OF_CHINA: return "Republic of China Period (1912-1949 CE)";
        }
        return "";
    }
    public void setPeriodNum(PeriodNum periodNum) { this.periodNum = periodNum == null ? PeriodNum.UNKNOWN : periodNum; }

    public String getCulturalOrigin() { return culturalOrigin; }
    public void setCulturalOrigin(String culturalOrigin) { this.culturalOrigin = culturalOrigin; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getConditionReport() { return conditionReport; }
    public void setConditionReport(String conditionReport) { this.conditionReport = conditionReport; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getAcquisitionMethod() { return acquisitionMethod; }
    public void setAcquisitionMethod(String acquisitionMethod) { this.acquisitionMethod = acquisitionMethod; }

    public String getProvenance() { return provenance; }
    public void setProvenance(String provenance) { this.provenance = provenance; }

    public String getAccessionNumber() { return accessionNumber; }
    public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}