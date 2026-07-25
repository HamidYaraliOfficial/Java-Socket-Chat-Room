package com.chatroom.i18n;

/**
 * Supported application languages. Each language declares its own text
 * direction (LTR / RTL) so the UI can mirror itself automatically.
 */
public enum Language {
    ENGLISH("English", "en", false),
    PERSIAN("فارسی", "fa", true),
    CHINESE("中文", "zh", false);

    private final String displayName;
    private final String code;
    private final boolean rtl;

    Language(String displayName, String code, boolean rtl) {
        this.displayName = displayName;
        this.code = code;
        this.rtl = rtl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public boolean isRtl() {
        return rtl;
    }

    public static Language fromCode(String code) {
        for (Language l : values()) {
            if (l.code.equalsIgnoreCase(code)) {
                return l;
            }
        }
        return ENGLISH;
    }
}
