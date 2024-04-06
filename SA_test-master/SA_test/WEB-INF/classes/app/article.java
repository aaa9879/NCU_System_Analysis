package app;
import java.sql.Timestamp;
import org.json.JSONObject;
import java.sql.*;

public class article {
	private int article_id;
	private String title;
	private String article_content;
	private Timestamp article_time;
	private int member_id;
	public article (int member_id,String title,String article_content,Timestamp article_time,int article_id) {
		this.article_content=article_content;
		this.title=title;
		this.article_time=article_time;
		this.member_id=member_id;
		this.article_id=article_id;
	}
	public article (String title,String article_content,int member_id) {
		this.article_content=article_content;
		this.title=title;
		this.member_id=member_id;
	}
	//回傳給前端
	public JSONObject getData() {
		JSONObject jso = new JSONObject();
		/** 透過JSONObject將該名會員所需之資料全部進行封裝*/
		jso.put("title",getTitle());
		jso.put("article_content",getArticle_content());
		jso.put("article_time", getArticle_time());
		jso.put("member_id",getMember_id());
		jso.put("article_id",getArticle_id());
		return jso;
	}
	public String getTitle() {
		return this.title;
	}
	public Timestamp getArticle_time() {
		return this.article_time;
	}
	public int getMember_id() {
		return this.member_id;
	}
	public String getArticle_content() {
		return this.article_content;
	}
	public int getArticle_id() {
		return this.article_id;
	}
}

