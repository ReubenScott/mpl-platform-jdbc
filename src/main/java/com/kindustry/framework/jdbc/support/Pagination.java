package com.kindustry.framework.jdbc.support;

import java.io.Serializable;

/**
 * 分页BEAN
 * @author loyin xiongzj
 * @version 1.0 2012-02-08
 */
public class Pagination implements Serializable {
	
	/**
   * 
   */
  private static final long serialVersionUID = -6376268396595297655L;

  /**
	 * 页面显示数
	 */
	private int pageSize ;

	/**
	 * 当前页
	 */
	private int currentPage ;
	
	/**
	 * 总数据数目 
	 */
	private long totalCount ;
	
	/**
	 * 起始数据 每页起始数据 从0开始 不可写   起始行的行号
	 */
	private long startIndex ;
	
	/**
	 * 总页数 不可写
	 */
	private long totalPageCount;
	

  /**
   * 数据集
   */
  private Object items = null;
  

  public Pagination(Object items, long totalCount, int startIndex, int pageSize) {
    this.pageSize = pageSize;
    this.totalCount = totalCount;
    this.items = items;
    this.startIndex = startIndex;
    setTotalPageCount();
    setCurrentPage();
  }

  private void setCurrentPage() {
    if (totalCount % pageSize == 0){
      this.totalPageCount = totalCount / pageSize;
    }else{
      this.totalPageCount =  totalCount / pageSize + 1;
    }
  }
  
  private void setTotalPageCount() {
    if (totalCount % pageSize == 0){
      this.totalPageCount = totalCount / pageSize;
    }else{
      this.totalPageCount =  totalCount / pageSize + 1;
    }
  }

  public long getCurrentPage() {
    return currentPage ;
  }
  
  public long getTotalPageCount() {
    return totalPageCount ;
  }

  public long getTotalCount() {
    return totalCount;
  }

  public long getStartIndex() {
    return startIndex;
  }
  
  public Object getItems() {
    return items;
  }

  
  
//  public void setTotalCount(long totalCount) {
//    if (totalCount > 0) {
//      this.totalCount = totalCount;
//      int count = totalCount / pageSize;
//      if (totalCount % pageSize > 0)
//        count++;
//      this.pageCount = count;
//      indexes = new int[count];
//      for (int i = 0; i < count; i++) {
//        indexes[i] = pageSize * i;
//      }
//    } else {
//      this.totalCount = 0;
//    }
//  }

  
  

	/*String getSql(Connection conn, String tables, String cond, int curpage,
			String orderBy, String columns) {
		DatabaseMetaData dmd = null;
		StringBuffer sb = new StringBuffer();
		try {
			dmd = conn.getMetaData();
			String dbName = dmd.getDatabaseProductName();
			sb.append("select");
			if ("MySQL".equals(dbName)) {
				sb.append(columns);
				sb.append(" from ");
				sb.append(tables);
				sb.append(" where 1=1 ");
				sb.append(cond);
				sb.append(" order by ");
				sb.append(orderBy);
				sb.append(" limit ");
				sb.append((curpage - 1) * pageUtil);
				sb.append(", ");
				sb.append(pageUtil);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}*/

	// public Page pagination(String columns,String cond,int curpage,String
	// orderBy,String tables,String id){
	// Page page=new Page();
	// Connection conn=null;
	// db=new BaseLogic().getDb();
	// try{
	// conn=db.getConnection();
	// String countSql="select count(*) from "+tables+" where 1=1 "+cond;
	// Object obj=db.queryForObject(conn, countSql, null);
	// int pageCount =Integer.parseInt(obj.toString());
	// page.setPageCount(pageCount);
	// int pageSize=pageCount%pageUtil>0? pageCount/pageUtil+1 :
	// pageCount/pageUtil;
	// page.setPageSize(pageSize);
	// String sql=getSql(conn,tables,cond,curpage,orderBy,columns);
	// List<Map> list=db.queryApp(conn, sql, null);
	// page.setDataList(list);
	// page.setPageUtil(pageUtil);
	// page.setCurpage(curpage);
	// }catch(Exception e){
	// e.printStackTrace();
	// }finally{
	// db.close(conn);
	// }
	// return page;
	// }
	// public static void main(String [] args){
	// JdbcCore db=new
	// JdbcCore(Contants.DRIVER,Contants.URL,Contants.USER,Contants.PASSWORD);
	// Pagination p=new Pagination(db,2);
	// //Connection conn=db.getConnection();
	// Page page= p.pagination("*", "", 1, "deptID", "deptinfo", "deptID");
	// System.out.println(page.getCurpage()+" "+page.getPageCount()+"
	// "+page.getPageSize());
	// }
}
