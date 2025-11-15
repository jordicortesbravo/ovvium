package com.ovvium.services.util.util.reflection;

import java.lang.reflect.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypeFilter implements MemberFilter<Member> {

    private final Class<?> requiredType;

    @Override
    public boolean match(Member member) {

        Class<?> type;
        if (member instanceof Field) {
            type = ((Field) member).getType();
        } else if (member instanceof Method) {
            type = ((Method) member).getReturnType();
        } else {
            throw new IllegalArgumentException("Member must be an instance of Field or Method. Found: " + member);
        }

        if (type.isPrimitive()) {
            type = ReflectionUtils.getBoxed(type);
        }

        return requiredType.isAssignableFrom(type);
    }

}
