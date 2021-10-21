package test1;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TT {
	/*简易输出*/
	public static void println(Object obj) {
		System.out.println(obj);
	}
	/*返回主机ip地址*/
	public static String getIp() {
		String ip="ip返回失败!";
		try {
			ip=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
