package com.soak.common.metic;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

public class ExpressionUtil {

  

  /**
   * 公式计算
   * 
   * @param expression
   * @return
   */
  public static Number calculate(String expression) {
    /*
     * 初始化一个JexlContext对象，它代表一个执行JEXL表达式的上下文环境
     */
    JexlContext context = JexlHelper.createContext();
    Number number = null;
    Expression e;
    try {
      e = ExpressionFactory.createExpression(expression);
      // 对这个Expression对象求值，传入执行JEXL表达式的上下文环境对象
      number = (Number) e.evaluate(context);
      // 输出表达式求值结果
      System.out.println(e.getExpression() + " = " + number);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return number;
  }

}