package test1;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TT {
	/*�������*/
	public static void println(Object obj) {
		System.out.println(obj);
	}
	/*��������ip��ַ*/
	public static String getIp() {
		String ip="ip����ʧ��!";
		try {
			ip=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
