package com.professional.b07legendaryproject2026.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ArtifactEnumMappingTest {

    @Test
    public void categoryNum_unknownUsesNegativeOne() {
        assertEquals(-1, CategoryNum.UNKNOWN.getId());
        assertSame(CategoryNum.UNKNOWN, CategoryNum.fromId(-1));
        assertSame(CategoryNum.UNKNOWN, CategoryNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void categoryNum_validValuesRoundTripThroughFirebaseId() {
        for (CategoryNum value : CategoryNum.values()) {
            if (value == CategoryNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Category failed to round-trip: " + value,
                    value,
                    CategoryNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void categoryNum_firstValidValueUsesZeroBasedId() {
        CategoryNum firstValid = firstValidCategory();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, CategoryNum.fromId(0));
    }

    @Test
    public void materialNum_unknownUsesNegativeOne() {
        assertEquals(-1, MaterialNum.UNKNOWN.getId());
        assertSame(MaterialNum.UNKNOWN, MaterialNum.fromId(-1));
        assertSame(MaterialNum.UNKNOWN, MaterialNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void materialNum_validValuesRoundTripThroughFirebaseId() {
        for (MaterialNum value : MaterialNum.values()) {
            if (value == MaterialNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Material failed to round-trip: " + value,
                    value,
                    MaterialNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void materialNum_firstValidValueUsesZeroBasedId() {
        MaterialNum firstValid = firstValidMaterial();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, MaterialNum.fromId(0));
    }

    @Test
    public void periodNum_unknownUsesNegativeOne() {
        assertEquals(-1, PeriodNum.UNKNOWN.getId());
        assertSame(PeriodNum.UNKNOWN, PeriodNum.fromId(-1));
        assertSame(PeriodNum.UNKNOWN, PeriodNum.fromId(Integer.MAX_VALUE));
    }

    @Test
    public void periodNum_validValuesRoundTripThroughFirebaseId() {
        for (PeriodNum value : PeriodNum.values()) {
            if (value == PeriodNum.UNKNOWN) {
                continue;
            }

            assertSame(
                    "Period failed to round-trip: " + value,
                    value,
                    PeriodNum.fromId(value.getId())
            );
        }
    }

    @Test
    public void periodNum_firstValidValueUsesZeroBasedId() {
        PeriodNum firstValid = firstValidPeriod();
        assertEquals(0, firstValid.getId());
        assertSame(firstValid, PeriodNum.fromId(0));
    }

    private static CategoryNum firstValidCategory() {
        for (CategoryNum value : CategoryNum.values()) {
            if (value != CategoryNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("CategoryNum has no valid values");
    }

    private static MaterialNum firstValidMaterial() {
        for (MaterialNum value : MaterialNum.values()) {
            if (value != MaterialNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("MaterialNum has no valid values");
    }

    private static PeriodNum firstValidPeriod() {
        for (PeriodNum value : PeriodNum.values()) {
            if (value != PeriodNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("PeriodNum has no valid values");
    }
}
