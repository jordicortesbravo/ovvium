package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;

public class EmptyFilter implements MemberFilter<Member> {

    @Override
    public boolean match(Member member) {
        return true;
    }

}
