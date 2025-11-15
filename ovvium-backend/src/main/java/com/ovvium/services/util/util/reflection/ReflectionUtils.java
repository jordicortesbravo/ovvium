package com.ovvium.services.util.util.reflection;

import com.ovvium.services.util.util.basic.Utils;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("unchecked")
public final class ReflectionUtils {

    private ReflectionUtils() {}

    private static final Map<Class<?>, Class<?>> wrapperToPrimitiveMap;
    private static final Map<Class<?>, Class<?>> primitiveToWrapperMap;
    private static final Map<Class<?>, List<Class<?>>> primitiveNarrowingMap;

    static {
        val map = new HashMap<Class<?>, Class<?>>();
        map.put(Boolean.class, boolean.class);
        map.put(Byte.class, byte.class);
        map.put(Character.class, char.class);
        map.put(Double.class, double.class);
        map.put(Float.class, float.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Short.class, short.class);
        map.put(Void.class, void.class);
        wrapperToPrimitiveMap = Collections.unmodifiableMap(map);
        primitiveToWrapperMap = Collections.unmodifiableMap(Utils.invert(wrapperToPrimitiveMap));

        val nmap = new HashMap<Class<?>, List<Class<?>>>();
        nmap.put(short.class, Arrays.asList((Class<?>) byte.class, char.class));
        nmap.put(char.class, Arrays.asList((Class<?>) byte.class, short.class));
        nmap.put(int.class, Arrays.asList((Class<?>) byte.class, short.class, char.class));
        nmap.put(long.class, Arrays.asList((Class<?>) byte.class, short.class, char.class, int.class));
        nmap.put(float.class, Arrays.asList((Class<?>) byte.class, short.class, char.class, int.class, long.class));
        nmap.put(double.class, Arrays.asList((Class<?>) byte.class, short.class, char.class, int.class, long.class, float.class));
        primitiveNarrowingMap = Collections.unmodifiableMap(nmap);
    }

    public static List<Class<?>> getClasses(Package _package) throws ClassNotFoundException, IOException {
        return getClasses(_package.getName());
    }

    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new LinkedList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static <T extends Member> Set<T> getMembers(Class<?> owner, Class<T> type, boolean inherited, MemberFilter<? super T> filter) {

        Set<T> set = new LinkedHashSet<T>();

        if (owner == null) {
            return set;
        }

        if (type.isAssignableFrom(Constructor.class)) {
            addAll(set, owner.getDeclaredConstructors());
        }
        if (type.isAssignableFrom(Method.class)) {
            addAll(set, owner.getDeclaredMethods());
        }
        if (type.isAssignableFrom(Field.class)) {
            addAll(set, owner.getDeclaredFields());
        }

        set = new LinkedHashSet<T>(filter(set, filter));

        if (inherited) {
            if (owner.isInterface()) {
                for (Class<?> i : owner.getInterfaces()) {
                    set.addAll(getMembers(i, type, inherited, filter));
                }
            } else {
                set.addAll(getMembers(owner.getSuperclass(), type, inherited, filter));
            }
        }

        for (val m : set) {
            ((AccessibleObject) m).setAccessible(true);
        }

        return set;
    }

    private static <T> void addAll(Set<T> set, Object[] array) {
        for (Object element : array) {
            T casted = (T) element;
            set.add(casted);
        }
    }

    public static Set<Member> getMembers(Class<?> owner, boolean inherited, MemberFilter<Member> filter) {
        return getMembers(owner, Member.class, inherited, filter);
    }

    public static <T extends Member> Set<T> getMembers(Class<?> owner, Class<T> type, MemberFilter<? super T> filter) {
        return getMembers(owner, type, true, filter);
    }

    public static <T extends Member> Set<T> getMembers(Class<?> owner, Class<T> type) {
        return getMembers(owner, type, true, new EmptyFilter());
    }

    public static Set<Member> getMembers(Class<?> owner, MemberFilter<Member> filter) {
        return getMembers(owner, Member.class, true, filter);
    }

    public static <T extends Member, M extends T> List<M> filter(Collection<M> list, MemberFilter<T> filter) {
        List<M> filtered = new ArrayList<M>();
        for (M member : list) {
            if (filter.match(member)) {
                filtered.add(member);
            }
        }
        return filtered;
    }

    public static Class<?> getPrimitive(Class<?> clss) {
        return wrapperToPrimitiveMap.get(clss);
    }

    public static Class<?> getBoxed(Class<?> clss) {
        return primitiveToWrapperMap.get(clss);
    }

    public static boolean isBoxed(Class<?> clss) {
        return wrapperToPrimitiveMap.containsKey(clss);
    }

