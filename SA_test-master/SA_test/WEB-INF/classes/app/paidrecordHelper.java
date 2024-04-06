package app;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;
import org.json.*;
import util.DBMgr;
import java.time.LocalDateTime;
public class paidrecordHelper {
	private paidrecordHelper() {      
    }
	private static paidrecordHelper prh;
	private Connection conn = null;
	private PreparedStatement pres = null;
	public static paidrecordHelper getHelper() {
		if(prh == null) prh = new paidrecordHelper();
		return prh;
	}
	public JSONObject create(paidrecord p,int id) {
    	/** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
    	/** 紀錄SQL總行數 */
        int row = 0;
        Random random = new Random();
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `sa`.`tbl_paid_record`(`paid_sequence`, `paid_time`, `paid_fee`, `member_id`)"
                    + " VALUES(?, ?, ?, ?)";
            
            int paid_sequence= generateRandomNumber(10, random);
            Timestamp tep=Timestamp.valueOf(LocalDateTime.now());
			long borrowTimeInMillis = tep.getTime();
			long eightHours = borrowTimeInMillis + (8 * 60 * 60 * 1000);
			Timestamp eightHoursLater = new Timestamp(eightHours);
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, paid_sequence);
            pres.setTimestamp(2, eightHoursLater);
            pres.setInt(3,1000);
            pres.setInt(4,id);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
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
        response.put("time", duration);
        response.put("row", row);

        return response;
    }
	public JSONObject getByID(int id) {
		paidrecord p = null;
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
			String sql = "SELECT * FROM `sa`.`tbl_paid_record` WHERE `member_id` = ? LIMIT 1";
        
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
				/** 將 ResultSet 之資料取出 */
				int ID = rs.getInt("member_id");
				int fee=rs.getInt("paid_fee");
				int sequence=rs.getInt("paid_sequence");
          
				p=new paidrecord(ID,sequence,fee);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(p.getData());
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
	//生成繳費序列亂數
	private static int generateRandomNumber(int digits, Random random) {
	        // 確保digits在有效範圍內
	        if (digits <= 0 || digits > 10) {
	            throw new IllegalArgumentException("Digits should be between 1 and 10");
	        }

	        // 生成十位數的亂數
	        int min = (int) Math.pow(10, digits - 1);
	        int max = (int) Math.pow(10, digits) - 1;

	        return random.nextInt(max - min + 1) + min;
	    }
}
