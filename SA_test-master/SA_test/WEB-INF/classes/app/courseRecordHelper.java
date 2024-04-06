 package app;

import java.sql.*;
import org.json.*;
import util.DBMgr;
public class courseRecordHelper {
	private courseRecordHelper() {      
    }
	private static courseRecordHelper crh;
	private Connection conn = null;
	private PreparedStatement pres = null;
	public static courseRecordHelper getHelper() {
		if(crh == null) crh = new courseRecordHelper();
		return crh; 
	}
	//新增社課報名紀錄
	public JSONObject createrecord(int member_id ,int course_id){
		String exexcute_sql = "";
		long start_time = System.nanoTime();
		int row = 0;
		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			
			String sql = "INSERT INTO `sa`.`tbl_course_registration_record`(`member_id`,`course_id`)"
	                   + " VALUES( ?, ?)";
			pres = conn.prepareStatement(sql);
			pres.setInt(1,member_id);
			pres.setInt(2,course_id );
			pres.executeUpdate();
			
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
	//取消報名
	public JSONObject deleterecord(int member_id ,int course_id){
		String exexcute_sql = "";
		long start_time = System.nanoTime();
		int row = 0;
		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			
			String sql = "DELETE FROM `sa`.`tbl_course_registration_record` WHERE `member_id` = ? AND `course_id` = ? LIMIT 1";

			pres = conn.prepareStatement(sql);
			pres.setInt(1,member_id);
			pres.setInt(2,course_id );
			pres.executeUpdate();
			
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
	//檢視社員報名紀錄
	 public JSONObject getAllRecordById(int id) {
	        JSONArray jsa = new JSONArray();
	        String exexcute_sql = "";
	        long start_time = System.nanoTime();
	        int row = 0;
	        ResultSet rs = null;
	        course c = null;
	        try {
	            conn = DBMgr.getConnection();
	            String sql = "SELECT cr.course_registration_record_id,c.course_start_time, c.member_id, cr.course_id, c.course_name, c.course_time, c.course_location " +
	                    "FROM `sa`.`tbl_course_registration_record` cr " +
	                    "JOIN `sa`.`tbl_course` c ON cr.course_id = c.course_id " +
	                    "WHERE cr.member_id = ?";

	            pres = conn.prepareStatement(sql);
	            pres.setInt(1,id);
	            rs = pres.executeQuery();
	            exexcute_sql = pres.toString();
	            System.out.println(exexcute_sql);
	            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
	            while(rs.next()) {
	                /** 每執行一次迴圈表示有一筆資料 */
	                row += 1;
	                String course_name = rs.getString("c.course_name");
	                String course_time = rs.getString("c.course_time");
	                String course_location = rs.getString("c.course_location");
	                int teacher_id = rs.getInt("c.member_id");
	                int cid = rs.getInt("cr.course_id");
	                Timestamp course_start_time=rs.getTimestamp("c.course_start_time");
	                c = new course(cid,course_name,course_start_time,teacher_id,course_time,course_location);
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
	 public JSONObject getAllRecord() {
	        JSONArray jsa = new JSONArray();
	        String exexcute_sql = "";
	        long start_time = System.nanoTime();
	        int row = 0;
	        ResultSet rs = null;
	        course c = null;
	        try {
	            conn = DBMgr.getConnection();
	            String sql = "SELECT c.member_id AS teacher_id, cr.course_id, c.course_name, c.course_time,m.member_id,m.member_name " +
	                    "FROM `sa`.`tbl_course_registration_record` cr " +
	                    "JOIN `sa`.`tbl_course` c ON cr.course_id = c.course_id " +
	                    "JOIN `sa`.`tbl_member` m ON cr.member_id = m.member_id";

	            pres = conn.prepareStatement(sql);
	            rs = pres.executeQuery();
	            exexcute_sql = pres.toString();
	            System.out.println(exexcute_sql);
	            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
	            while(rs.next()) {
	                /** 每執行一次迴圈表示有一筆資料 */
	                JSONObject tmp = new JSONObject();
	            	row += 1;
	                String course_name = rs.getString("c.course_name");
	                tmp.put("course_name", course_name);
	                String course_time = rs.getString("c.course_time");
	                tmp.put("course_time", course_time);
	                String member_name = rs.getString("m.member_name");
	                tmp.put("member_name", member_name);
	                int teacher_id = rs.getInt("teacher_id");
	                tmp.put("teacher_id", teacher_id);
	                int cid = rs.getInt("cr.course_id");
	                tmp.put("course_id",cid);
	                int member_id = rs.getInt("m.member_id");
	                tmp.put("member_id", member_id);
	                
	               
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
