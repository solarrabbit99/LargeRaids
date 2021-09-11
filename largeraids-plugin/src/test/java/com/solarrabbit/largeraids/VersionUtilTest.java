package com.solarrabbit.largeraids;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VersionUtilTest {
    private static final String[] VERSIONS_MOCK = new String[] { "v1_14_R1", "v1_15_R1", "v1_16_R3", "v1_17_R1" };

    @Test
    public void versionAtLeastTest() {
        assertEquals(false, isAtLeastMock("v1_16_R3", "v1_14_R1"));
        assertEquals(false, isAtLeastMock("v1_16_R3", "v1_15_R1"));
        assertEquals(true, isAtLeastMock("v1_16_R3", "v1_16_R3"));
        assertEquals(true, isAtLeastMock("v1_16_R3", "v1_17_R1"));
    }

    public static boolean isAtLeastMock(String version, String currentVersion) {
        boolean hasMet = false;
        for (int i = 0; i < VERSIONS_MOCK.length; i++) {
            if (!hasMet && VERSIONS_MOCK[i].equals(version))
                hasMet = true;
            if (VERSIONS_MOCK[i].equals(currentVersion))
                return hasMet;
        }
        return false;
    }

}
