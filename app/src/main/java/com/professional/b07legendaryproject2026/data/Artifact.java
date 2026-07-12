package com.professional.b07legendaryproject2026.data;

public class Artifact {
    // Mandatory fields
    private int lotNumber;
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
    private int accessionNumber;
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
        this.lotNumber = -1;
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
        this.accessionNumber = -1;
        this.notes = null;
        this.image = null;
    }

    public Artifact(int lotNumber, String name, String description, int categoryNum, int materialNum, int periodNum) {
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
        this.accessionNumber = -1;
        this.notes = null;
        this.image = null;
    }

    // Getters and setters
    public int getLotNumber() { return lotNumber; }
    public void setLotNumber(int lotNumber) { this.lotNumber = lotNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CategoryNum getCategoryNum() { return categoryNum; }
    public void setCategoryNum(CategoryNum categoryNum) { this.categoryNum = categoryNum == null ? CategoryNum.UNKNOWN : categoryNum; }

    public MaterialNum getMaterialNum() { return materialNum; }
    public void setMaterialNum(MaterialNum materialNum) { this.materialNum = materialNum == null ? MaterialNum.UNKNOWN : materialNum; }

    public PeriodNum getPeriodNum() { return periodNum; }
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

    public int getAccessionNumber() { return accessionNumber; }
    public void setAccessionNumber(int accessionNumber) { this.accessionNumber = accessionNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}