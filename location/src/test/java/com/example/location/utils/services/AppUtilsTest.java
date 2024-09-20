package com.example.location.utils.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUtilsTest {

    private AppUtils appUtils;

    @BeforeEach
    void setUp() {
        appUtils = new AppUtils();
    }

    @Test
    void testToSlug_NormalString() {
        String input = "This is a normal string";
        String expected = "this-is-a-normal-string";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_StringWithAccents() {
        String input = "Đây là chuỗi có dấu";
        String expected = "day-la-chuoi-co-dau";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_StringWithSpecialCharacters() {
        String input = "Special @# characters! in % this ^ string";
        String expected = "special-characters-in-this-string";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_StringWithMultipleSpaces() {
        String input = "  String    with    multiple    spaces ";
        String expected = "string-with-multiple-spaces";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_EmptyString() {
        String input = "";
        String expected = "";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_StringWithLeadingAndTrailingSpaces() {
        String input = "   Leading and trailing spaces    ";
        String expected = "leading-and-trailing-spaces";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }

    @Test
    void testToSlug_StringWithHyphen() {
        String input = "This string-has-hyphen";
        String expected = "this-string-has-hyphen";
        String result = appUtils.toSlug(input);
        assertEquals(expected, result);
    }
}