package com.project.concurrency.utils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.concurrency.pojo.User;
import com.project.concurrency.utils.vo.RespBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用JDBC来连接数据库
 */
public class UserUtil {
     public static void createUser(int count) throws ClassNotFoundException, SQLException, IOException {
         // 生成count个用户
         List<User> userList = new ArrayList<>();
         for (int i = 0; i < count; i++) {
             User user = new User();
             user.setId(13000000000L+i);
             user.setNickname("user"+i);
             user.setSalt("1a2b3c");
             user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSalt()));
//             user.setHead("");
             user.setRegisterDate(new Date());
             user.setLastLoginDate(new Date());
             user.setLoginCount(1);
             userList.add(user);
         }
         // 插入数据库
         // 1. 获取连接
         Connection conn = getConnection();
//         conn.setAutoCommit(false);
         String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
         PreparedStatement prepareStatement = conn.prepareStatement(sql);
         for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            prepareStatement.setInt(1,user.getLoginCount());
            prepareStatement.setString(2,user.getNickname());
            prepareStatement.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
            prepareStatement.setString(4,user.getSalt());
            prepareStatement.setString(5, user.getPassword());
            prepareStatement.setLong(6,user.getId());
            prepareStatement.addBatch();
         }
         prepareStatement.executeBatch();
         prepareStatement.clearParameters();
         conn.close();

         // 登录, 然后生成user ticket
         String urlString = "http://localhost:8080/login/doLogin";
         File file = new File("C:\\Users\\24508\\Desktop\\secKill_config.txt");
         if(file.exists()){
             file.delete();
         }
         RandomAccessFile raf = new RandomAccessFile(file, "rw");
         raf.seek(0);
         for (int i = 0; i < userList.size(); i++) {
             User user = userList.get(i);
             // 请求url, 获取每个用户的ticket
             URL url = new URL(urlString);
             HttpURLConnection co = (HttpURLConnection) url.openConnection();
             co.setRequestMethod("POST");
             co.setDoOutput(true);
             OutputStream outputStream = co.getOutputStream();
             String params = "mobile=" + user.getId()+ "&password=" + MD5Util.inputPassToFromPass("123456");
             outputStream.write(params.getBytes());
             outputStream.flush();
             InputStream inputStream = co.getInputStream();
             ByteArrayOutputStream bout = new ByteArrayOutputStream();
             byte[] buff = new byte[1024];
             int len = 0;
             while ((len=inputStream.read(buff))>=0){
                 bout.write(buff,0,len);
             }
             inputStream.close();
             bout.close();
             String response = new String(bout.toByteArray());
             ObjectMapper mapper = new ObjectMapper();
             RespBean respBean = mapper.readValue(response, RespBean.class);
             String userTicket = (String) respBean.getObject();
             // 准备写入
             String row = user.getId()+","+userTicket;
             raf.seek(raf.length());
             raf.write(row.getBytes());
             raf.write("\r\n".getBytes());
         }
         raf.close();

     }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/concurrency?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useUnicode=true&useSSL=false";
        String username = "root";
        String password = "";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver); // 查找并加载这个类
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        createUser(3);
    }
}
