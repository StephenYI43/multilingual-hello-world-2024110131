package com.songjunyi.multilingualhelloworld

import java.util.Locale

enum class AppLanguage(
    val localeTag: String,
    val nativeName: String,
) {
    CHINESE("zh-CN", "中文"),
    ENGLISH("en", "English"),
    FRENCH("fr", "Français");

    fun locale(): Locale = Locale.forLanguageTag(localeTag)

    companion object {
        fun fromSystemLocale(locale: Locale): AppLanguage = when (locale.language.lowercase()) {
            "zh" -> CHINESE
            "fr" -> FRENCH
            else -> ENGLISH
        }
    }
}
