package app;

import java.sql.Timestamp;
import org.json.JSONObject;

public class borrowrecord {
	private int borrow_record_id;
	private Timestamp borrow_time;
	private int instrument_id;
	private int member_id;
	public borrowrecord (Timestamp borrow_time,int instrument_id,int member_id) {
		this.borrow_time=borrow_time;
		this.instrument_id=instrument_id;
		this.member_id=member_id;
	}
	public borrowrecord (int borrow_record_id,Timestamp borrow_time,int instrument_id,int member_id) {
		this.borrow_record_id=borrow_record_id;
		this.borrow_time=borrow_time;
		this.instrument_id=instrument_id;
		this.member_id=member_id;
	}
	public JSONObject getData() {
        /** 透過JSONObject將該名會員所需之資料全部進行封裝*/ 
        JSONObject jso = new JSONObject();
        jso.put("borrow_record_id", getBorrow_record_id());
        jso.put("borrow_time", getBorrow_time());
        jso.put("instrument_id",getInstrument_id());
        jso.put("member_id",getMember_id());
        return jso;
    }
	public int getBorrow_record_id(){	
		return this.borrow_record_id;
	}
	public Timestamp getBorrow_time(){
		return this.borrow_time;
	}
	public int getInstrument_id(){
		return this.instrument_id;
	}
	public int getMember_id(){
		return this.member_id;
	}
}