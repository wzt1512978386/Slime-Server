package data;

public class Addr{
	/*地址类，表示当前用户的网络地址信息*/
	public String account;	//用户名
	public int port;		//端口号
	public String ip;		//IP地址
	public boolean flag=true;
	public Addr(String account,String ip,int port) {
		this.account=account;
		this.ip=ip;
		this.port=port;
		flag=true;
	}
	
	@Override
	public boolean equals(Object o) {
		Addr addr=(Addr)o;
		//if(this.account.equals(addr.account)&&this.ip.equals(addr.ip)&&this.port==addr.port)
		if(this.account.equals(addr.account))
			return true;
		else
			return false;
	}
}