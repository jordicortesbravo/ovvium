package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;
import java.util.regex.Pattern;

public class RegexFilter implements MemberFilter<Member> {

    private final Pattern pattern;

    public RegexFilter(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean match(Member member) {
        return pattern.matcher(member.getName()).matches();
    }
}
