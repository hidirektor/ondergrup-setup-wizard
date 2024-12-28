package me.t3sl4.installer.utils.system;

public class Definitions {
    public static final String CURRENT_VERSION = "v1.0.6";

    public static final String UPDATER_RELEASE_URL = "https://github.com/hidirektor/ondergrup-updater-service/releases/latest";
    public static final String HYDRAULIC_RELEASE_URL = "https://github.com/hidirektor/ondergrup-hydraulic-tool/releases/latest";
    public static final String LAUNCHER_RELEASE_URL = "https://github.com/hidirektor/ondergrup-launcher/releases/latest";

    public static final String UPDATER_RELEASE_BASE_URL = "https://github.com/hidirektor/ondergrup-updater-service/releases";
    public static final String HYDRAULIC_RELEASE_BASE_URL = "https://github.com/hidirektor/ondergrup-hydraulic-tool/releases";
    public static final String LAUNCHER_RELEASE_BASE_URL = "https://github.com/hidirektor/ondergrup-launcher/releases";

    public static String mainPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}
