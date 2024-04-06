package app;
import java.sql.Timestamp;

import org.json.JSONObject;

public class message {
	private int message_id;
	private String message_content;
	private Timestamp message_time;
	private int article_id;
	private int member_id;
	public message (String message_content,Timestamp message_time,int article_id,int member_id) {
		this.message_content=message_content;
		this.message_time=message_time;
		this.article_id=article_id;
		this.member_id=member_id;
	}
	//回傳給前端
		public JSONObject getData() {
			JSONObject jso = new JSONObject();
			/** 透過JSONObject將該名會員所需之資料全部進行封裝*/
			jso.put("message_content",getContent());
			jso.put("message_time",getMessage_time());
			jso.put("article_id", getArticle_id());
			jso.put("member_id",getMember_id());
			return jso;
		}
		public String getContent() {
			return this.message_content;
		}
		public Timestamp getMessage_time() {
			return this.message_time;
		}
		public int getMember_id() {
			return this.member_id;
		}
		public int getArticle_id() {
			return this.article_id;
		}
}
