package data;

public class User {
	private static final String SP = " ___ ";
	public String account;
	public String password;
	public int head;
	public User(String account,String password) {
		this.account=account;
		this.password=password;
	}
	public User(String str) {
		String []strs=str.split(SP);
		this.account=strs[0];
		this.password=strs[1];
		this.head=Integer.parseInt(strs[2]);
	}
	public String toEncode() {
		return account+SP+password+SP+head;
	}

}