    @SneakyThrows
    public static <T> T create(Class<T> clss, Object... args) {
        Constructor<T> ctor = null;
        for (val c : clss.getDeclaredConstructors()) {
            if (valid(c, args)) {
                ctor = (Constructor<T>) c;
            }
        }
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    public static boolean valid(Constructor<?> ctor, Object... args) {
        if (ctor.getParameterTypes().length != args.length) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            val par = ctor.getParameterTypes()[i];
            if (args[i] != null && !par.isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public static Object invoke(Object instance, String methodName, Object... args) {
        val clss = instance.getClass();
        val set = getMembers(clss, Method.class, true, new MethodFilter(methodName, args));
        return firstOrFail(set, clss, "valid methods", methodName).invoke(instance, args);
    }

    @SneakyThrows
    public static Object invoke(Class<?> clss, String methodName, Object... args) {
        val set = getMembers(clss, Method.class, true, new MethodFilter(methodName, args));
        return firstOrFail(set, clss, "valid methods", methodName).invoke(null, args);
    }

    @SneakyThrows
    public static Object get(Object instance, String fieldName) {
        val clss = instance.getClass();
        val set = getMembers(clss, Field.class, true, new RegexFilter(fieldName));
        return firstOrFail(set, clss, "fields", fieldName).get(instance);
    }

    @SneakyThrows
    public static Object get(Class<?> clss, String fieldName) {
        return getField(clss, fieldName).get(null);
    }

    @SneakyThrows
    public static void set(Object instance, String fieldName, Object value) {
        val clss = instance.getClass();
        val set = getMembers(clss, Field.class, true, new RegexFilter(fieldName));
        firstOrFail(set, clss, "fields", fieldName).set(instance, value);
    }

    @SneakyThrows
    public static void set(Class<?> clss, String fieldName, Object value) {
        getField(clss, fieldName).set(null, value);
    }

    public static Field getField(Class<?> clss, String name) {
        Field f;
        try {
            f = clss.getField(name);
        } catch (Exception e) {
            f = getFieldAcc(clss, name);
        }
        if (f != null) {
            f.setAccessible(true);
        }
        return f;
    }

    private static Field getFieldAcc(Class<?> clss, String name) {
        if (clss == null) {
            return null;
        }
        try {
            return clss.getDeclaredField(name);
        } catch (Exception e) {
            return getFieldAcc(clss.getSuperclass(), name);
        }
    }

    @SneakyThrows
    public static <E> E getFieldValue(Object instance, String fieldName, Class<E> clazz) {
        return (E) getFieldValue(instance, fieldName);
    }

    @SneakyThrows
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            val field = getFieldAcc(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    @SneakyThrows
    public static List<String> getStringConstants(Class<?> clss) {
        val list = new ArrayList<String>();

        if (Enum.class.isAssignableFrom(clss)) {
            for (Object c : clss.getEnumConstants()) {
                list.add(c.toString());
            }

        } else {
            @SuppressWarnings("rawtypes")//
            Set<Field> set = getMembers(clss, Field.class, false, new ModifierFilter(Modifier.PUBLIC, Modifier.FINAL));

            for (val field : set) {
                if (field.getType().equals(String.class)) {
                    list.add((String) field.get(null));
                }
            }
        }

        return list;
    }

    private static <T extends AccessibleObject> T firstOrFail(Set<T> set, Class<?> clss, String member, String name) {
        if (set.size() == 1) {
            T m = Utils.first(set);
            m.setAccessible(true);
            return m;
        } else if (set.size() == 0) {
            throw new IllegalArgumentException("No " + member + " were found with name '" + name + "' in " + clss);
        }
        throw new IllegalArgumentException(set.size() + " " + member + " with name '" + name + "' were found in " + clss + ": " + set);
    }

    public static List<Class<?>> getGenericClasses(Type type) {
        val parType = (ParameterizedType) type;
        val list = new ArrayList<Class<?>>();
        for (val arg : parType.getActualTypeArguments()) {
            list.add((Class<?>) arg);
        }
        return list;
    }

    public static Class<?> getGenericClass(Type type) {
        return Utils.first(getGenericClasses(type));
    }

    public static <T> Class<T> getConcreteClass(Class<?> clazz) {
        return (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("rawtypes")
    public static <T> T getEnum(Class<T> clss, String name) {
        return (T) Enum.valueOf((Class<Enum>) clss, name);
    }

    /**
     * Similar to Class.isAssignableFrom(). Based on Narrowing primitive conversions in
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
     */
    public boolean isAssignablePrimitive(Class<?> from, Class<?> to) {
        if (!from.isPrimitive() || !to.isPrimitive()) {
            throw new IllegalArgumentException("Both 'from' and 'to' must be primitive classes. Received " + from + " and " + to);
        }
        if (from.equals(to)) {
            return true;
        }
        val valid = primitiveNarrowingMap.get(from);
        return valid != null && (valid.contains(to));
    }
}
