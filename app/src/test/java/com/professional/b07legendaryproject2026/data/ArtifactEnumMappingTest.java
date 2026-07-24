package com.professional.b07legendaryproject2026.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ArtifactEnumMappingTest {

    @Test
    public void categoryNum_unknownUsesNegativeOne() {
        assertEquals(-1, Artifact.CategoryNum.UNKNOWN.getId());
        assertSame(Artifact.CategoryNum.UNKNOWN, Artifact.CategoryNum.fromId(-1));
        assertSame(Artifact.CategoryNum.UNKNOWN, Artifact.CategoryNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void categoryNum_validValuesRoundTripThroughFirebaseId() {
        for (Artifact.CategoryNum value : Artifact.CategoryNum.values()) {
            if (value == Artifact.CategoryNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Category failed to round-trip: " + value,
                    value,
                    Artifact.CategoryNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void categoryNum_firstValidValueUsesZeroBasedId() {
        Artifact.CategoryNum firstValid = firstValidCategory();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, Artifact.CategoryNum.fromId(0));
    }

    @Test
    public void materialNum_unknownUsesNegativeOne() {
        assertEquals(-1, Artifact.MaterialNum.UNKNOWN.getId());
        assertSame(Artifact.MaterialNum.UNKNOWN, Artifact.MaterialNum.fromId(-1));
        assertSame(Artifact.MaterialNum.UNKNOWN, Artifact.MaterialNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void materialNum_validValuesRoundTripThroughFirebaseId() {
        for (Artifact.MaterialNum value : Artifact.MaterialNum.values()) {
            if (value == Artifact.MaterialNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Material failed to round-trip: " + value,
                    value,
                    Artifact.MaterialNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void materialNum_firstValidValueUsesZeroBasedId() {
        Artifact.MaterialNum firstValid = firstValidMaterial();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, Artifact.MaterialNum.fromId(0));
    }

    @Test
    public void periodNum_unknownUsesNegativeOne() {
        assertEquals(-1, Artifact.PeriodNum.UNKNOWN.getId());
        assertSame(Artifact.PeriodNum.UNKNOWN, Artifact.PeriodNum.fromId(-1));
        assertSame(Artifact.PeriodNum.UNKNOWN, Artifact.PeriodNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void periodNum_validValuesRoundTripThroughFirebaseId() {
        for (Artifact.PeriodNum value : Artifact.PeriodNum.values()) {
            if (value == Artifact.PeriodNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Period failed to round-trip: " + value,
                    value,
                    Artifact.PeriodNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void periodNum_firstValidValueUsesZeroBasedId() {
        Artifact.PeriodNum firstValid = firstValidPeriod();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, Artifact.PeriodNum.fromId(0));
    }

    private static Artifact.CategoryNum firstValidCategory() {
        for (Artifact.CategoryNum value : Artifact.CategoryNum.values()) {
            if (value != Artifact.CategoryNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("CategoryNum has no valid values");
    }

    private static Artifact.MaterialNum firstValidMaterial() {
        for (Artifact.MaterialNum value : Artifact.MaterialNum.values()) {
            if (value != Artifact.MaterialNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("MaterialNum has no valid values");
    }

    private static Artifact.PeriodNum firstValidPeriod() {
        for (Artifact.PeriodNum value : Artifact.PeriodNum.values()) {
            if (value != Artifact.PeriodNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("PeriodNum has no valid values");
    }
}
