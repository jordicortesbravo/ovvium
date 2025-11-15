package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SetterFilter implements MemberFilter<Method> {

    public static final String PREFIX = "set";

    private final boolean verifyReturnType;

    public SetterFilter() {
        this(true);
    }

    @Override
    public boolean match(Method member) {
        return (!verifyReturnType || member.getReturnType().equals(void.class) || member.getReturnType().equals(member.getDeclaringClass())) //
                && verifyName(member.getName()) //
                && member.getParameterTypes().length == 1;
    }

    private boolean verifyName(String name) {
        return name.startsWith(PREFIX) && name.length() > PREFIX.length();
    }

}
