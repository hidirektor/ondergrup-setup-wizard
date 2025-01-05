package me.t3sl4.installer.utils.system;

public class Definitions {
    public static final String CURRENT_VERSION = "v1.1.6";

    public static final String REPO_OWNER = "hidirektor";
    public static final String UPDATER_REPO_NAME = "ondergrup-updater-service";
    public static final String LAUNCHER_REPO_NAME = "ondergrup-launcher";
    public static final String HYDRAULIC_REPO_NAME = "ondergrup-hydraulic-tool";

    public static final String PREF_NODE_NAME = "canicula";
    public static final String PREF_SETUP_WIZARD_KEY = "setup_wizard_version";
    public static final String PREF_UPDATER_KEY = "updater_version";
    public static final String PREF_LAUNCHER_KEY = "launcher_version";
    public static final String PREF_HYDRAULIC_KEY = "hydraulic_version";

    public static String mainPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}
