package app;
import java.sql.*;


import org.json.*;
public class member {
	private int id;
	private String account;
	private String name;
	private String password;
	private String phone;
	private String group;
	private Timestamp create_time;
	private String identity;
	
	private memberHelper mh =  memberHelper.getHelper();
	
	public member( String account, String name, String password, String phone, String group) {
	        this.account=account;
			this.name=name;
	        this.password=password;
	        this.phone=phone;
	        this.group=group; 
	}
	public member( String account ,String password) {
        this.account=account;

        this.password=password;

	}
	public member(int id ,String name,String password,String phone,String group,Timestamp create_time,String identity,String account) {
        this.account=account;
		this.name=name;
        this.password=password;
        this.phone=phone;
        this.group=group; 
        this.create_time=create_time;
        this.id=id;
        this.identity=identity;
	}
	public member(int id,String name,String password,String phone,String group) {
		this.name=name;
        this.password=password;
        this.phone=phone;
        this.group=group; 
        this.id=id;
	}
	public member(String account,String name,String password,String phone,String group,String identity) {
		this.name=name;
        this.password=password;
        this.phone=phone;
        this.group=group; 
        this.identity=identity;
        this.account=account;
	}
	public JSONObject getData() {
        /** 透過JSONObject將該名會員所需之資料全部進行封裝*/ 
        JSONObject jso = new JSONObject();
        jso.put("id", getID());
        jso.put("name", getName());
        jso.put("account", getAccount());
        jso.put("password", getPassword());
        jso.put("create_time", getCreate_time());
        jso.put("phone", getPhone());
        jso.put("identity", getIdentity());
        jso.put("group", getGroup());
        
        return jso;
    }
	 public JSONObject update() {
	        /** 新建一個JSONObject用以儲存更新後之資料 */
	        JSONObject data = new JSONObject();
	        /** 檢查該名會員是否已經在資料庫 */
	        if(this.id != 0) {
	            /** 透過MemberHelper物件，更新目前之會員資料置資料庫中 */
	            data = mh.update(this);
	        }
	        
	        return data;
	    }
	public int getID() {
        return this.id;
    }
	public String getAccount() {
        return this.account;
    }
	public String getName() {
        return this.name;
    }
	public String getPassword() {
        return this.password;
    }
	public String getPhone() {
        return this.phone;
    }
	public String getGroup() {
        return this.group;
    }
	public Timestamp getCreate_time() {	
        return this.create_time;
    }
	public String getIdentity() {
        return this.identity;
    }
	
}	
