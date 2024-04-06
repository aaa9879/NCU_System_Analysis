package app;
import java.sql.*;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import util.DBMgr;
public class messageHelper {
	private messageHelper() {
		
	}
	private static messageHelper msh;
	private Connection conn = null;
    private PreparedStatement pres = null;
    public static messageHelper getHelper() {
        if(msh == null) msh = new messageHelper();
        return msh;
    }
    
    public JSONObject create(message mes) {
    	/** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
    	/** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `sa`.`tbl_message`(`message_content`, `message_time`,`article_id`, `member_id`)"
                    + " VALUES(?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            String message_content = mes.getContent();            
            Timestamp message_time= mes.getMessage_time();
            int article_id= mes.getArticle_id();
            int member_id = mes.getMember_id();
            
        
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, message_content);
            pres.setTimestamp(2, message_time);
            pres.setInt(3, article_id);
            pres.setInt(4, member_id);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }
        exexcute_sql = pres.toString();
        System.out.println(exexcute_sql);

      
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);

        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("time", duration);
        response.put("row", row);

        return response;
    }

    public JSONObject getmessageByid(int id) {
        JSONObject result = new JSONObject();
        JSONArray messages = new JSONArray();
        String execute_sql = "";

        try {
            conn = DBMgr.getConnection();
            String sql = "SELECT * FROM `tbl_message` WHERE `article_id` = ?";
            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);  // Set the article_id parameter

            ResultSet rs = pres.executeQuery();

            while (rs.next()) {
                JSONObject message = new JSONObject();
                message.put("message_id", rs.getInt("message_id"));
                message.put("message_content", rs.getString("message_content"));
                message.put("message_time", rs.getTimestamp("message_time").toString());
                message.put("article_id", rs.getInt("article_id"));
                message.put("member_id", rs.getInt("member_id"));

                messages.put(message);
            }

            result.put("replies", messages);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(pres, conn);
        }

        execute_sql = pres.toString();
        System.out.println(execute_sql);

        return result;
    }

}

