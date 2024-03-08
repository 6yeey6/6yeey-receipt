package com.ibg.receipt.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @Description SPEL表达式解析工具类
 * @Date 2020/2/7
 * @Created by lvzhonglin
 */
public class SpelUtils {

    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     * 解析EL表达式，返回布尔计算记过
     * @param context   传值根对象
     * @param spelExpression EL表达式
     * @return
     */
    public static boolean parseSpel(EvaluationContext context, String spelExpression){
        Expression expression = parser.parseExpression(spelExpression);
        return expression.getValue(context, Boolean.class);
    }

    public static void main(String[] args) {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("dayCreditSearchCount", 1000);
        context.setVariable("dayCreditSearchCountLimit", 800);

        boolean result = parseSpel(context, "#dayCreditSearchCount + 1 > #dayCreditSearchCountLimit");
        System.out.println(result);

        context.setVariable("name", "张三");
        context.setVariable("name", "李四");
        String name = (String) context.lookupVariable("name");
        System.out.println(name);
    }
}
