package com.ovvium.services.util.util.string;

import com.ovvium.services.util.util.basic.Traverser;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

@UtilityClass
public final class StringUtils {

    public static final String PATH_SEPARATOR = "/";

    public static String stripAccents(String str) {
        if (str == null) {
            return null;
        }

        String result = Normalizer.normalize(str, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return result;
    }

    public static String normalize(String str) {
        if (str == null) {
            return null;
        }

        String result = stripAccents(str);
        result = result.replaceAll("[.,;:]", " ");
        result = result.replaceAll("(\\s)+", " ");

        return result.trim().toUpperCase();
    }

    /**
     * 
     * @deprecated use {@link #stripAccents(String)} instead.
     */
    @Deprecated
    public static String toUpperCaseWithoutAccent(String str) {

        if (str == null) {
            return null;
        }

        return stripAccents(str).toUpperCase();
    }

    public static String convertAsciiHexToString(String hex) {

        if (hex == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            sb.append((char) Integer.parseInt(output, 16));
        }
        return sb.toString();
    }

    /**
     * Fa un split, evitant separar el text entre cometes dobles.
     * 
     * @param removeQuotes
     *            si s'han d'eliminar les cometes dels tokens resultants
     */
    public static List<String> splitQuoted(String s, boolean removeQuotes) {
        val list = new LinkedList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(s);
        while (m.find()) {
            String token = m.group(1);
            if (removeQuotes) {
                token = token.replace("\"", "");
            }
            list.add(token);
        }
        return list;
    }

    /**
     * Treu el prefixe de l'String entrat. Llança una excepció en cas de que no tingui tal prefixe. És semblant a StringUtils.removeStart()
     * d'Apache Commons, però aquell no llança cap excepció.
     */
    public static String removePrefix(String s, String prefix) {
        if (!s.startsWith(prefix)) {
            throw new IllegalArgumentException("String '" + s + "' doesn't start with '" + prefix + "'");
        }
        return s.substring(prefix.length());
    }

    public static String removePrefixIgnoreCase(String s, String prefix) {
        if (!startsWithIgnoreCase(s, prefix)) {
            throw new IllegalArgumentException("String '" + s + "' doesn't start with '" + prefix + "'");
        }
        return s.substring(prefix.length());
    }

    /**
     * Treu els tags HTML intentant obtenir una representació semblant del format:
     * <ul>
     * <li>Els BR i P es substituïran per salts de línia</li>
     * <li>Els links es posaran entre parèntesi</li>
     * <li>Els TR seran salts de línia i els TD i TH tabul·lacions</li>
     * <li>Els LI tindràn una tabul·lació al davant i un ·</li>
     * <li>Els espais múltiples es convertiran en un</li>
     * <li>La resta de tags al campo</li>
     * </ul>
     */
    public static String htmlToPlainText(String html) {

        html = replace(html, "<\\s*(br|p|tr)/?\\s*>", "\r\n"); // BR P TR
        html = replace(html, "<\\s*(td|th)/?\\s*>", "\t"); // TD TH
        html = replace(html, "<\\s*(li)/?\\s*>", "\r\n\t· "); // LI
        html = replace(html, "<\\s*a\\s*href=([\"'])([^\"]*)\\1\\s*>([^<]*)</a>", " $2 \\($1\\) "); // LI
        html = html.replaceAll("<.*?>", " "); // Resta de tags

        // Dobles espais:
        String last = null;
        while (!html.equals(last)) {
            last = html;
            html = html.replaceAll("  ", " ");
        }

        return html;
    }

    private static String replace(String s, String regex, String replacement) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(s).replaceAll(replacement);
    }

    public static String pathConcat(Object... path) {
        if (path.length == 0) {
            return "";
        }
        val sb = new StringBuilder();
        val tr = Traverser.of(path);
        for (Object p : tr) {
            if (p != null) {
                String s = p.toString();
                if (PATH_SEPARATOR.equals(s)) {
                    sb.append(PATH_SEPARATOR);
                } else {
                    sb.append(pathClean(s));
                    if (!tr.isLast()) {
                        sb.append(PATH_SEPARATOR);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String pathClean(String path) {
        return path.startsWith(PATH_SEPARATOR) ? pathClean(path.substring(1)) //
                : path.endsWith(PATH_SEPARATOR) ? pathClean(path.substring(0, path.length() - 1)) //
                        : path;
    }

    public static String getFirstLine(String s, int maxlength) {
        if (s == null) {
            return null;
        }
        int p = org.apache.commons.lang.StringUtils.indexOfAny(s, new char[] { '\r', '\n' });
        p = min(p < 0 ? s.length() : p, maxlength);
        return s.substring(0, p);
    }

    public static String uncapitalizeFirst(String s) {
        return s == null ? null : s.isEmpty() ? "" : s.substring(0, 1).toLowerCase(Locale.US) + s.substring(1);
    }

    public static String capitalizeFirst(String s) {
        return s == null ? null : s.isEmpty() ? "" : s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1);
    }

    public static boolean containsIgnoreCase(Collection<String> collection, String value) {
        if (CollectionUtils.isEmpty(collection) || org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return false;
        }
        for (val s : collection) {
            if (value.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithIgnoreCase(String source, String string) {
        if (org.apache.commons.lang.StringUtils.isEmpty(source) || org.apache.commons.lang.StringUtils.isEmpty(string)) {
            return false;
        }
        val s = string.toUpperCase();
        val src = source.toUpperCase();
        return src.startsWith(s);
    }

    // TODO: Escaped, no accepta contrabarres
    public static String format(String s, Object... args) {
        val p = Pattern.compile("\\?");
        val m = p.matcher(s);
        val sb = new StringBuffer();
        int i = 0;
        while (m.find()) {
            if (i >= args.length) {
                throw new IllegalArgumentException("Less number of arguments where passed");
            }
            m.appendReplacement(sb, args[i] == null ? "" : args[i].toString());
            i++;
        }
        if (i != args.length) {
            throw new IllegalArgumentException("Too many arguments where passed");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String randomString(int length, String fromCharacters) {
        char[] chars = fromCharacters.toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String removeLineBreaks(String str) {
        return str.replace("\\r\\n", " ").replace("\\n", " ").replace("\\r", " ");
    }

    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || org.apache.commons.lang.StringUtils.isEmpty(open) || org.apache.commons.lang.StringUtils.isEmpty(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List<String> list = new ArrayList<String>();
        int pos = 0;
        while (pos < (strLen - closeLen)) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

}
