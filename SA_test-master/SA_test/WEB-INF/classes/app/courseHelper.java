package app;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.*;
import app.course;
import util.DBMgr;

public class courseHelper {
	private static courseHelper ch;
	private Connection conn = null;
	private PreparedStatement pres = null;
	public courseHelper(){
		
	}
	 public static courseHelper getHelper() {
        if(ch == null) ch = new courseHelper();
        return ch;
    }
	   //取得所有課程
	   public JSONObject getAllcourse() {
	        course c = null;

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
	            String sql = "SELECT * FROM `sa`.`tbl_course`";
	            
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
	                int course_id = rs.getInt("course_id");
	                String course_name = rs.getString("course_name");
	                String  course_time= rs.getString("course_time");
	                int member_id = rs.getInt("member_id");
	                Timestamp course_start_time	= rs.getTimestamp("course_start_time");
	                String  course_location= rs.getString("course_location");
	                c = new course(course_id,course_name, course_start_time,member_id,course_time,course_location);
	                jsa.put(c.getData());
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
	   //新增社課評價
	   public JSONObject createCourseRating(int member_id,int course_id,int course_value) {
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
	            String sql = "INSERT INTO `sa`.`tbl_course_value` (`member_id`, `course_id`, `course_value`) VALUES (?, ?, ?)";

	        
	            
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1, member_id);
	            pres.setInt(2, course_id);
	            pres.setInt(3, course_value );
	           
	            
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
	   //得到社課評價
	   public int getRating(int member_id,int course_id) {
	    	int rating = 0; // 初始化為空值
	    	try {
	            /** 取得資料庫之連線 */
	            conn = DBMgr.getConnection();
	            /** SQL指令 */
	             String sql ="SELECT course_value FROM `sa`.`tbl_course_value` WHERE `member_id` = ? AND `course_id` = ? LIMIT 1";
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1, member_id);
	            pres.setInt(2, course_id);
	            /** 執行查詢 */
	            ResultSet rs = pres.executeQuery();

	            /** 檢查是否有查詢結果 */
	            if (rs.next()) {
	                rating = rs.getInt("course_value");
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
	    	return rating;
	    }
	   //繳交作業
	   public JSONObject submitHomework(int member_id,int course_id,String content) {
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
	            String sql = "INSERT INTO `sa`.`tbl_homework` (`member_id`, `course_id`,`content`,`homework_time`) VALUES (?, ?, ?,?)";

	        
	            
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1, member_id);
	            pres.setInt(2, course_id);
	            pres.setString(3, content );
	            pres.setTimestamp(4,Timestamp.valueOf(LocalDateTime.now()));
	           
	            
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
	   //新增社課
	   public JSONObject createCourse(String course_name,Timestamp course_start_time,int member_id,String course_location,String time) {
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
	            String sql = "INSERT INTO `sa`.`tbl_course` (`course_name`, `course_start_time`, `member_id`, `course_time`, `course_location`) VALUES (?, ?, ?,?,?)";

	        
	            
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setString(1, course_name);
	            pres.setTimestamp(2,course_start_time );
	            pres.setInt(3, member_id);
	            pres.setString(4, time);
	            pres.setString(5, course_location );
	            
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
	   //取得所有作業
	   public JSONObject getAllhomework(int teacher_id) {
	        course c = null;

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
	            String sql = "SELECT h.member_id, h.content, h.homework_time, h.score, h.course_id " +
	                    "FROM `sa`.`tbl_course` c " +
	                    "JOIN `sa`.`tbl_homework` h ON h.course_id = c.course_id " +
	                    "WHERE c.member_id = ?";

	            
	            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1,teacher_id);
	            rs = pres.executeQuery();

	            /** 紀錄真實執行的SQL指令，並印出 **/
	            exexcute_sql = pres.toString();
	            System.out.println(exexcute_sql);
	            
	            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
	            while (rs.next()) {
	                JSONObject tmp = new JSONObject();
	                /** 每執行一次迴圈表示有一筆資料 */
	                row += 1;

	                /** 將 ResultSet 之資料取出 */
	                String content = rs.getString("content");
	                tmp.put("content", content);
	                int student_id = rs.getInt("member_id");
	                tmp.put("student_id", student_id);

	                String originalHomeworkTime = rs.getString("homework_time");
	                LocalDateTime originalDateTime = LocalDateTime.parse(originalHomeworkTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	                LocalDateTime adjustedDateTime = originalDateTime.plusHours(8);

	                String adjustedHomeworkTime = adjustedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	                tmp.put("homework_time", adjustedHomeworkTime);

	                String score = rs.getString("score");
	                tmp.put("score", score);
	                int course_id = rs.getInt("course_id");
	                tmp.put("course_id",course_id);

	                jsa.put(tmp);
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
	    //更改分數
	    public JSONObject updateScore(int student_id,int course_id,int score) {
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
	            String sql = "Update `sa`.`tbl_homework` SET `score` = ?   WHERE `member_id` = ? AND `course_id` = ?";
	            ;
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1, score);
	            pres.setInt(2, student_id);
	            pres.setInt(3, course_id);
	   
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
	    public JSONObject getHomeworkById(int member_id,int course_id) {
	        course c = null;

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
	            String sql = "SELECT *" +
	                    "FROM `sa`.`tbl_homework` h " +
	                    "WHERE h.member_id = ? AND h.course_id=?";

	            
	            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1,member_id);
	            pres.setInt(2,course_id);
	            rs = pres.executeQuery();

	            /** 紀錄真實執行的SQL指令，並印出 **/
	            exexcute_sql = pres.toString();
	            System.out.println(exexcute_sql);
	            
	            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
	            while (rs.next()) {
	                JSONObject tmp = new JSONObject();
	                /** 每執行一次迴圈表示有一筆資料 */
	                row += 1;

	                /** 將 ResultSet 之資料取出 */
	                String content = rs.getString("content");
	                tmp.put("content", content);
	

	               
	                String score = rs.getString("score");
	                tmp.put("score", score);
	             

	                jsa.put(tmp);
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
}
