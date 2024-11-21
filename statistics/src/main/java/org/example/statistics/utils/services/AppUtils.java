package org.example.statistics.utils.services;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class AppUtils {

    public String toSlug(String input) {
        String slug = input.toLowerCase(Locale.ROOT);
        slug = slug.replaceAll("đ", "d");
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = slug.replaceAll("[^\\w\\s-]", "").replaceAll("\\s+", "-").replaceAll("-+", "-");
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }
}
