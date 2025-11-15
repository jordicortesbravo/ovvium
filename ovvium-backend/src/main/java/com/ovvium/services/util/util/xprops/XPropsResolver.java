package com.ovvium.services.util.util.xprops;

import java.util.*;
import java.util.Map.Entry;

import org.springframework.expression.spel.SpelEvaluationException;

import com.ovvium.services.util.util.container.Pair;
import com.ovvium.services.util.util.el.Exp;
import com.ovvium.services.util.util.el.ExpFactory;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class XPropsResolver {

    private final ExpFactory factory;

    public XProps getResolved(XProps p) {

        List<Pair<String, Exp>> variables;
        List<SpelEvaluationException> exceptions;
        do {
            variables = getVariables(p);
            exceptions = new ArrayList<SpelEvaluationException>();
        } while (resolveExpressions(p, variables, exceptions));

        if (!exceptions.isEmpty()) {
            throw new XPropsResolverException(exceptions);
        }
        return p;
    }

    private List<Pair<String, Exp>> getVariables(XProps p) {
        val list = new ArrayList<Pair<String, Exp>>();
        for (Entry<String, String> e : p) {
            val exp = factory.get(e.getValue());
            if (!exp.isLiteral()) {
                list.add(Pair.makePair(e.getKey(), exp));
            }
        }
        return list;
    }

    private boolean resolveExpressions(XProps p, List<Pair<String, Exp>> variables, List<SpelEvaluationException> exceptions) {
        boolean modified = false;
        val it = variables.iterator();
        val map = getMap(p);
        while (it.hasNext()) {
            val e = it.next();
            try {
                val value = e.getSecond().evalMap(map);
                p.put(e.getFirst(), value);
                it.remove();
                modified = true;
            } catch (SpelEvaluationException ex) {
                exceptions.add(ex);
            }
        }
        return modified;
    }

    private Map<String, Object> getMap(XProps p) {
        val map = new HashMap<String, Object>();
        for (Entry<String, String> e : p) {
            val path = new ArrayList<String>(Arrays.asList(e.getKey().split("\\.")));
            insert(map, path, e.getValue());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private void insert(Map<String, Object> map, List<String> path, String value) {
        if (path.size() > 1) {
            val p = path.remove(0);
            val m = map.get(p);

            if (m == null) {
                val nm = new HashMap<String, Object>();
                map.put(p, nm);
                insert(nm, path, value);

            } else if (m instanceof Map) {
                insert((Map<String, Object>) m, path, value);

            }
            // TODO: throw exception when another type of value is already there?

        } else {
            map.put(path.get(0), value);
        }
    }

}
