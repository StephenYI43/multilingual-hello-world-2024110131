package com.songjunyi.multilingualhelloworld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class AppLanguageTest {
    @Test
    public void appProvidesExactlyThreeUniqueLanguages() {
        AppLanguage[] languages = AppLanguage.values();
        Set<String> localeTags = new HashSet<>();
        Arrays.stream(languages).forEach(language -> localeTags.add(language.getLocaleTag()));

        assertEquals(3, languages.length);
        assertEquals(3, localeTags.size());
    }

    @Test
    public void frenchIsTheThirdLanguage() {
        assertEquals(AppLanguage.FRENCH, AppLanguage.values()[2]);
        assertEquals("fr", AppLanguage.FRENCH.getLocaleTag());
        assertTrue(AppLanguage.FRENCH.getNativeName().contains("Français"));
    }

    @Test
    public void systemLocaleIsMappedToSupportedLanguages() {
        assertEquals(AppLanguage.CHINESE, AppLanguage.Companion.fromSystemLocale(java.util.Locale.CHINA));
        assertEquals(AppLanguage.FRENCH, AppLanguage.Companion.fromSystemLocale(java.util.Locale.FRANCE));
        assertEquals(AppLanguage.ENGLISH, AppLanguage.Companion.fromSystemLocale(java.util.Locale.GERMANY));
    }
}
