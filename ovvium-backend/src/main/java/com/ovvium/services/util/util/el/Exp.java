package com.ovvium.services.util.util.el;

import com.ovvium.services.util.util.container.Maps;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Passar a spring
@RequiredArgsConstructor
@EqualsAndHashCode
public class Exp {

	private final Expression expression;
	private final Map<String, Object> variables;

	public Exp(Expression expression) {
		this(expression, Maps.mapSO());
	}

	public String eval() {
		return eval(String.class);
	}

	public <T> T eval(Class<T> clss) {
		return expression.getValue(createContext(), clss);
	}

	public String eval(Object any) {
		return eval(String.class, any);
	}

	/**
	 * Evaluates an expression. Decides type of object passed using reflection
	 */
	@SuppressWarnings("unchecked")
	public <T> T eval(Class<T> clss, Object any) {
		if (any instanceof Map) {
			return evalMap(clss, (Map<String, ?>) any);
		} else if (any instanceof Object[]) {
			return evalArray(clss, (Object[]) any);
		} else if (any instanceof List) {
			return evalList(clss, (List<?>) any);
		}
		return evalRoot(clss, any);
	}

	public String evalRoot(Object root) {
		return evalRoot(String.class, root);
	}

	public <T> T evalRoot(Class<T> clss, Object root) {
		return eval(context(root), clss);
	}

	public String evalArray(Object... variables) {
		return evalArray(String.class, variables);
	}

	public <T> T evalArray(Class<T> clss, Object... variables) {
		return evalList(clss, Arrays.asList(variables));
	}

	public String evalList(List<?> list) {
		return evalList(String.class, list);
	}

	public <T> T evalList(Class<T> clss, List<?> list) {
		val map = new HashMap<String, Object>();
		for (int i = 0; i < list.size(); i++) {
			map.put("_" + i, list.get(i));
		}
		return evalMap(clss, map);
	}

	public String evalMap(Map<String, ?> variables) {
		return evalMap(String.class, variables);
	}

	public <T> T evalMap(Class<T> clss, Map<String, ?> variables) {
		return eval(context(variables), clss);
	}

	@Override
	public String toString() {
		return expression.getExpressionString();
	}

	public boolean isLiteral() {
		return expression instanceof LiteralExpression;
	}

	protected EvaluationContext context(Object root) {
		val ctx = createContext();
		ctx.setRootObject(root);
		if (root instanceof Map) {
			ctx.addPropertyAccessor(new MapAccessor());
		}
		return ctx;
	}

	protected StandardEvaluationContext createContext() {
		val ctx = new StandardEvaluationContext();
		ctx.setVariables(variables);
		return ctx;
	}

	protected <T> T eval(EvaluationContext ctx, Class<T> clss) {
		return expression.getValue(ctx, clss);
	}

}
