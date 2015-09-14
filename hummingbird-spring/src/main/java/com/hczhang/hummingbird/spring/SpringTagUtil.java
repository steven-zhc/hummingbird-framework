package com.hczhang.hummingbird.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Created by steven on 3/6/15.
 */
public class SpringTagUtil {
    public static boolean isSpringExp(String str) {

        if (str.startsWith("${") && str.endsWith("}")) {
            return true;
        }
        return false;
    }

    public static Object getExpValue(String value) {

        // Set up a new EL parser
        ExpressionParser parser = new SpelExpressionParser();
        String expstr =  "#{'" + value + "'}";
        Expression exp = parser.parseExpression( value );

        return exp.getValue();
    }

    public static Integer getIntExpValue(String value) {
        return (Integer) getExpValue(value);
    }
}
