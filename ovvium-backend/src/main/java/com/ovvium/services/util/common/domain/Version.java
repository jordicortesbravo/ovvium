package com.ovvium.services.util.common.domain;

import lombok.val;

import java.util.Arrays;

public class Version implements Comparable<Version> {

    private final int[] v;

    /**
     * @param version
     *            Examples: 14, 1.5.01, 0.10.5
     */
    public Version(String version) {
        int[] vAux = null;
        try {
            val s = version.split("\\.");
            if (s.length == 0) {
                throwInvalid(version);
            }
            vAux = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                vAux[i] = Integer.parseInt(s[i]);
                if (vAux[i] < 0) {
                    throwInvalid(version);
                }
            }
        } catch (Exception e) {
            throwInvalid(version);
        }
        v = vAux;
    }

    private void throwInvalid(String version) {
        throw new IllegalArgumentException("Invalid version format: '" + version + "'");
    }

    @Override
    public String toString() {
        val s = Arrays.toString(v).replace(", ", ".");
        return s.substring(1, s.length() - 1);
    }

    @Override
    public int compareTo(Version o) {
        val commonPlaces = Math.min(v.length, o.v.length);
        for (int i = 0; i < commonPlaces; i++) {
            if (v[i] != o.v[i]) {
                return Integer.compare(v[i], o.v[i]);
            }
        }
        val trailing = v.length < o.v.length ? o.v : v;
        for (int i = commonPlaces; i < trailing.length; i++) {
            if (trailing[i] != 0) {
                return v.length - o.v.length;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Version && compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int mult = 1;
        for (int i = 0; i < v.length; i++) {
            mult = mult * 59;
            if (v[i] != 0) {
                h = h * 31 + v[i] * mult;
            }
        }
        return h;
    }
}
