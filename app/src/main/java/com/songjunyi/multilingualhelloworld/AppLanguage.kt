package com.songjunyi.multilingualhelloworld

import java.util.Locale

enum class AppLanguage(
    val localeTag: String,
    val nativeName: String,
) {
    CHINESE("zh-CN", "中文"),
    ENGLISH("en", "English"),
    JAPANESE("ja", "日本語");

    fun locale(): Locale = Locale.forLanguageTag(localeTag)
}

