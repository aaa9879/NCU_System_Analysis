package app;
import java.sql.*;
import java.time.LocalDateTime;
import org.json.*;

import util.DBMgr;

public class AnnouncementHelper {
    private AnnouncementHelper() {
    }

    private static AnnouncementHelper ah;

    private Connection conn = null;
    private PreparedStatement pres = null;

    public static AnnouncementHelper getHelper() {
        if (ah == null) ah = new AnnouncementHelper();
        return ah;
    }

   //創建公告
    public JSONObject create(Announcement a) {
        String execute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;

        try {
            conn = DBMgr.getConnection();

            String sql = "INSERT INTO `sa`.`tbl_announcement`(`announcement_content`, `announcement_time`, `member_id`,`announcement_title`) VALUES (?, ?, ?,?)";
            //因為有放入建構子所以可以使用announcement的get方法
            String title =a.getTitle();
            String content = a.getContent();
            int id=a.getAdminId();

            pres = conn.prepareStatement(sql);
            pres.setString(1, content);
            pres.setTimestamp(2,Timestamp.valueOf(LocalDateTime.now()) );
            pres.setInt(3, id);
            pres.setString(4, title);
            row = pres.executeUpdate();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(pres, conn);
        }

        execute_sql = pres.toString();
        System.out.println(execute_sql);

        long end_time = System.nanoTime();
        long duration = (end_time - start_time);

        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("time", duration);
        response.put("row", row);

        return response;
    }

   //用announcement_id刪除公告
    public JSONObject deleteByID(int id) {
        String execute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;

        try {
            conn = DBMgr.getConnection();

            String sql = "DELETE FROM `sa`.`tbl_announcement` WHERE `announcement_id` = ? LIMIT 1";

            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);

            row = pres.executeUpdate();

            execute_sql = pres.toString();
            System.out.println(execute_sql);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(pres, conn);
        }

        long end_time = System.nanoTime();
        long duration = (end_time - start_time);

        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("time", duration);
        response.put("row", row);

        return response;
    }
    //更新公告
    public JSONObject update(Announcement announcement) {
        JSONArray jsa = new JSONArray();
        String execute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;

        try {
            conn = DBMgr.getConnection();

           
            String sql = "UPDATE `sa`.`tbl_announcement` SET `announcement_title` = ?, `announcement_content` = ? WHERE `announcement_id` = ?";
            //因為有放入建構子所以可以使用announcement的get方法
            String title = announcement.getTitle();
            String content = announcement.getContent();
            int announcement_id = announcement.getAnnouncement_id();

            pres = conn.prepareStatement(sql);
            pres.setString(1, title);
            pres.setString(2, content);
            pres.setInt(3, announcement_id);

            row = pres.executeUpdate();

            execute_sql = pres.toString();
            System.out.println(execute_sql);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(pres, conn);
        }

        long end_time = System.nanoTime();
        long duration = (end_time - start_time);

        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }

    //取得所有公告
    public JSONObject getAllAnnouncement() {
        Announcement announcement = null;
        JSONArray jsa = new JSONArray();
        String execute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;
        ResultSet rs = null;

        try {
            conn = DBMgr.getConnection();

            String sql = "SELECT * FROM `sa`.`tbl_announcement`";

            pres = conn.prepareStatement(sql);
            rs = pres.executeQuery();

            execute_sql = pres.toString();
            System.out.println(execute_sql);

            while (rs.next()) {
                row += 1;
               //從資料庫抓取資料
               int id = rs.getInt("member_id");
               String title = rs.getString("announcement_title");
               String content = rs.getString("announcement_content");
               Timestamp create_time = rs.getTimestamp("announcement_time");
               int announcement_id=rs.getInt("announcement_id");
               //放入建構子
               announcement = new Announcement(announcement_id,id, title, content, create_time);
               //可以用announcemnt的jsonobject的getdata方法取得資料
               jsa.put(announcement.getData());
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(rs, pres, conn);
        }

        long end_time = System.nanoTime();
        long duration= (end_time - start_time);

        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    //用announcement_id取得公告
    public JSONObject getAnnouncementByID(int id) {

        String execute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;

        try {
            conn = DBMgr.getConnection();

            String sql = "SELECT * FROM `sa`.`tbl_announcement` WHERE `announcement_id` = ? LIMIT 1";

            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);
            row = pres.executeUpdate();

            execute_sql = pres.toString();
            System.out.println(execute_sql);


        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close( pres, conn);
        }

        long end_time = System.nanoTime();
        long duration = (end_time - start_time);

        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("row", row);
        response.put("time", duration);


        return response;
    }
}