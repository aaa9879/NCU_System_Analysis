package app;
import java.sql.*;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import util.DBMgr;
public class articleHelper {
	private articleHelper() {
		
	}
	private static articleHelper ah;
	private Connection conn = null;
    private PreparedStatement pres = null;
    public static articleHelper getHelper() {
        if(ah == null) ah = new articleHelper();
        return ah;
    }
    //創建文章
    public JSONObject create(article a) {
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
            String sql = "INSERT INTO `sa`.`tbl_article`(`title`, `article_content`, `article_time`, `member_id`)"
                    + " VALUES(?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            String title = a.getTitle();
            String article_content= a.getArticle_content();
            Timestamp article_time= a.getArticle_time();
            int member_id = a.getMember_id();
            
        
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, title);
            pres.setString(2, article_content);
            pres.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pres.setInt(4,member_id);
            
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
    //取得所有文章
    public JSONObject getArticle() {
        /** 新建一個 Article物件之 a 變數，用於紀錄每一位查詢回之article資料 */
        article a = null;
        /** 用於儲存所有檢索回之article，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            //用article_time去抓出,oreder by 就是用順序抓，然後寫desc就是到著抓所以代表最新的會先被抓出來
            String sql = "SELECT * FROM `sa`.`tbl_article`ORDER BY `article_time` DESC;";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int id = rs.getInt("member_id");
                String title = rs.getString("title");
                String  article_content= rs.getString("article_content");
                Timestamp article_time= rs.getTimestamp("article_time");
                int article_id = rs.getInt("article_id");
                
                /** 將每一筆article資料產生一名新article物件 */
                a = new article(id,title, article_content,article_time,article_id);
                /** 取出該article之資料並封裝至 JSONsonArray 內 */
                jsa.put(a.getData());
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);
        
        
        System.out.println(response.toString());

        return response;
    }
    public JSONObject deleteByID(int article_id) {
        /** 記錄實際執行之 SQL 指令 */
        String execute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄 SQL 總行數 */
        int row = 0;

        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();

            // 刪除 tbl_message 中相依的訊息
            String deleteMessageSQL = "DELETE FROM `sa`.`tbl_message` WHERE `article_id` = ?";
            PreparedStatement deleteMessagePS = conn.prepareStatement(deleteMessageSQL);
            deleteMessagePS.setInt(1, article_id);
            deleteMessagePS.executeUpdate();

            /** SQL指令 */
            String sql = "DELETE FROM `sa`.`tbl_article` WHERE `article_id` = ?";

            /** 將參數回填至 SQL 指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, article_id);
            
            /** 執行刪除之 SQL 指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的 SQL 指令，並印出 **/
            execute_sql = pres.toString();
            System.out.println(execute_sql);

        } catch (SQLException e) {
            /** 印出 JDBC SQL 指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(null, pres, null);
        }

        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);

        /** 將 SQL 指令、花費時間與影響行數，封裝成 JSONObject 回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("row", row);
        response.put("time", duration);
        return response;
    }

   

}
