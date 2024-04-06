package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import app.paidrecord;
import app.paidrecordHelper;

@WebServlet("/api/paidrecord.do")
public class paidrecordController extends HttpServlet{
	  private static final long serialVersionUID = 1L;
	  private paidrecordHelper prh =  paidrecordHelper.getHelper();
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  	   String id = request.getParameter("id");
		   	   JSONObject resp = new JSONObject();
	           int memberID=Integer.valueOf(id);
		   	   paidrecord p=new paidrecord(memberID);
		   	   JSONObject data =prh.create(p,memberID);
	    
	           resp.put("response", id);
	           resp.put("status", "0");
	           response.setContentType("application/json");
	           response.setCharacterEncoding("UTF-8");
	           response.getWriter().write(resp.toString());
	      
	  }
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException {
			   String id = request.getParameter("id");
		       int memberID=Integer.valueOf(id);
		       JSONObject query = prh.getByID(memberID);
		           
		       /** 新建一個JSONObject用於將回傳之資料進行封裝 */
		       JSONObject resp = new JSONObject();
		       resp.put("status", "200");
		       resp.put("response", query);
		       response.setContentType("application/json");
			   response.setCharacterEncoding("UTF-8");
			   response.getWriter().write(resp.toString());
		      
		  }
}
