package test1;

import data.Addr;
import data.DataBase;

public class ChatThread {
	private boolean runFlag;
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				synchronized(this) {
					runFlag=true;
					while(runFlag) {
						try {
							wait(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(int i=DataBase.addrList.size()-1;i>=0;i--) {
							Addr addr=DataBase.addrList.get(i);
							//TT.println(addr.account+"!!");
							if(!addr.flag) {
								TT.println(addr.account+"±ª«Â¿ÌµÙ");
								DataBase.addrList.remove(i);
							}
							else
								addr.flag=false;
						}
					}
				}
			}
		}).start();
	}
	public void stop() {
		runFlag=false;
	}

}
