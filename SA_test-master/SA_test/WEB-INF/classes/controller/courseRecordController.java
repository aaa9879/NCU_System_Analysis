package controller;

import tools.JsonReader;

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


import app.courseRecordHelper;
@WebServlet("/api/courserecord.do")
public class courseRecordController extends HttpServlet{
	 private static final long serialVersionUID = 1L;
	 private courseRecordHelper crh=courseRecordHelper.getHelper();
	//為classinfo的put報名社課
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  int member_id = Integer.parseInt(request.getParameter("id"));
		  int course_id = Integer.parseInt(request.getParameter("course_id"));
		   JSONObject resp = new JSONObject();
		   try {
			   JSONObject data =crh.createrecord(member_id,course_id);
	    
	           resp.put("response", data);
	           resp.put("id", course_id);
	           resp.put("status", "0");
	           response.setContentType("application/json");
	           response.setCharacterEncoding("UTF-8");
	           response.getWriter().write(resp.toString());
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	      
	  }
	//為classinfo的delete 取消報名
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
			  int member_id = Integer.parseInt(request.getParameter("id"));
			  int course_id = Integer.parseInt(request.getParameter("course_id"));
			  JSONObject resp = new JSONObject();
			   try {
				   JSONObject data =crh.deleterecord(member_id,course_id);
		    
		           resp.put("response", data);
		  		   resp.put("status", "0");
		  		  
		           response.setContentType("application/json");
		           response.setCharacterEncoding("UTF-8");
		           response.getWriter().write(resp.toString());
		       } catch (Exception e) {
		           e.printStackTrace();
		       }
		      
		  }
	  //為registrationrecord.html的get
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
			  	String Str_member_id = request.getParameter("id");
		  		
			   JSONObject resp = new JSONObject();
			   if(Str_member_id==null) {
				   JSONObject data = crh.getAllRecord();
				   resp.put("response", data);
		  		   resp.put("status", "0");
		  		  
		           response.setContentType("application/json");
		           response.setCharacterEncoding("UTF-8");
		           response.getWriter().write(resp.toString());
			   }else {
				   int member_id = Integer.parseInt(Str_member_id);
				   JSONObject data =crh.getAllRecordById(member_id);
				    
		           resp.put("response", data);
		  		   resp.put("status", "0");
		  		  
		           response.setContentType("application/json");
		           response.setCharacterEncoding("UTF-8");
		           response.getWriter().write(resp.toString());
			   }
				   
		       
		      
		  }
}

