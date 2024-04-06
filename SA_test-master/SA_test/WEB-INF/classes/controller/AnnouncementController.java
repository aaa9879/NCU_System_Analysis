package controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import app.AnnouncementHelper;
import app.member;
import tools.JsonReader;
import app.Announcement;

import javax.servlet.annotation.WebServlet;

@WebServlet("/api/announcement.do")
public class AnnouncementController extends HttpServlet {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private AnnouncementHelper ah =  AnnouncementHelper.getHelper();
  
	public void doPut(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        JsonReader jsr = new JsonReader(request);
	        JSONObject jso = jsr.getObject();
	        String title = jso.getString("title");
	        String content = jso.getString("content");
	        int id = jso.getInt("id");
	        Announcement a = new Announcement(title,content,id);
	        //呼叫helper的create方法
	        JSONObject data = ah.create(a);

	        JSONObject resp = new JSONObject();
	            
	        resp.put("status", "200");
	        resp.put("message", "成功! 新增公告...");
	        resp.put("response", data);
	        resp.put("id",id);
	        jsr.response(resp, response);
	  }

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		   //呼叫helper的gellallannouncement方法
           JSONObject data = ah.getAllAnnouncement();
           JSONObject resp = new JSONObject();
           resp.put("status", "200");
           resp.put("message", "公告資料取得成功");
           resp.put("response", data);
           response.setContentType("application/json");
	       response.setCharacterEncoding("UTF-8");
	       response.getWriter().write(resp.toString());
       
    }
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        JsonReader jsr = new JsonReader(request);
	        JSONObject jso = jsr.getObject();
	        
	        /** 取出經解析到JSONObject之Request參數 */
	        int id = jso.getInt("id");
	        //呼叫helper的deletebyid方法
	        JSONObject query = ah.deleteByID(id);
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "公告移除成功！");
	        resp.put("response", query);

	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        jsr.response(resp, response);
    }
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
	        JsonReader jsr = new JsonReader(request);
	        JSONObject jso = jsr.getObject();
	        
	        /** 取出經解析到JSONObject之Request參數 */
	        int announcement_id = jso.getInt("announcement_id");
	        String title = jso.getString("title");
	        String content = jso.getString("content");
	        
	        //把這些參數放到announcement參數
	        Announcement a = new Announcement(announcement_id,title,content);
	        
	        //呼叫helper的update方法
	        JSONObject data = ah.update(a);
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "成功! 更新公告...");
	        resp.put("response", data);
	        
	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        jsr.response(resp, response);
	    }
}
