package com.professional.b07legendaryproject2026;

public class Artifact {
    // Mandatory fields
    private int lotNumber;
    private String name;
    private String description;
    private int categoryNum;
    private int materialNum;
    private int periodNum;

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

    public Artifact() {
        this.lotNumber = -1;
        this.name = null;
        this.description = null;
        this.categoryNum = -1;
        this.materialNum = -1;
        this.periodNum = -1;

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
        this.categoryNum = categoryNum;
        this.materialNum = materialNum;
        this.periodNum = periodNum;

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

    public int getCategoryNum() { return categoryNum; }
    public void setCategoryNum(int categoryNum) { this.categoryNum = categoryNum; }

    public int getMaterialNum() { return materialNum; }
    public void setMaterialNum(int materialNum) { this.materialNum = materialNum; }

    public int getPeriodNum() { return periodNum; }
    public void setPeriodNum(int periodNum) { this.periodNum = periodNum; }

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