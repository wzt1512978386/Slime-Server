package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.Addr;
import data.DataBase;
import data.Record;
import data.RecordManager;
import test1.TT;
import test1.UdpServer;
import test1.UdpServer.TYPE;

public class GameThread {
	// 外部调用
	UdpServer server;

	
	public Spirit S;
	private boolean runFlag;
	
	public GameThread(UdpServer server) {
		this.server = server;
		S=new Spirit();
		
	}
	private void init() {
		

        //SysU.IN101(" "+ man[0].getX());
        
	}
	public void stop() {
		runFlag=false;
	}
	
	private int timeTmp=0;
	public void start() {
		runFlag=true;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				S.calGwPos(true);
				synchronized (this) {
					while (runFlag) {
						try {
							//wait(50);
								wait(25);
								timeTmp+=1;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(timeTmp%20==0&&timeTmp>1000) {
							//TT.println("检查");
							for(int i=S.man.size()-1;i>=0;i--) {
								if(!S.man.get(i).setFlag) {
									//TT.println(S.man.get(i).account+"不在");
									S.man.remove(i);
									if(S.man.size()==0) {
										stop();
									}
									//TT.println(S.man.size());
								}
							}
							for(Player p:S.man)
								p.setFlag=false;
						}
						S.calGwPos(false);
						for(int k=S.man.size()-1;k>=0;k--) {
							Player p=S.man.get(k);
							for(int i=0;i<S.gwNum;i++) {
								if(S.distance(p.pos[0],p.pos[1],S.gwPos[i][0],S.gwPos[i][1])<=S.gwSpeed(S.gwType[i])*9) {
									S.calXYd(S.gwPos[i], S.gwSpeed(S.gwType[i]), false);
									if(S.gwType[i]==Spirit.GW_TYPE.B)
										p.HP-=20;
									else if(S.gwType[i]==Spirit.GW_TYPE.G)
										p.HP-=15;
									else if(S.gwType[i]==Spirit.GW_TYPE.Z)
										p.HP-=15;
									
	
									//TT.println(p.score);
								}
							}
							for(Food F:S.foods) {
								F.change=false;
								if(S.distance(p.pos[0],p.pos[1],F.X,F.Y)<=30) {
									F.rand();
									p.score+=5;
									//TT.println(p.account+"碰到");
								}
							}
							//TT.println(p.pos[0]+"  "+p.pos[1]+"    "+S.foods.get(0).X+" "+S.foods.get(0).Y);
							if(p.score<0)
								p.score=0;
							if(p.HP<=0) {
								p.HP=0;
								for(int i=DataBase.gameAddrList.size()-1;i>=0;i--) {
									Addr addr=DataBase.gameAddrList.get(i);
									if(addr.account.equals(p.account)) {
										server.sendMsg(p.account, addr.ip, addr.port, ""+p.score, TYPE.GAME_DEATH);
										DataBase.gameAddrList.remove(i);
										break;
									}
								}
								//S.man.remove(p);
								Record record=new Record(-1,p.account,p.score);
								S.man.remove(k);
								boolean lastFlag=true;
								for(int i=0;i<DataBase.rankList.size();i++) {
									Record r=DataBase.rankList.get(i);
									if(record.score>r.score) {
										DataBase.rankList.add(i, record);
										lastFlag=false;
										break;
									}
								}
								if(lastFlag) 
									DataBase.rankList.add(record);
								
								for(int i=0;i<DataBase.rankList.size();i++) {
									Record r=DataBase.rankList.get(i);
									r.no=i+1;
								}
								for(int i=DataBase.rankList.size()-1;i>=0;i--) {
									if(i>=100)
										DataBase.rankList.remove(i);
								}
								try {
									RecordManager.save();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}
						int ranInt=(int)(Math.random()*100000);
						for (Addr addr : DataBase.gameAddrList) {
							server.sendMsg(addr.account, addr.ip, addr.port, ranInt+server.SP+S.toEncode(),
									UdpServer.TYPE.GAME_INFO);
						}
						for (Addr addr : DataBase.gameAddrList) {
							server.sendMsg(addr.account, addr.ip, addr.port, ranInt+server.SP+S.toEncode(),
									UdpServer.TYPE.GAME_INFO);
						}
						

					}
				}
			}
		}).start();
	}
	public boolean isRun() {
		return runFlag;
	}

	

}
