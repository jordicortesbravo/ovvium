package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import lombok.val;

public class MethodFilter extends RegexFilter {

    private final Object[] args;

    public MethodFilter(String pattern, Object... parameters) {
        super(pattern);
        this.args = parameters;
    }

    @Override
    public boolean match(Member member) {
        if (!super.match(member)) {
            return false;
        }
        if (member instanceof Method) {
            val m = (Method) member;
            if (m.getParameterTypes().length != args.length) {
                return false;
            }
            for (int i = 0; i < args.length; i++) {
                val paramClass = m.getParameterTypes()[i];
                if (!doArgMatch(args[i], paramClass)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean doArgMatch(Object arg, Class<?> paramClass) {
        if (arg == null || paramClass.isAssignableFrom(arg.getClass())) {
            return true;
        }
        return paramClass.equals(ReflectionUtils.getPrimitive(arg.getClass()));
    }
}
