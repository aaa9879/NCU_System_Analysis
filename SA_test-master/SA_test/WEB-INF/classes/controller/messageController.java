package controller;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import app.article;
import app.articleHelper;
import app.message;
import app.messageHelper;
import tools.JsonReader;
@WebServlet("/api/message.do")
public class messageController extends HttpServlet{
	private messageHelper msh = messageHelper.getHelper();
	public void doPost(HttpServletRequest request, HttpServletResponse response)

	    throws ServletException, IOException {

	    JsonReader jsr = new JsonReader(request);
	    JSONObject jso = jsr.getObject();
	    int article_id = jso.getInt("article_id");
	    String message_content = jso.getString("message_content");
	    String message_time= jso.getString("message_time");
	    
	    Timestamp messageTime = null;

	    try {
	        LocalDateTime dateTime = LocalDateTime.parse(message_time, DateTimeFormatter.ISO_DATE_TIME);
	        messageTime = Timestamp.valueOf(dateTime);
	    } catch (DateTimeParseException e) {
	        e.printStackTrace();
	        messageTime = new Timestamp(System.currentTimeMillis());
	    }
	    
	    int member_id = jso.getInt("member_id");
	    message mes = new message(message_content, messageTime, article_id,member_id);
	    JSONObject data = msh.create(mes); 

	    JSONObject resp = new JSONObject();

	    resp.put("status", "200");
	    resp.put("response", data);
	    jsr.response(resp, response);
	}
	
	// Handle GET requests for retrieving messages
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
		String Str_article_id = request.getParameter("article_id");	
		int article_id=Integer.parseInt(Str_article_id);
		JSONObject query = msh.getmessageByid(article_id);
			
	      JSONObject resp = new JSONObject();
	      resp.put("status", "200");
	      resp.put("message", "所有資料取得成功");
	      resp.put("response", query);        
	      response.setContentType("application/json");
	      response.setCharacterEncoding("UTF-8");
	      response.getWriter().write(resp.toString());
	}

}
