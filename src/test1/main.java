package test1;

import data.DataBase;
import data.RecordManager;
import game.GameThread;

public class main {

	public static void main(String[] args) {
		RecordManager RM=new RecordManager();
		UdpServer server=new UdpServer();
		
		server.startServe();
		
		//server.sendMsg("192.168.137.177", 58020, "hhhh");
		/*
		test tt=new test();
		String str=tt.FF1_(1234.1234f);
		char []c=str.toCharArray();
		TT.println(tt.FF1(c[0], c[1], c[2]));
		*/
		
	}
	

}
