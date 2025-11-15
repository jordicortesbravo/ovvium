package com.ovvium.services.util.util.xprops;

import com.ovvium.services.util.util.el.ExpFactory;
import com.ovvium.services.util.util.reflection.ModifierFilter;
import com.ovvium.services.util.util.reflection.MultipleFilter;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.util.util.reflection.SetterFilter;
import com.ovvium.services.util.util.string.StringUtils;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class XProps extends Properties implements Iterable<Entry<String, String>> {

    private static final long serialVersionUID = -727798774096709995L;
    public static final char SEPARATOR = '.';

    private static final KeyComparator<String> KEY_COMPARATOR = new KeyComparator<String>();

    private static class KeyComparator<T extends Comparable<T>> implements Comparator<Entry<T, ?>> {

        @Override
        public int compare(Entry<T, ?> a, Entry<T, ?> b) {
            return a.getKey().compareTo(b.getKey());
        }
    }

    public XProps() {}

    private XProps(XProps p) {
        this.add(p);
    }

    @Override
    public XProps clone() {
        return new XProps(this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterator<Entry<String, String>> iterator() {
        Set set = super.entrySet();
        List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(set);
        Collections.sort(list, KEY_COMPARATOR);
        return list.iterator();
    }

    public XProps set(String key, String value) {
        setProperty(key == null ? "" : key, value == null ? "" : value);
        return this;
    }

    public XProps add(String prefix, Properties properties) {
        prefix = prefix == null ? "" : prefix + SEPARATOR;
        for (val name : properties.stringPropertyNames()) {
            set(prefix + name, properties.getProperty(name));
        }
        return this;
    }

    public XProps add(Properties properties) {
        return add(null, properties);
    }

    public XProps add(String prefix, Map<String, String> map) {
        prefix = prefix == null ? "" : prefix + SEPARATOR;
        for (Entry<String, String> e : map.entrySet()) {
            set(prefix + e.getKey(), e.getValue());
        }
        return this;
    }

    public XProps add(Map<String, String> map) {
        return add(null, map);
    }


	/**
	 * @deprecated Use getRequired or getOptionalProperty instead.
	 */
	@Deprecated
    public String get(String key) {
		return getProperty(key);
    }


    public String getRequired(String key) {
        return getOptionalProperty(key).orElseThrow(() -> new PropertyNotFoundException(key));
    }

    public Optional<String> getOptionalProperty(String key) {
        return Optional.ofNullable(super.getProperty(key));
    }

    public Integer getInt(String key) {
		return Integer.parseInt(getRequired(key));
    }

    public Long getLong(String key) {
		return Long.parseLong(getRequired(key));
    }

    public Boolean getBoolean(String key) {
		return Boolean.parseBoolean(getRequired(key));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getOptionalProperty(key)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    @SneakyThrows
    public URL getUrl(String key) {
		return new URL(getRequired(key));
    }

    @SneakyThrows
    public URI getUri(String key) {
        return new URI(getRequired(key));
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        return getEnumAux(key, enumClass, false);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, boolean caseSensitive) {
        return getEnumAux(key, enumClass, caseSensitive);
    }

    protected <T> T getEnumAux(String key, Class<T> enumClass, boolean caseSensitive) {
        String s = getRequired(key);
        for (val e : enumClass.getEnumConstants()) {
            if (caseSensitive ? e.toString().equals(s) : e.toString().equalsIgnoreCase(s)) {
                return e;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOfType(String key, Class<T> type) {

        if (type.isPrimitive()) {
            type = (Class<T>) ReflectionUtils.getBoxed(type);
        }

        if (String.class.isAssignableFrom(type)) {
            return (T) get(key);
        } else if (Integer.class.isAssignableFrom(type)) {
            return (T) getInt(key);
        } else if (Long.class.isAssignableFrom(type)) {
            return (T) getLong(key);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return (T) getBoolean(key);
        } else if (URL.class.isAssignableFrom(type)) {
            return (T) getUrl(key);
        } else if (URI.class.isAssignableFrom(type)) {
            return (T) getUri(key);
        } else if (Enum.class.isAssignableFrom(type)) {
            return getEnumAux(key, type, false);
        }

        return null;
    }

    public <T> T fill(T object) {
        @SuppressWarnings({ "unchecked", "rawtypes" }) //
        val setters = ReflectionUtils.getMembers(object.getClass(), Method.class,
                new MultipleFilter<Method>( //
                        new ModifierFilter(Modifier.PUBLIC, -Modifier.ABSTRACT), //
                        new SetterFilter()));
        for (val setter : setters) {
            val name = org.apache.commons.lang.StringUtils.uncapitalize(StringUtils.removePrefix(setter.getName(), SetterFilter.PREFIX));
            val value = getOfType(name, setter.getParameterTypes()[0]);
            if (value != null) {
                ReflectionUtils.invoke(object, setter.getName(), value);
            }
        }
        return object;
    }

    public List<String> childNames() {
        val set = new TreeSet<String>();
        for (val k : stringPropertyNames()) {
            int p = k.indexOf('.');
            p = p >= 0 ? p : k.length();
            set.add(k.substring(0, p));
        }
        return new ArrayList<String>(set);
    }

    public XProps sub(String prefix) {
        return sub(prefix, false);
    }

    /**
     * @param prefix
     * @param inherit
     *            si és true
     */
    public XProps sub(String prefix, boolean inherit) {
        XProps p = inherit ? new XProps(this) : new XProps();
        prefix += SEPARATOR;
        for (String key : stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                p.setProperty(StringUtils.removePrefix(key, prefix), getRequired(key));
            }
        }
        return p;
    }

    public String[] getSplitted(String key) {
        String s = getRequired(key);
        String[] ss = s.split(",");
        for (int i = 0; i < ss.length; i++) {
            ss[i] = ss[i].trim();
        }
        return ss;
    }

    @SneakyThrows
    public XProps load(Charset charset, String... resources) {
        for (String resource : resources) {
            if (!resource.startsWith("/")) {
                resource = '/' + resource;
            }
            @Cleanup val is = this.getClass().getResourceAsStream(resource);
            if (is == null) {
                throw new FileNotFoundException("Resource " + resource + " does not exist.");
            }
            @Cleanup val reader = new InputStreamReader(is, charset);
            this.load(reader);
        }
        return this;
    }

    @SneakyThrows
    public XProps load(Charset charset, File file) {
        @Cleanup val reader = new InputStreamReader(new FileInputStream(file), charset);
        this.load(reader);
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        Set set = new TreeSet<Entry<String, String>>(KEY_COMPARATOR);
        for (Entry<String, String> entry : this) {
            set.add(entry);
        }
        return set;
    }

    @Deprecated
    // FIXME Fer millor! Ho estem usant per injectar xprops comodament com a contexte velocity
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> toMap() {
        Map<String, Object> map = (Map) this;
        map.put("props", this);
        return map;
    }

    public XProps resolveExpressions() {
        return resolveExpressions("${", "}");
    }

    public XProps resolveExpressions(String prefix, String suffix) {
        return new XPropsResolver(new ExpFactory(prefix, suffix)).getResolved(this);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean original) {
        if (original) {
            return super.toString();
        }
        val sb = new StringBuilder();
        for (Entry<String, String> p : this) {
            sb.append(p.getKey()).append("=").append(p.getValue()).append("\r\n");
        }
        return sb.toString();
    }

}
