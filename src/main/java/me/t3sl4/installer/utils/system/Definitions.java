package me.t3sl4.installer.utils.system;

public class Definitions {
    public static final String CURRENT_VERSION = "v2.0.2";

    public static final String REPO_OWNER = "hidirektor";
    public static final String UPDATER_REPO_NAME = "updater-service-desktop";
    public static final String HYDRAULIC_REPO_NAME = "hydraulic-tool-desktop";

    public static final String PREF_NODE_NAME = "ondergrup";
    public static final String PREF_SETUP_WIZARD_KEY = "setup_wizard_version";
    public static final String PREF_UPDATER_KEY = "updater_version";
    public static final String PREF_HYDRAULIC_KEY = "hydraulic_version";

    public static String mainPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}
