package me.siketyan.silicagel.util;

import me.siketyan.silicagel.model.Media;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemplateParser {
    private static final String PREFIX = "%";
    private static final String SUFFIX = "%";

    public static String parse(String template, Media media, String player) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        Map<String, String> variables = new HashMap<>();
        variables.put("title", media.getTitle());
        variables.put("artist", media.getArtist());
        variables.put("album", media.getAlbum());
        variables.put("player", player);
        variables.put("y", String.format(Locale.ROOT, "%4d", year));
        variables.put("m", String.format(Locale.ROOT, "%2d", month));
        variables.put("d", String.format(Locale.ROOT, "%2d", day));
        variables.put("h", String.format(Locale.ROOT, "%02d", hour));
        variables.put("i", String.format(Locale.ROOT, "%02d", minute));
        variables.put("s", String.format(Locale.ROOT, "%02d", second));

        return replace(template, variables);
    }

    private static String replace(String template, Map<String, String> variables) {
        for (String variable : variables.keySet()) {
            template = template.replaceAll(
                PREFIX + variable + SUFFIX,
                variables.get(variable)
            );
        }

        return template;
    }
}
