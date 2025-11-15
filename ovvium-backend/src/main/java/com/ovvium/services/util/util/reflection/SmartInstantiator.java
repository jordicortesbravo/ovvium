package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Constructor;
import java.util.*;

import lombok.SneakyThrows;
import lombok.val;

public class SmartInstantiator {

    @SneakyThrows
    public static <T> Constructor<T> findConstructor(Class<T> clss, Class<?>... types) {

        val list = new ArrayList<Constructor<T>>();
        for (Constructor<?> ctor : clss.getConstructors()) {
            if (isValid(ctor, types)) {
                @SuppressWarnings("unchecked") Constructor<T> cuco = (Constructor<T>) ctor;
                list.add(cuco);
            }
        }

        if (list.isEmpty()) {
            throw new NoSuchMethodException(clss.getName() + ".<init>" + Arrays.toString(types));
        }

        Collections.sort(list, new Comparator<Constructor<T>>() {

            @Override
            public int compare(Constructor<T> o1, Constructor<T> o2) {
                return o1.getParameterTypes().length - o2.getParameterTypes().length;
            }
        });

        return list.get(0);
    }

    @SneakyThrows
    public static <T> Constructor<T> findConstructor(Class<T> clss, Object... args) {
        return findConstructor(clss, getTypes(args));
    }

    public static boolean isValid(Constructor<?> ctor, Class<?>... types) {
        for (Class<?> par : ctor.getParameterTypes()) {
            if (findCandidateIndex(par, types) < 0) {
                return false;
            }
        }
        return true;
    }

    public static Object[] getArgs(Class<?>[] pars, Object... candidateArgs) {
        Object[] args = new Object[pars.length];
        Class<?>[] types = getTypes(candidateArgs);
        for (int i = 0; i < pars.length; i++) {
            int index = findCandidateIndex(pars[i], types);
            if (index >= 0) {
                args[i] = candidateArgs[index];
            } else {
                throw new IllegalArgumentException("Argument not found for parameter " + i + " of type " + pars[i]);
            }
        }
        return args;
    }

    public static Class<?>[] getTypes(Object... args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }

    public static int findCandidateIndex(Class<?> par, Class<?>... candidateArgs) {
        for (int i = 0; i < candidateArgs.length; i++) {
            Class<?> arg = candidateArgs[i];
            if (par.isAssignableFrom(arg)) {
                return i;
            }
        }
        return -1;
    }

    @SneakyThrows
    public static <T> T newInstance(Constructor<T> ctor, Object... candidateArgs) {
        Object[] args = getArgs(ctor.getParameterTypes(), candidateArgs);
        T instance = ctor.newInstance(args);
        return instance;
    }

    @SneakyThrows
    public static <T> T newInstance(Class<T> clss, Object... candidateArgs) {
        Constructor<T> ctor = findConstructor(clss, getTypes(candidateArgs));
        return newInstance(ctor, candidateArgs);
    }
}
