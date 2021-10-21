package data;

import java.util.Date;

public class ChatEnity {
	public enum TYPE{
		MSG,
		GIF,
		NONE,
		REVOKE
	}
	//消息编码
	private static final String SP=" ___ ";
	private static final String LF=" --- ";
	//数据
	public TYPE type;
	public String account,msg;
	public MyDate date;
	public int head;
	
	public ChatEnity(String account,String msg,TYPE type) {
		this.type=type;
		this.account=account;
		this.msg=msg;
		this.date=new MyDate();
	}
	public ChatEnity(String str) {
		String []strs=str.split(SP);
		this.account=strs[0];
		this.type=getType(strs[1]);
		this.msg=strs[2];
		this.date=new MyDate(strs[3]);
		this.head=Integer.parseInt(strs[4]);
	}
	public TYPE getType(String str) {
		switch(str) {
		case "MSG":
			return TYPE.MSG;
		case "GIF":
			return TYPE.GIF;
		case "REVOKE":
			return TYPE.REVOKE;
		}
		return TYPE.NONE;
	}
	public String toType() {
		switch(type) {
		case MSG:
			return "MSG";
		case GIF:
			return "GIF";
		case REVOKE:
			return "REVOKE";
		default:return "NONE";
		}
	}
	private int getHead() {
		int head=0;
		for(User u:DataBase.userList) {
			if(u.account.equals(account)) {
				head=u.head;
				break;
			}
		}
		return head;
	}
	public String toEncode() {
		
		return msg+SP+date.toString()+SP+getHead()+SP; 
	}
	public String toEncodeT() {
		return account+SP+toType()+SP+msg+SP+date.toString()+SP+getHead()+SP;
	}
	
	public ChatEnity(String []Strs) {
		account=Strs[0];
		
	}
	
		
	
}
