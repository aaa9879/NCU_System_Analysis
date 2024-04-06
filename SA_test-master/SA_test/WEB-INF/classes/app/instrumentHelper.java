package app;
import java.sql.*;
import org.json.*;
import util.DBMgr;
public class instrumentHelper {
	private instrumentHelper() {
        
    }
    private static instrumentHelper ih;
    private Connection conn = null;
    private PreparedStatement pres = null;
    private instrument i;
    public static instrumentHelper getHelper() {
        if(ih == null) ih = new instrumentHelper();
        return ih;
    }
    public JSONObject getAllinstrument() {
        instrument i = null;
        JSONArray jsa = new JSONArray();
        String exexcute_sql = "";
        long start_time = System.nanoTime();
        int row = 0;
        ResultSet rs = null;
        try {
            conn = DBMgr.getConnection();
            String sql = "SELECT * FROM `sa`.`tbl_instrument`";
            pres = conn.prepareStatement(sql);
            rs = pres.executeQuery();
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                int id = rs.getInt("instrument_id");
                String name = rs.getString("instrument_name");
                int quantity = rs.getInt("instrument_quantity");
                i = new instrument(id,name,quantity);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(i.getData());
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
