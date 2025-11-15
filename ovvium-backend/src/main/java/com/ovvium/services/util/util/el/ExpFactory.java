package com.ovvium.services.util.util.el;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExpFactory {

	private final ExpressionParser parser;
	private final ParserContext context;
	private final Map<String, Object> variables = new HashMap<String, Object>();

	public ExpFactory() {
		this("${", "}");
	}

	public ExpFactory(String prefix, String suffix) {
		this(new SpelExpressionParser(), new TemplateParserContext(prefix, suffix));
	}

	public ExpFactory registerVariable(String key, Object value) {
		variables.put(key, value);
		return this;
	}

	public Exp get(String expression) {
		return new Exp(parser.parseExpression(expression, context), Collections.unmodifiableMap(variables));
	}

	public static ExpFactory dollar() {
		return new ExpFactory();
	}

	public static ExpFactory hash() {
		return new ExpFactory("#{", "}");
	}

	public static ExpFactory percent() {
		return new ExpFactory("%{", "}");
	}
}
