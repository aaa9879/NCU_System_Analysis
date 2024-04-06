package app;
import java.sql.*;
import java.sql.Timestamp;
import org.json.*;
public class course {
	private int course_id;
	private String course_name;
	private Timestamp course_start_time ;
	private int member_id;
	private String course_time;
	private String course_location;
	public course(int course_id,String course_name,Timestamp course_start_time,int member_id,String course_time,String course_location) {
		this.course_id=course_id;
		this.course_name=course_name;
		this.course_start_time=course_start_time;
		this.member_id=member_id;
		this.course_time=course_time;
		this.course_location=course_location;
	}
	public course(String course_name,String course_time,String course_location,int member_id) {
		this.member_id=member_id;
		this.course_name=course_name;
		this.course_time=course_time;
		this.course_location=course_location;
	}
	public course(String course_name,String course_time,String course_location,int member_id,int course_id) {
		this.member_id=member_id;
		this.course_name=course_name;
		this.course_time=course_time;
		this.course_location=course_location;
		this.course_id=course_id;
	}
	public int getCourse_id(){
		return this.course_id;
	}
	public String getCourse_name(){
		return this.course_name;
	}
	public Timestamp getCourse_start_time(){
		return this.course_start_time;
	}
	public int getMember_id(){
		return this.member_id;
	}
	public String getCourse_time(){
		return this.course_time;
	}
	public String getCourse_location(){
		return this.course_location;
	}
	public JSONObject getData() {
        JSONObject jso = new JSONObject();
        jso.put("course_id", getCourse_id());
        jso.put("course_name", getCourse_name());
        jso.put("course_start_time", getCourse_start_time());
        jso.put("member_id", getMember_id());
        jso.put("course_time", getCourse_time());
        jso.put("course_location", getCourse_location());
        return jso;
    }
}
