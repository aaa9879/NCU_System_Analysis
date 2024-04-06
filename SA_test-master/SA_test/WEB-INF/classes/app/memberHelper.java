package app;
import java.sql.*;
import java.time.LocalDateTime;
import org.json.*;

import util.DBMgr;
public class memberHelper {
	private memberHelper() {
        
    }
    private static memberHelper mh;
    private Connection conn = null;
    private PreparedStatement pres = null;
    public static memberHelper getHelper() {
        if(mh == null) mh = new memberHelper();
        return mh;
    }
    
    public JSONObject createSystemAdmin(member m) {
    	/** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
    	/** 紀錄SQL總行數 */
        int row = 0;
        
        try {
        	conn = DBMgr.getConnection();
            String sql = "INSERT INTO `sa`.`tbl_member`(`member_name`, `member_password`, `member_phone`, `member_group`, `created_time`, `identity`, `member_account`)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?)";
            String name = m.getName();
            String account = m.getAccount();
            String password = m.getPassword();
            String phone = m.getPhone();
            String group=m.getGroup();
            String identity=m.getIdentity();
            
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, password);
            pres.setString(3, phone);
            pres.setString(4, group);
            pres.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pres.setString(6, identity); 
            pres.setString(7, account);
            
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
    
    public JSONObject create(member m) {
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
            String sql = "INSERT INTO `sa`.`tbl_member`(`member_name`, `member_password`, `member_phone`, `member_group`, `created_time`, `identity`, `member_account`)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            String name = m.getName();
            String account = m.getAccount();
            String password = m.getPassword();
            String phone = m.getPhone();
            String group=m.getGroup();
        
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, password);
            pres.setString(3, phone);
            pres.setString(4,group);
            pres.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pres.setString(6, "1");
            pres.setString(7, account);
            
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
    public boolean checkDuplicate(member m){
        /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `sa`.`tbl_member` WHERE `member_account` = ?";
            
            /** 取得所需之參數 */
            String account = m.getAccount();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, account);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
            rs.next();
            row = rs.getInt("count(*)");

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
        
        /** 
         * 判斷是否已經有一筆該電子郵件信箱之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    }
    public boolean checkPassword(String account,String password){
    	 /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `sa`.`tbl_member` WHERE `member_account` = ? AND `member_password` = ?";
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, account);
            pres.setString(2, password);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();
            /** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
            rs.next();
            row = rs.getInt("count(*)");

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
        
        /** 
         * 判斷是否已經有一筆該電子郵件信箱之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    }
    public String getIdentity(String account) {
    	String identity = null;
    	try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
             String sql ="SELECT identity FROM `sa`.`tbl_member` WHERE `member_account` = ? LIMIT 1";
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, account);
            /** 執行查詢 */
            ResultSet rs = pres.executeQuery();

            /** 檢查是否有查詢結果 */
            if (rs.next()) {
                identity = rs.getString("identity");
            }
           
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close( pres, conn);
        }
    	return identity;
    }
    
    public JSONObject getAllmember() {
        /** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
        member m = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `sa`.`tbl_member` WHERE identity = 1 ";

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
                String name = rs.getString("member_name");
                String  account= rs.getString("member_account");
                String password = rs.getString("member_password");
                Timestamp create_time= rs.getTimestamp("created_time");
                String identity = rs.getString("identity");
                String phone = rs.getString("member_phone");
                String group = rs.getString("member_group");
                /** 將每一筆會員資料產生一名新Member物件 */
                m = new member(id,name, password,phone,group,create_time,identity,account);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
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

        return response;
    }
    
    public JSONObject getAlladmin() {
        /** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之系統管理者之資料 */
        member m = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `sa`.`tbl_member` WHERE `identity`= 2";
            
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
                String name = rs.getString("member_name");
                String  account= rs.getString("member_account");
                String password = rs.getString("member_password");
                Timestamp create_time= rs.getTimestamp("created_time");
                String identity = rs.getString("identity");
                String phone = rs.getString("member_phone");
                String group = rs.getString("member_group");
                /** 將每一筆會員資料產生一名新Member物件 */
                m = new member(id,name, password,phone,group,create_time,identity,account);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
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

        return response;
    }
    public JSONObject getAllteacher() {
        /** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之系統管理者之資料 */
        member m = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `sa`.`tbl_member` WHERE `identity`= 3";
            
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
                String name = rs.getString("member_name");
                String  account= rs.getString("member_account");
                String password = rs.getString("member_password");
                Timestamp create_time= rs.getTimestamp("created_time");
                String identity = rs.getString("identity");
                String phone = rs.getString("member_phone");
                String group = rs.getString("member_group");
                /** 將每一筆會員資料產生一名新Member物件 */
                m = new member(id,name, password,phone,group,create_time,identity,account);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
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

        return response;
    }
    public JSONObject deleteByID(int id) {
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
            String deleteBorrowRecordSQL = "DELETE FROM `sa`.`tbl_borrow_record` WHERE `member_id` = ? AND `return_time` IS NOT NULL";
            try (PreparedStatement deleteBorrowRecordPS = conn.prepareStatement(deleteBorrowRecordSQL)) {
                deleteBorrowRecordPS.setInt(1, id);
                deleteBorrowRecordPS.executeUpdate();
            }
        
         // 刪除相關聯的 tbl_message 記錄
            String deleteMessageSQL = "DELETE FROM `sa`.`tbl_message` WHERE `article_id` IN (SELECT `article_id` FROM `sa`.`tbl_article` WHERE `member_id` = ?)";
            try (PreparedStatement deleteMessagePS = conn.prepareStatement(deleteMessageSQL)) {
                deleteMessagePS.setInt(1, id);
                deleteMessagePS.executeUpdate();
            }

            // 刪除相關聯的 tbl_announcement 記錄
            String deleteAnnouncementSQL = "DELETE FROM `sa`.`tbl_announcement` WHERE `member_id` = ?";
            try (PreparedStatement deleteAnnouncementPS = conn.prepareStatement(deleteAnnouncementSQL)) {
                deleteAnnouncementPS.setInt(1, id);
                deleteAnnouncementPS.executeUpdate();
            }

            // 刪除相關聯的 tbl_paid_record 記錄
            String deletePaidRecordSQL = "DELETE FROM `sa`.`tbl_paid_record` WHERE `member_id` = ?";
            try (PreparedStatement deletePaidRecordPS = conn.prepareStatement(deletePaidRecordSQL)) {
                deletePaidRecordPS.setInt(1, id);
                deletePaidRecordPS.executeUpdate();
            }

            // 刪除相關聯的 tbl_article 記錄
            String deleteArticleSQL = "DELETE FROM `sa`.`tbl_article` WHERE `member_id` = ?";
            try (PreparedStatement deleteArticlePS = conn.prepareStatement(deleteArticleSQL)) {
                deleteArticlePS.setInt(1, id);
                deleteArticlePS.executeUpdate();
            }


         // 刪除相關聯的 tbl_course_value 記錄
            String deleteCourseValueSQL = "DELETE FROM `sa`.`tbl_course_value` WHERE `member_id` = ?";
            PreparedStatement deleteCourseValuePS = conn.prepareStatement(deleteCourseValueSQL);
            deleteCourseValuePS.setInt(1, id);
            deleteCourseValuePS.executeUpdate();

            String deleteRegistrationRecordSQL = "DELETE FROM `sa`.`tbl_course_registration_record` WHERE `member_id` = ?";
            PreparedStatement deleteRegistrationRecordPS = conn.prepareStatement(deleteRegistrationRecordSQL);
            deleteRegistrationRecordPS.setInt(1, id);
            deleteRegistrationRecordPS.executeUpdate();
            String deleteHomeworkSQL = "DELETE FROM `sa`.`tbl_homework` WHERE `member_id` = ?";
            PreparedStatement deleteHomeworkPS = conn.prepareStatement(deleteHomeworkSQL);
            deleteHomeworkPS.setInt(1, id);
            deleteHomeworkPS.executeUpdate();
            /** SQL指令 */
            String sql = "DELETE FROM `sa`.`tbl_member` WHERE `member_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);
            /** 執行刪除之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
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
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        return response;
    }
    
    
    public JSONObject getByID(String id) {
        /** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
        member m = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `sa`.`tbl_member` WHERE `member_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int Id = rs.getInt("member_id");
                String name = rs.getString("member_name");
                String account = rs.getString("member_account");
                String password = rs.getString("member_password");
                Timestamp create_time = rs.getTimestamp("created_time");
                String identity = rs.getString("identity");
                String phone = rs.getString("member_phone");
                String group = rs.getString("member_group");
                
                /** 將每一筆會員資料產生一名新Member物件 */
                m = new member(Id,name,password,phone,group,create_time,identity,account);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
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

        return response;
    }
    public JSONObject update(member m) {
        /** 紀錄回傳之資料 */
        JSONArray jsa = new JSONArray();
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
            String sql = "Update `sa`.`tbl_member` SET `member_name` = ? ,`member_password` = ? , `member_phone` = ? , `member_group` = ? WHERE `member_id` = ?";
            /** 取得所需之參數 */
            String name = m.getName();
            int id = m.getID();
            String password = m.getPassword();
            String phone = m.getPhone();
            String group = m.getGroup();
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, password);
            pres.setString(3, phone);
            pres.setString(4, group);
            pres.setInt(5, id);
            /** 執行更新之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

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
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    public String getId(String account) {
    	String id = null; // 初始化為空值
    	try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
             String sql ="SELECT member_id FROM `sa`.`tbl_member` WHERE `member_account` = ? LIMIT 1";
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, account);
            /** 執行查詢 */
            ResultSet rs = pres.executeQuery();

            /** 檢查是否有查詢結果 */
            if (rs.next()) {
                id = rs.getString("member_id");
            }
           
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close( pres, conn);
        }
    	return id;
    }
}
