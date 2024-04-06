package controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import app.instrument;
import app.instrumentHelper;
import tools.JsonReader;

import javax.servlet.annotation.WebServlet;

@WebServlet("/api/instrument.do")
public class instrumentController extends HttpServlet {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private instrumentHelper ih =  instrumentHelper.getHelper();
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    String id =request.getParameter("id");
	    if (id==null) {

	           JSONObject query = ih.getAllinstrument();
	           
	           /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	           JSONObject resp = new JSONObject();
	           resp.put("status", "200");
	           resp.put("message", "所有資料取得成功");
	           resp.put("response", query);  
	           resp.put("id", id);
		       response.setContentType("application/json");
		       response.setCharacterEncoding("UTF-8");
		       response.getWriter().write(resp.toString());
	       }else {
	    	   
	       }
    }
}
