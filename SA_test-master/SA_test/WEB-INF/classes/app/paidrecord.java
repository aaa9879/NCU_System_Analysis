package app;

import java.sql.Timestamp;
import org.json.JSONObject;

public class paidrecord {
	private int paid_sequence;
	private Timestamp paid_time;
	private int paid_fee;
	private int member_id;
	public paidrecord (int member_id) {
		this.member_id=member_id;
	}
	public paidrecord (int member_id, int paid_sequence,int paid_fee) {
		this.member_id=member_id;
		this.paid_sequence=paid_sequence;
		this.paid_fee=paid_fee;
	}
	public JSONObject getData() {
        /** 透過JSONObject將該名會員所需之資料全部進行封裝*/ 
        JSONObject jso = new JSONObject();
        jso.put("id", getMember_id());
        jso.put("paid_sequence", getPaid_sequence());
        jso.put("paid_fee", getPaid_fee());
        jso.put("paid_time", getPaid_time());

        return jso;
    }
	public int getMember_id() {
		return this.member_id;
	}
	public Timestamp getPaid_time() {
		return this.paid_time;
	}
	public int getPaid_fee() {
		return this.paid_fee;
	}
	public int getPaid_sequence() {
		return this.paid_sequence;
	}
}