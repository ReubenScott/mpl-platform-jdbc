package com.soak.framework.jdbc.datasource;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.soak.common.util.StringUtil;

public class SSHDatabaseConnection {

  private final static Logger logger = LoggerFactory.getLogger(SSHDatabaseConnection.class);
  private static Session session = null;
  private static Connection connection = null;

  static String sshHost = "epl-sool.rhcloud.com"; // hostname or ip or SSH server
  static String sshuser = "577d176f0c1e66737100025e"; // SSH loging username
  static String sshPassword = "kf1260100"; // SSH login password
  static String sshKeyFilepath = "C:/Users/hongzhi/.ssh/id_rsa";
  // private static final int SSH_PORT = 22;
  private static int sshPort = 22; // remote SSH host port number

  static String remoteHost = "127.11.144.130"; // hostname or ip of your database server
  static int localPort = 3366; // local port number use to bind SSH tunnel , any free port can be used
  static int remotePort = 3306; // remote port number of your database
  String dbuserName = "adminZH1PKiH"; // database loging username
  String dbpassword = "1Vr7WxakgjMm"; // database login password

  String localSSHUrl = "localhost";

  /**
   * 利用JSch包实现远程主机SHELL命令执行
   * 
   * @param ip
   *          主机IP
   * @param user
   *          主机登陆用户名
   * @param psw
   *          主机登陆密码
   * @param port
   *          主机ssh2登陆端口，如果取默认值，传-1
   * @param privateKey
   *          密钥文件路径
   * @param passphrase
   *          密钥的密码
   */
  public static void sshShell(String ip, String user, String psw, int port, String privateKey, String passphrase) throws Exception {
    Session session = null;
    Channel channel = null;

    JSch jsch = new JSch();

    // 设置密钥和密码
    if (privateKey != null && !"".equals(privateKey)) {
      if (passphrase != null && "".equals(passphrase)) {
        // 设置带口令的密钥
        jsch.addIdentity(privateKey, passphrase);
      } else {
        // 设置不带口令的密钥
        jsch.addIdentity(privateKey);
      }
    }

    if (port <= 0) {
      // 连接服务器，采用默认端口
      session = jsch.getSession(user, ip);
    } else {
      // 采用指定的端口连接服务器
      session = jsch.getSession(user, ip, port);
    }

    // 如果服务器连接不上，则抛出异常
    if (session == null) {
      throw new Exception("session is null");
    }

    // 设置登陆主机的密码
    session.setPassword(psw);// 设置密码
    // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
    session.setConfig("StrictHostKeyChecking", "no");
    // 设置登陆超时时间
    session.connect(30000);

    try {
      // 创建sftp通信通道
      channel = (Channel) session.openChannel("shell");
      channel.connect(1000);

      // 获取输入流和输出流
      InputStream instream = channel.getInputStream();
      OutputStream outstream = channel.getOutputStream();

      // 发送需要执行的SHELL命令，需要用\n结尾，表示回车
      String shellCommand = "ls \n";
      outstream.write(shellCommand.getBytes());
      outstream.flush();

      // 获取命令执行的结果
      if (instream.available() > 0) {
        byte[] data = new byte[instream.available()];
        int nLen = instream.read(data);

        if (nLen < 0) {
          throw new Exception("network error.");
        }

        // 转换输出结果并打印出来
        String temp = new String(data, 0, nLen, "iso8859-1");
        System.out.println(temp);
      }
      outstream.close();
      instream.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      channel.disconnect();
      session.disconnect();
    }
  }

  /***
   * 获取实例
   * 
   * @return
   */
  public static Session getSSHSession() {
    if (session == null || !session.isConnected()) {
      createSShSession();
    }
    return session;
  }

