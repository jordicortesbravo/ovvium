package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;

public interface MemberFilter<T extends Member> {

    boolean match(T member);

}
