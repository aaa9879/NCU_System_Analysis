package app;

import java.sql.*;
import java.time.LocalDateTime;

import org.json.*;
import util.DBMgr;
public class borrowrecordHelper {
	private borrowrecordHelper() {      
    }
	private static borrowrecordHelper br;
	private Connection conn = null;
	private PreparedStatement pres = null;
	public static borrowrecordHelper getHelper() {
		if(br == null) br = new borrowrecordHelper();
		return br;
	}
	//新增借用紀錄
	public JSONObject createrecord(borrowrecord b,int instid,int memid){
		String exexcute_sql = "";
		long start_time = System.nanoTime();
		int row = 0;
		boolean inspect=false;
		Timestamp check;
		String change="";
		JSONObject response = new JSONObject();
		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			
			//如果有人還沒歸還又借的判斷
			String sqlcheck2 = "SELECT `return_time` FROM `sa`.`tbl_borrow_record` WHERE `member_id` = ? ";
			pres = conn.prepareStatement(sqlcheck2);
			pres.setInt(1, memid);
			ResultSet rscheck = pres.executeQuery();
			//把全部的return_time都抓出來，只要裡面有字串null就不能借
			while(rscheck.next()) {
				check = rscheck.getTimestamp("return_time");
				change=String.valueOf(check);
				//如果改社員的資料裡面找到null就跳出，代表他不能借用然後inspect設成true
				if(change=="null") {
					inspect=true;
					break;
				}
			}
			//代表沒有null可以借用
			if(inspect==false) {
				//新增借用紀錄
				/** SQL指令 */
				String sql = "INSERT INTO `sa`.`tbl_borrow_record`(`borrow_time`, `instrument_id`, `member_id`)"
                   + " VALUES(?, ?, ?)";
          
				/** 取得所需之參數 */
				Timestamp borrow_time = b.getBorrow_time();
				long borrowTimeInMillis = borrow_time.getTime();
				long eightHours = borrowTimeInMillis + (8 * 60 * 60 * 1000);
				Timestamp eightHoursLater = new Timestamp(eightHours);

				int instrument_id = b.getInstrument_id();
				int member_id = b.getMember_id();  
          
				/** 將參數回填至SQL指令當中 */
				pres = conn.prepareStatement(sql);
				pres.setTimestamp(1, eightHoursLater);
				pres.setInt(2, instrument_id);
				pres.setInt(3, member_id);
				pres.executeUpdate();
				//資料庫器材數量-1
				String sqlQuantity = "SELECT `instrument_quantity` FROM `sa`.`tbl_instrument` WHERE `instrument_id` = ? LIMIT 1";

				pres = conn.prepareStatement(sqlQuantity);
				pres.setInt(1, instid);
				ResultSet rsQuantity = pres.executeQuery();

				int currentQuantity = 0;
				if (rsQuantity.next()) {
					currentQuantity = rsQuantity.getInt("instrument_quantity");
				}
				//如果數量大於0才能借
				if (currentQuantity > 0) {
					String sqlUpdate = "UPDATE `sa`.`tbl_instrument` SET `instrument_quantity` = ? WHERE `instrument_id` = ?";
					pres = conn.prepareStatement(sqlUpdate);
					pres.setInt(1, currentQuantity - 1);
					pres.setInt(2, instid);
					pres.executeUpdate();
				}
				response.put("check","canborrow");
			}
			else {
				response.put("check","can'tborrow");
			}
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

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		response.put("sql", exexcute_sql);
		return response;
  }
	//特定社員的借用資料
	public JSONObject getByID(int id) {
		borrowrecord b = null;
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
			String sql = "SELECT b.borrow_record_id,m.member_name,i.instrument_name,b.borrow_time,b.return_time,i.instrument_id "+
					     "FROM `sa`.`tbl_borrow_record` b "+
					     "JOIN `sa`.`tbl_member` m ON b.member_id = m.member_id "+
					     "JOIN `sa`.`tbl_instrument` i ON b.instrument_id = i.instrument_id "+
					     "WHERE b.member_id = ? ";
    
			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, id);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);
			while(rs.next()) {
				row += 1;
				//因為我使用join所以沒有建構子可以用所以我就用jsonobject的put放到jsonarray裡面
				int borrowrecord_id = rs.getInt("borrow_record_id");
				String member_name=rs.getString("member_name");
				String instrument_name=rs.getString("instrument_name");
				Timestamp borrow_time=rs.getTimestamp("borrow_time");
				Timestamp return_time=rs.getTimestamp("return_time");
				int instrument_id=rs.getInt("instrument_id");
				JSONObject recordObject = new JSONObject();
				recordObject.put("borrow_record_id", borrowrecord_id);
			    recordObject.put("member_name", member_name);
				recordObject.put("instrument_name", instrument_name);
				recordObject.put("borrow_time",borrow_time);
				recordObject.put("return_time",return_time);
				recordObject.put("instrument_id",instrument_id);
				jsa.put(recordObject);
				
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
	//檢視所有借用紀錄
	 public JSONObject getAllBorrowRecord() {
		 	borrowrecord b = null;
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
	            String sql = "SELECT b.borrow_record_id,m.member_name,i.instrument_name,b.borrow_time,b.return_time ,i.instrument_id,i.instrument_quantity "+
					     "FROM `sa`.`tbl_borrow_record` b "+
					     "JOIN `sa`.`tbl_member` m ON b.member_id = m.member_id "+
					     "JOIN `sa`.`tbl_instrument` i ON b.instrument_id = i.instrument_id ";
	            
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
					int borrowrecord_id = rs.getInt("borrow_record_id");
					String member_name=rs.getString("member_name");
					String instrument_name=rs.getString("instrument_name");
					Timestamp return_time=rs.getTimestamp("return_time");
					Timestamp borrow_time=rs.getTimestamp("borrow_time");
					int instrument_id=rs.getInt("instrument_id");
					int instrument_quantity=rs.getInt("instrument_quantity");
					JSONObject recordObject = new JSONObject();
					recordObject.put("borrow_record_id", borrowrecord_id);
				    recordObject.put("member_name", member_name);
					recordObject.put("instrument_name", instrument_name);
					recordObject.put("return_time",return_time);
					recordObject.put("borrow_time",borrow_time);
					recordObject.put("instrument_id",instrument_id);
					recordObject.put("instrument_quantity", instrument_quantity);
					/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
	                jsa.put(recordObject);
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
	 //為社員取消登記
	  public JSONObject deleteByID(int id,int instrument_id) {
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
	            String sql = "DELETE FROM `sa`.`tbl_borrow_record` WHERE `borrow_record_id` = ? LIMIT 1";
	            
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setInt(1, id);
	            /** 執行刪除之SQL指令並記錄影響之行數 */
	            row = pres.executeUpdate();
	            //資料庫器材數量+1
				String sqlQuantity = "SELECT `instrument_quantity` FROM `sa`.`tbl_instrument` WHERE `instrument_id` = ? LIMIT 1";

				pres = conn.prepareStatement(sqlQuantity);
				pres.setInt(1,instrument_id );
				ResultSet rsQuantity = pres.executeQuery();

				int currentQuantity = 0;
				if (rsQuantity.next()) {
					currentQuantity = rsQuantity.getInt("instrument_quantity");
				}
					String sqlUpdate = "UPDATE `sa`.`tbl_instrument` SET `instrument_quantity` = ? WHERE `instrument_id` = ?";
					pres = conn.prepareStatement(sqlUpdate);
					pres.setInt(1, currentQuantity + 1);
					pres.setInt(2, instrument_id);
					pres.executeUpdate();
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
	  //系統管理者歸還器材時會更新歸還時間
	  public JSONObject update(int borrow_record_id,int instrument_id) {
	        /** 記錄實際執行之SQL指令 */
	        String exexcute_sql = "";
	        /** 紀錄程式開始執行時間 */
	        long start_time = System.nanoTime();
	        /** 紀錄SQL總行數 */
	        int row = 0;
	        
	        try {
	            /** 取得資料庫之連線 */
	            conn = DBMgr.getConnection();
	           //輸入return_time
	            String sql = "Update `sa`.`tbl_borrow_record` SET `return_time` = ?  WHERE `borrow_record_id` = ?";
	            /** 取得所需之參數 */
	            Timestamp tep=Timestamp.valueOf(LocalDateTime.now());
				long borrowTimeInMillis = tep.getTime();
				long eightHours = borrowTimeInMillis + (8 * 60 * 60 * 1000);
				Timestamp eightHoursLater = new Timestamp(eightHours);
	            /** 將參數回填至SQL指令當中 */
	            pres = conn.prepareStatement(sql);
	            pres.setTimestamp(1, eightHoursLater);
	            pres.setInt(2,borrow_record_id);
	            /** 執行更新之SQL指令並記錄影響之行數 */
	            row = pres.executeUpdate();
	            //資料庫器材數量+1
				String sqlQuantity = "SELECT `instrument_quantity` FROM `sa`.`tbl_instrument` WHERE `instrument_id` = ? LIMIT 1";

				pres = conn.prepareStatement(sqlQuantity);
				pres.setInt(1,instrument_id );
				ResultSet rsQuantity = pres.executeQuery();

				int currentQuantity = 0;
				if (rsQuantity.next()) {
					currentQuantity = rsQuantity.getInt("instrument_quantity");
				}
					String sqlUpdate = "UPDATE `sa`.`tbl_instrument` SET `instrument_quantity` = ? WHERE `instrument_id` = ?";
					pres = conn.prepareStatement(sqlUpdate);
					pres.setInt(1, currentQuantity + 1);
					pres.setInt(2, instrument_id);
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
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
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
}
