package data;

public class Addr{
	/*��ַ�࣬��ʾ��ǰ�û��������ַ��Ϣ*/
	public String account;	//�û���
	public int port;		//�˿ں�
	public String ip;		//IP��ַ
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