  /**
   * F&uuml;hrt eine SQL-Abfrage aus und liefert eine Tabelle der Ergebnisse
   * 
   * @param query
   *          Der SQL-Code
   * @return Tabelle der Ergebnisse. Erste Dimension = Zeile; zweite Dimension = Spalte
   * @throws SQLException
   *           Tritt auf, wenn der SQL-Befehl eine Fehler ausl&ouml;ste. (Fehler wird per System.err.println(...) ausgegeben)
   */
  private static void createSShSession() {
    if (session == null || !session.isConnected()) {
      synchronized (session) {
        if (session == null || !session.isConnected()) {
          try {
            Properties config = new Properties();
            JSch jsch = new JSch();
            session = jsch.getSession(sshuser, sshHost, sshPort);
            if (StringUtil.isEmpty(sshKeyFilepath)) {
              session.setPassword(sshPassword);
            } else {
              jsch.addIdentity(sshKeyFilepath); // 私密
            }
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.connect();
            session.setPortForwardingL(localPort, remoteHost, remotePort);
            System.out.println("SSH Connected");
            System.out.println("localhost:" + localPort + " -> " + remoteHost + ":" + remotePort);
            System.out.println("Port Forwarded");
          } catch (JSchException e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

  private static void connectToServer(String dataBaseName) throws SQLException {
    // connectSSH();
    connectToDataBase(dataBaseName);
  }

  private static void connectToDataBase(String dataBaseName) throws SQLException {
    String dbuserName = "sf2_showpad_biz";
    String dbpassword = "lOAWEnL3K";
    int localPort = 8740; // any free port can be used
    String localSSHUrl = "localhost";
    try {

      // mysql database connectivity
      MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setServerName(localSSHUrl);
      dataSource.setPortNumber(localPort);
      dataSource.setUser(dbuserName);
      dataSource.setAllowMultiQueries(true);

      dataSource.setPassword(dbpassword);
      dataSource.setDatabaseName(dataBaseName);

      connection = dataSource.getConnection();

      System.out.print("Connection to server successful!:" + connection + "\n\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void closeConnections() {
    CloseDataBaseConnection();
    CloseSSHConnection();
  }

  private static void CloseDataBaseConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        System.out.println("Closing Database Connection");
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private static void CloseSSHConnection() {
    if (session != null && session.isConnected()) {
      System.out.println("Closing SSH Connection");
      session.disconnect();
    }
  }

  // works ONLY FOR single query (one SELECT or one DELETE etc)
  private static ResultSet executeMyQuery(String query, String dataBaseName) {
    ResultSet resultSet = null;

    try {
      connectToServer(dataBaseName);
      Statement stmt = connection.createStatement();
      resultSet = stmt.executeQuery(query);
      System.out.println("Database connection success");
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return resultSet;
  }

  public static void DeleteOrganisationReferencesFromDB(String organisationsLike) {
    try {
      connectToServer("ServerName");
      Statement stmt = connection.createStatement();

      ResultSet resultSet = stmt.executeQuery("select * from DB1");

      String organisationsToDelete = "";
      List<String> organisationsIds = new ArrayList<String>();

      // create string with id`s values to delete organisations references
      while (resultSet.next()) {
        String actualValue = resultSet.getString("id");
        organisationsIds.add(actualValue);
      }

      for (int i = 0; i < organisationsIds.size(); i++) {
        organisationsToDelete = " " + organisationsToDelete + organisationsIds.get(i);
        if (i != organisationsIds.size() - 1) {
          organisationsToDelete = organisationsToDelete + ", ";
        }
      }

      stmt.executeUpdate(" DELETE FROM `DB1`.`table1` WHERE `DB1`.`table1`.`organisation_id` in ( " + organisationsToDelete + " );");

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnections();
    }
  }

  public static List<String> getOrganisationsDBNamesBySubdomain(String organisationsLike) {
    List<String> organisationDbNames = new ArrayList<String>();
    ResultSet resultSet = executeMyQuery("select `DB`.organisation.dbname from `DB1`.organisation where subdomain like '" + organisationsLike + "%'", "DB1");
    try {
      while (resultSet.next()) {
        String actualValue = resultSet.getString("dbname");
        organisationDbNames.add(actualValue);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnections();
    }
    return organisationDbNames;
  }

  public static List<String> getAllDBNames() {
    // get all live db names incentral DB
    List<String> organisationDbNames = new ArrayList<String>();
    ResultSet resultSet = executeMyQuery("show databases", "DB1");
    try {
      while (resultSet.next()) {
        String actualValue = resultSet.getString("Database");
        organisationDbNames.add(actualValue);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnections();
    }
    return organisationDbNames;
  }

  public static void deleteDataBasesByName(List<String> DataBasesNamesList) {
    try {
      // connectSSH();
      int dataBasesAmount = DataBasesNamesList.size();
      for (int i = 0; i < dataBasesAmount; i++) {
        connectToDataBase(DataBasesNamesList.get(i));

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP database `" + DataBasesNamesList.get(i) + "`");

      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      CloseDataBaseConnection();
      closeConnections();
    }
  }

  private static void doSshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort)
      throws JSchException {
    final JSch jsch = new JSch();
    Session session = jsch.getSession(strSshUser, strSshHost, 22);
    session.setPassword(strSshPassword);

    final Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);

    session.connect();
    session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
  }

  public static HashMap queryOneAsMap(Connection conn, String sql, Object... params) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    HashMap hashRow = null;
    try {
      conn.setReadOnly(true);
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      ResultSetMetaData dbRsMd = rs.getMetaData();
      String[] colNames = new String[dbRsMd.getColumnCount()];
      for (int i = 1; i <= dbRsMd.getColumnCount(); i++) {
        colNames[i - 1] = dbRsMd.getColumnName(i).toLowerCase();
      }
      while (rs.next()) {
        hashRow = new HashMap();
        for (int i = 0; i < colNames.length; i++) {
          hashRow.put(colNames[i], rs.getObject(colNames[i]));
          System.out.println(colNames[i] + " : " + rs.getObject(colNames[i]));
        }
      }
      return hashRow;
    } catch (SQLException e) {
      logger.info(e.getMessage());
      return null;
    } finally {
      // this.release(dbalias,conn, ps, rs);
    }
  }

  public static void main(String[] args) {
    String sshHost = "epl-sool.rhcloud.com"; // hostname or ip or SSH server
    String sshuser = "577d176f0c1e66737100025e"; // SSH loging username
    String sshPassword = "kf1260100"; // SSH login password
    String sshKeyFilepath = Thread.currentThread().getContextClassLoader().getResource(".ssh/id_rsa").getPath();
  
    int sshPort = 22; // remote SSH host port number

    String remoteHost = "127.11.144.130"; // hostname or ip of your database server
    int localPort = 3366; // local port number use to bind SSH tunnel , any free port can be used
    int remotePort = 3306; // remote port number of your database
    String dbuserName = "adminZH1PKiH"; // database loging username
    String dbpassword = "1Vr7WxakgjMm"; // database login password

    String localSSHUrl = "localhost";
    /***************/
    String driverName = "com.mysql.jdbc.Driver";

    try {
      java.util.Properties config = new java.util.Properties();
      JSch jsch = new JSch();
      session = jsch.getSession(sshuser, sshHost, sshPort);
      jsch.addIdentity(sshKeyFilepath);
      config.put("StrictHostKeyChecking", "no");
      config.put("ConnectionAttempts", "3");
      session.setConfig(config);
      // session.setPassword(sshPassword);
      session.connect();
      session.setPortForwardingL(localPort, remoteHost, remotePort);

      System.out.println("SSH Connected");

      Class.forName(driverName).newInstance();
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:" + localPort + "/attendance", dbuserName, dbpassword);

      String sql = "SELECT * FROM dim_scheduletype";
      queryOneAsMap(conn, sql);

      System.out.println("localhost:" + localPort + " -> " + remoteHost + ":" + remotePort);
      System.out.println("Port Forwarded");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
