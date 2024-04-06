package controller;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import app.borrowrecordHelper;
import app.instrumentHelper;
import app.member;
import app.borrowrecord;
import tools.JsonReader;
import javax.servlet.annotation.WebServlet;
@WebServlet("/api/borrowrecord.do")
public class borrowrecordController extends HttpServlet{
  private static final long serialVersionUID = 1L;
  private borrowrecordHelper br =  borrowrecordHelper.getHelper();
  private instrumentHelper ih=instrumentHelper.getHelper();
  public void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	   String borrowtime = request.getParameter("borrow-time");
	   String instrument_id = request.getParameter("instrument_id");
	   String id = request.getParameter("id");
	   JSONObject resp = new JSONObject();
	   try {
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//為2023-12-13T18:27 時間格式
           java.util.Date parsedDate = dateFormat.parse(borrowtime);
           Timestamp borrow_time = new Timestamp(parsedDate.getTime());
      
           int instrumentID=Integer.valueOf(instrument_id);
           int memberID=Integer.valueOf(id);
           borrowrecord b=new borrowrecord(borrow_time,instrumentID,memberID);
           JSONObject data =br.createrecord(b,instrumentID,memberID);
    
           resp.put("response", data);
           resp.put("id", id);
           resp.put("status", "0");
           response.setContentType("application/json");
           response.setCharacterEncoding("UTF-8");
           response.getWriter().write(resp.toString());
       } catch (ParseException e) {
           e.printStackTrace();
       }    
  }
  public void doGet(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException {
			   String id = request.getParameter("id");
			   if(id!=null) {
				   int memberID=Integer.valueOf(id);	
				   JSONObject query = br.getByID(memberID);
		           
				   /** 新建一個JSONObject用於將回傳之資料進行封裝 */
				   JSONObject resp = new JSONObject();
				   resp.put("status", "200");
				   resp.put("response", query);
				   response.setContentType("application/json");
				   response.setCharacterEncoding("UTF-8");
				   response.getWriter().write(resp.toString());
			   }
			   else {
				   JSONObject temp=br.getAllBorrowRecord();			   
			   	   JSONObject resp = new JSONObject();
			   	   resp.put("status", "200");
			   	   resp.put("response", temp);
			   	   response.setContentType("application/json");
			   	   response.setCharacterEncoding("UTF-8");
			   	   response.getWriter().write(resp.toString());
			   }
		  }
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException {
	  		   JsonReader jsr = new JsonReader(request);
	  		   JSONObject jso = jsr.getObject();
	  		   
		       int borrowrecordID=jso.getInt("id");
		       int instrumentID=jso.getInt("instrument_id");
		       JSONObject query = br.deleteByID(borrowrecordID,instrumentID);
		           
		       /** 新建一個JSONObject用於將回傳之資料進行封裝 */
		       JSONObject resp = new JSONObject();
		       resp.put("status", "200");
		       resp.put("response", query);
		       response.setContentType("application/json");
			   response.setCharacterEncoding("UTF-8");
			   response.getWriter().write(resp.toString());
		      
		  }
  public void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        
	        /** 取出經解析到JSONObject之Request參數 */
	        String ID = request.getParameter("id");
	        int borrow_record_id=Integer.valueOf(ID);
	        String instrument_id = request.getParameter("instrument_id");
	        int instrumentID=Integer.valueOf(instrument_id);
	        JSONObject data = br.update(borrow_record_id,instrumentID);
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "成功!");
	        resp.put("response", data);
	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(resp.toString());
	    }
}
