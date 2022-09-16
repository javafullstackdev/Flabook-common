package com.msquare.flabook.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.msquare.flabook.models.ReleaseNote;

/**
 *
 */
public class VersionComparer {

    private VersionComparer() throws IllegalAccessException {
        throw new IllegalAccessException("VersionComparer is static util");
    }

    /**
     *
     */
    @Data
    @AllArgsConstructor
    public static class AppUpdateResult {
        private String version;
        private Boolean needUpdate;
        private Boolean forceUpdate;
    }


    private static final String VERSION_DELIMITER = "\\.";
    private static final String NULL_DEFAULT_VALUE = "0";

    /**
     *
     * @param releaseNote 업데이트 앱버전
     * @param version 사용자 앱버전
     * @return AppUpdateResult
     */
    public static AppUpdateResult checkVersion(ReleaseNote releaseNote, String version) {

        if(checkVersion(releaseNote.getVersion(), version)) {
            if(releaseNote.getForceUpdateVersion() != null && !releaseNote.getForceUpdateVersion().isEmpty()) {
                return new AppUpdateResult(releaseNote.getVersion(), true, checkVersion(releaseNote.getForceUpdateVersion(), version ));
            } else {
                return new AppUpdateResult(releaseNote.getVersion(), true, releaseNote.getForceUpdate());
            }
        }
        return new AppUpdateResult(releaseNote.getVersion(), false, false);
    }

    private static boolean checkVersion(String updateVersion, String version) {
        String[] updateVersionComponents = updateVersion.split(VERSION_DELIMITER);
        String[] versionComponents = version.split(VERSION_DELIMITER);

        int max = Math.max(updateVersionComponents.length, versionComponents.length);

        for( int i = 0; i < max ; i++ ) {
            String updateVersionCompoment = (updateVersionComponents.length > i) ? updateVersionComponents[i] : NULL_DEFAULT_VALUE;
            String versionComponent = (versionComponents.length > i) ? versionComponents[i] : NULL_DEFAULT_VALUE;

            int c = compare(updateVersionCompoment, versionComponent);

            if(c > 0) {
                return true;
            } else if(c < 0){
                return false;
            }
        }
        return false;
    }


    private static boolean isNull(String v1, String v2) {
        return v1 == null && v2 == null;
    }
    private static int compare(String v1, String v2){
        if (isNull(v1, v2))
            return 0;

        if (v1 == null || v1.isEmpty()) {
            try {
                if(Integer.parseInt(NULL_DEFAULT_VALUE) == Integer.parseInt(v2)) {
                    return 0;
                } else {
                    return 1;
                }
            } catch(Exception e) {
                return 0;
            }
        }

        if (v2 == null) {
            try {
                if(Integer.parseInt(NULL_DEFAULT_VALUE) == Integer.parseInt(v1)) {
                    return 0;
                } else {
                    return -1;
                }
            } catch(Exception e) {
                return 0;
            }
        }

        try {
            return Integer.compare(Integer.parseInt(v1), Integer.parseInt(v2));
        } catch(Exception e) {
            return 0;
        }
    }
}
