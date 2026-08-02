package com.professional.b07legendaryproject2026.data;
import java.util.Objects;

import java.io.Serializable;

public class Artifact implements  Serializable {
    // Mandatory fields
    private String lotNumber;
    private String name;
    private String description;
    private CategoryNum categoryNum;
    private MaterialNum materialNum;
    private PeriodNum periodNum;
    private int likes;

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

    public Artifact() {
        this.lotNumber = null;
        this.name = null;
        this.description = null;
        this.categoryNum = CategoryNum.UNKNOWN;
        this.materialNum = MaterialNum.UNKNOWN;
        this.periodNum = PeriodNum.UNKNOWN;
        this.likes = 0;

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
        this.likes=0;

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
        return periodNum != null ? periodNum.getDescription() : "";
    }
    public void setPeriodNum(PeriodNum periodNum) { this.periodNum = periodNum == null ? PeriodNum.UNKNOWN : periodNum; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) {this.likes = likes; }

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

    // Checks if any text or enum field in this artifact contains the search query string
    public boolean matchesQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }

        String q = query.toLowerCase().trim();

        return containsText(lotNumber, q) ||
                containsText(name, q) ||
                containsText(description, q) ||
                containsText(culturalOrigin, q) ||
                containsText(dimensions, q) ||
                containsText(conditionReport, q) ||
                containsText(currentLocation, q) ||
                containsText(acquisitionMethod, q) ||
                containsText(provenance, q) ||
                containsText(accessionNumber, q) ||
                containsText(notes, q) ||
                (categoryNum != null && containsText(categoryNum.getDisplayName(), q)) ||
                (materialNum != null && containsText(materialNum.getDisplayName(), q)) ||
                (periodNum != null && containsText(periodNum.getDescription(), q));
    }

    private boolean containsText(String field, String target) {
        return field != null && field.toLowerCase().contains(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(lotNumber, artifact.lotNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lotNumber);
    }
}