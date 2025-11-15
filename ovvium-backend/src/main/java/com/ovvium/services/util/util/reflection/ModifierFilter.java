package com.ovvium.services.util.util.reflection;

import java.lang.reflect.Member;

/**
 * Filtre que comprova si el {@link Member Member} passat té els {@link java.lang.reflect.Modifier Modifier} definits al
 * constructor. Un modificador negatiu, significa que el Member no ha de tenir aquell modificador.
 * <p>
 * Per exemple, un filtre que deixi passar només els Membres que siguin public i no abstractes es crearia com:
 * <p>
 * {@code new ModifierFilter(Modifier.PUBLIC, -Modifier.ABSTRACT);}
 */
public class ModifierFilter<T extends Member> implements MemberFilter<Member> {

    private final int[] required;

    // TODO: Com a optimització, segurament es podrien guardar dos enters enlloc de tota la llista: una "fusió" amb tots els modifiers
    // positius i un amb els negatius. Però s'ha de provar bé.
    public ModifierFilter(int... required) {
        this.required = required;
    }

    @Override
    public boolean match(Member member) {
        int mod = member.getModifiers();
        for (int m : required) {
            if (m > 0) {
                if ((mod & m) == 0) {
                    return false;
                }
            } else {
                if ((mod & -m) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
