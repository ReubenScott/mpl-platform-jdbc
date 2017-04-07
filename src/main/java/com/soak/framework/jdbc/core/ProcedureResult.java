package com.soak.framework.jdbc.core;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * @author 
 * @main sunyujia@yahoo.cn
 * @date May 17, 2008 7:26:48 PM
 */
public class ProcedureResult {
  private List rs = new ArrayList();
  private List output = new ArrayList();
  private Object value;

  public void addRs(List list) {
    if (list.size() > 0)
      rs.add(list);
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public List getRs() {
    if (rs.size() == 1)
      return (List) rs.get(0);
    return rs;
  }

  public List getOutput() {
    return output;
  }

}
