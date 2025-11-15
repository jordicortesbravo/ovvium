package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Method;

public class OvviumFilter implements MemberFilter<Method> {

    public static final String PREFIX = "get";

    @Override
    public boolean match(Method member) {
        return verifyName(member.getName()) //
                && member.getParameterTypes().length == 0 //
                && !member.getReturnType().equals(void.class);
    }

    private boolean verifyName(String name) {
        return name.startsWith(PREFIX) && name.length() > PREFIX.length();
    }

}
