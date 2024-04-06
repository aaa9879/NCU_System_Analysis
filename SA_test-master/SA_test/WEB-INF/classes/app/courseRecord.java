package app;
import java.sql.Timestamp;
import org.json.JSONObject;
import java.sql.*;

public class courseRecord {
	private int courseRecord_id;
	private int member_id;
	private int course_id;
	public courseRecord(int member_id,int course_id) {
		this.member_id=member_id;
		this.course_id=course_id;
	}
	public int getCourseRecord_id() {
		return this.courseRecord_id;
	}
	public int getMember_id() {
		return this.member_id;
	}
	public int getCourse_id() {
		return this.course_id;
	}
	public JSONObject getData() {
		JSONObject jso = new JSONObject();
		jso.put("courseRecord_id",getCourseRecord_id());
		jso.put("member_id",getMember_id());
		jso.put("course_id",getCourse_id());
		return jso;
	}
}

