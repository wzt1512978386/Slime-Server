package test1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import data.Addr;
import data.ChatEnity;
import data.DataBase;
import data.MyDate;
import data.Record;
import data.RecordManager;
import data.User;
import game.GameThread;
import game.Player;
import game.Spirit;

public class UdpServer {
	// ��Ϣ����
	public static final String SP = " ___ ";
	private static final String LF = " --- ";

	public static enum TYPE {
		TRY,
		LOGIN_IN,
		LOGIN_REGISTER,
		LOGIN_UPDATE,
		CHAT_MSG,
		CHAT_GIF,
		CHAT_REVOKE,
		DELETE_MSG,
		GAME_ENTER,
		GAME_INFO,
		GAME_DEATH,
		GAME_RANKING
		
	}

	// �������
	private String SIP;
	private int SPORT = 58010;

	private final int MAX_LENGTH = 1024*4;

	private DatagramSocket UdpSocket;
	GameThread gameThread;
	ChatThread chatThread;
	
	public UdpServer() {
		SIP = TT.getIp();
		TT.println(SIP + "  " + SPORT);
		socketInit();
		gameThread=new GameThread(this);
		chatThread=new ChatThread();
		chatThread.start();
	}

	public void startServe() {
		new Thread(new Runnable() {
			public void run() {
				TT.println("������ ip��ַ:" + UdpSocket.getLocalAddress().getHostAddress() + "   �˿ں�:"
						+ UdpSocket.getLocalPort());
				byte[] buffer = new byte[MAX_LENGTH];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				while (true) {
					try {
						UdpSocket.receive(packet);
						String receStr = new String(packet.getData(), 0, packet.getLength(), "GBK");
						// msgSplit(receStr);
						// TT.println(receStr);
						String CIP = packet.getAddress().getHostAddress();
						int CPORT = packet.getPort();
						 //TT.println(CPORT+" "+CIP);

						// TT.println("ip:"+CIP+" port:" + CPORT+" data:" + receStr);
						// sendMsg(CIP,CPORT,"������bb",TYPE.TRY);
						/*
						 * String CMD,ACCOUNT=""; String []Strs,tmps; Strs=receStr.split(SP);
						 * 
						 * 
						 * //��ȡ�˺� tmps=Strs[0].split(":") //��ȡ�������� tmps=Strs[1].split(":");
						 * if(tmps[0].equals("cmd")) CMD=tmps[1];
						 */
						String[] Strs = receStr.split(SP);
						String CMD = Strs[0];
						String ACCOUNT="";
						if(!CMD.equals("TRY"))
							ACCOUNT= Strs[1];

						// ����port�б�
						if (CMD.equals("CHAT_MSG") || CMD.equals("CHAT_GIF")||CMD.equals("NONE")) {
							Addr addr_ = new Addr(ACCOUNT, CIP, CPORT);
							int tmpIdx = DataBase.addrList.indexOf(addr_);
							if (tmpIdx == -1) {
								DataBase.addrList.add(addr_);
								for (ChatEnity chat : DataBase.chatList) {
									// TT.println(str);
									// String[]strs=str.split(SP);
									if (chat.type == ChatEnity.TYPE.MSG)
										sendMsg(chat.account, CIP, CPORT, chat.toEncode(), TYPE.CHAT_MSG);
									else if (chat.type == ChatEnity.TYPE.GIF)
										sendMsg(chat.account, CIP, CPORT, chat.toEncode(), TYPE.CHAT_GIF);
									else if (chat.type == ChatEnity.TYPE.REVOKE)
										sendMsg(chat.account, CIP, CPORT, chat.toEncode(), TYPE.CHAT_REVOKE);

								}
							} else {
								Addr tmpAddr = DataBase.addrList.get(tmpIdx);
								if (!(tmpAddr.ip.equals(CIP) && tmpAddr.port == CPORT
										&& tmpAddr.account.equals(ACCOUNT))) {
									DataBase.addrList.set(tmpIdx, addr_);
									for (ChatEnity chat : DataBase.chatList) {
										// TT.println(str);
										// String[]strs=str.split(SP);
										if (chat.type == ChatEnity.TYPE.MSG)
											sendMsg(chat.account, CIP, CPORT, chat.toEncode(), TYPE.CHAT_MSG);
										else if (chat.type == ChatEnity.TYPE.GIF)
											sendMsg(chat.account, CIP, CPORT, chat.toEncode(), TYPE.CHAT_GIF);

									}
								}
							}
						}

						
						//�õ��ı���
						String PASSWORD;
						Boolean findFlag;
						switch (CMD) {
						case "TRY":
							TT.println("���գ�" + receStr);
							sendMsg(ACCOUNT, CIP, CPORT, "TRYBACK", TYPE.TRY);
							break;
						case "LOGIN_IN":
							TT.println("���գ�" + receStr);
							findFlag=false;
							PASSWORD=Strs[2];
							for(User u:DataBase.userList) {
								if(u.account.equals(ACCOUNT)) {
									findFlag=true;
									if(u.password.equals(PASSWORD)) 
										sendMsg(ACCOUNT,CIP,CPORT,"CORRECT"+SP+u.head,TYPE.LOGIN_IN);
									else 
										sendMsg(ACCOUNT,CIP,CPORT,"WRONG",TYPE.LOGIN_IN);
									break;
								}
							}
							if(!findFlag) 
								sendMsg(ACCOUNT,CIP,CPORT,"NOT_FOUND",TYPE.LOGIN_IN);
							break;
						case "LOGIN_REGISTER":
							TT.println("���գ�" + receStr);
							findFlag=false;
							PASSWORD=Strs[2];
							for(User u:DataBase.userList) {
								if(u.account.equals(ACCOUNT)) {
									findFlag=true;
									sendMsg(ACCOUNT,CIP,CPORT,"HAD_BEEN_REGISTERED",TYPE.LOGIN_REGISTER);
									break;
								}
							}
							if(!findFlag) {
								DataBase.userList.add(new User(ACCOUNT,PASSWORD));
								RecordManager.save();
								sendMsg(ACCOUNT,CIP,CPORT,"REGISTER_OK",TYPE.LOGIN_REGISTER);
							}
							break;
						case "LOGIN_UPDATE":
							TT.println("���գ�" + receStr);
							String newAccount=Strs[2];
							String newPassword=Strs[3];
							int newHead=Integer.parseInt(Strs[4]);
							boolean accountSameFlag=false;
							for(User u:DataBase.userList) {
								if(u.account.equals(newAccount)&&!newAccount.equals(ACCOUNT)) {
									sendMsg(ACCOUNT,CIP,CPORT,"UPDATE_FAIL",TYPE.LOGIN_UPDATE);
									accountSameFlag=true;
									break;
								}
							}
							if(accountSameFlag)
								break;
							for(User u:DataBase.userList) {
								if(u.account.equals(ACCOUNT)) {
									u.account=newAccount;
									u.password=newPassword;
									u.head=newHead;
									sendMsg(ACCOUNT,CIP,CPORT,"UPDATE_SUCCESS"+SP+ACCOUNT+SP+newAccount+SP+newHead,TYPE.LOGIN_UPDATE);
									for(Addr addr:DataBase.addrList) {
										sendMsg(ACCOUNT,addr.ip,addr.port,"UPDATE_SUCCESS"+SP+ACCOUNT+SP+newAccount+SP+newHead,TYPE.LOGIN_UPDATE);
									}
									
									break;
								}
							}
							for(ChatEnity chat:DataBase.chatList) {
								if(ACCOUNT.equals(chat.account)) {
									chat.account=newAccount;
									chat.head=newHead;
								}
							}
							for(Record r:DataBase.rankList) {
								if(r.account.equals(ACCOUNT)) {
									r.account=newAccount;
								}
							}
							RecordManager.save();
							break;
						case "CHAT_MSG":
							TT.println("���գ�" + receStr);
							String msg = Strs[2];
							ChatEnity chatMsg = new ChatEnity(ACCOUNT, msg, ChatEnity.TYPE.MSG);
							DataBase.chatList.add(chatMsg);// ACCOUNT+SP+chatMsg.toEncode().replace("\n",LF));

							for (Addr addr : DataBase.addrList) {
								sendMsg(ACCOUNT, addr.ip, addr.port, chatMsg.toEncode(), TYPE.CHAT_MSG);
							}

							break;
						case "CHAT_GIF":
							TT.println("���գ�" + receStr);
							// TT.println("����");
							String emoIdx = Strs[2];
							ChatEnity chatEmo = new ChatEnity(ACCOUNT, emoIdx, ChatEnity.TYPE.GIF);
							DataBase.chatList.add(chatEmo);
							for (Addr addr : DataBase.addrList) {
								sendMsg(ACCOUNT, addr.ip, addr.port, chatEmo.toEncode(), TYPE.CHAT_GIF);
							}

							break;
						case "CHAT_NONE":
							for(Addr addr:DataBase.addrList) {
								if(addr.account.equals(ACCOUNT)) {
									addr.flag=true;
									break;
								}
							}
							break;
						case "DELETE_MSG":
							TT.println("���գ�" + receStr);
							// TT.println("�ҵ�");
							for (int i = 0; i < DataBase.chatList.size(); i++) {
								ChatEnity chat = DataBase.chatList.get(i);
								// TT.println(chat.account+" "+chat.msg+" "+chat.date);
								if (chat.account.equals(ACCOUNT) && chat.msg.equals(Strs[2])
										&& chat.date.equals(new MyDate(Strs[3]))) {
									// TT.println("�ҵ�ɾ��");
									//DataBase.chatList.remove(i);////////////
									DataBase.chatList.get(i).type=ChatEnity.TYPE.REVOKE;
									RecordManager.save();
									for (Addr addr : DataBase.addrList) {
										sendMsg(chat.account, addr.ip, addr.port, chat.toEncode(), TYPE.DELETE_MSG);
									}

									break;
								}
							}
							break;
						case "DELETE_ADDR":
							TT.println("���գ�" + receStr);
							for (int i = 0; i < DataBase.addrList.size(); i++) {
								Addr addr = DataBase.addrList.get(i);
								if (ACCOUNT.equals(addr.account)) {
									// TT.println("ɾ��");
									DataBase.addrList.remove(i);
									break;
								}
							}
							//gameThread.stop();
							break;
						case "GAME_ENTER":
							TT.println("���գ�" + receStr);
							//������ҵ�ַ
							Addr addr_ = new Addr(ACCOUNT, CIP, CPORT);
							for(int i=DataBase.gameAddrList.size()-1;i>=0;i--) {
								Addr tmpAddr = DataBase.gameAddrList.get(i);
								if(tmpAddr.equals(addr_)) {
									DataBase.gameAddrList.remove(i);
								}
							}
							DataBase.gameAddrList.add(addr_);
							/*
							int tmpIdx = DataBase.gameAddrList.indexOf(addr_);
							if (tmpIdx == -1)
								
							else {
								
								
								if (!(tmpAddr.ip.equals(CIP) && tmpAddr.port == CPORT
										&& tmpAddr.account.equals(ACCOUNT))) {
									DataBase.gameAddrList.set(tmpIdx, addr_);
								}
							}*/
							Player player=new Player(ACCOUNT,Player.MAN_TYPE.L);
							//boolean findFlag2=false;
							//TT.println(gameThread.S.man.size()+" ");
							for(int i=gameThread.S.man.size()-1;i>=0;i--) {
								TT.println(gameThread.S.man.get(i).account+"   "+ACCOUNT);
								if(gameThread.S.man.get(i).account.equals(ACCOUNT)) {
									gameThread.S.man.remove(i);//.set(i,player);
									//findFlag2=true;
									break;
								}
							}
							gameThread.S.man.add(player);
							/*
							if(!findFlag2) {
								//TT.println("�������"+ACCOUNT);
								//gameThread.S.manNum+=1;
							}
							*/
							if(!gameThread.isRun())
								gameThread.start();
							
							
							break;
						case "GAME_MOVE":
							//TT.println("���գ�" + receStr);
							String []tmp=Strs[2].split(" ");
							float posX=Float.parseFloat(tmp[0]);
							float posY=Float.parseFloat(tmp[1]);
							float X_d=Float.parseFloat(tmp[2]);
							float Y_d=Float.parseFloat(tmp[3]);
							gameThread.S.setUserPos(ACCOUNT,posX, posY,X_d,Y_d);
							break;
							// String receIP=packet.getAddress().getHostAddress();
							// int recePort=packet.getPort();
							// TT.println("ip:"+receIP+" port:" + recePort+" data:" + receStr);
							// sendMsg(receIP, recePort, "�������bb",TYPE.TRY);
							// client.sendBack("192.168.137.145",58020, "I love �����bb");
						case "GAME_RANKING":
							String rankData="";
							for(int i=0;i<DataBase.rankList.size();i++) {
								rankData=rankData+DataBase.rankList.get(i).toEncode();
							}
							sendMsg(ACCOUNT,CIP,CPORT,rankData,TYPE.GAME_RANKING);
							break;
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						TT.println("udp�����������쳣!");
					}
				}
			}
		}).start();

	}

	/* ��ʼ�������socket */
	private void socketInit() {
		try {
			UdpSocket = new DatagramSocket(SPORT, InetAddress.getByName(SIP));
			System.out.println("udp����������ɹ���");
		} catch (Exception e) {
			UdpSocket = null;
			System.out.println("udp���������ʧ�ܣ�");
			e.printStackTrace();
		}
	}

	/* ���ķ��� */
	private void msgAnalyze(String msg) {
		String CMD = "";
		String[] Strs, tmps;
		Strs = msg.split(SP);

		// ��ȡ�ͻ���ip��ַ
		tmps = Strs[0].split(":");
		if (tmps[0].equals("cmd"))
			CMD = tmps[1];
	}

	/* ���Ĳ�� */
	private void msgSplit(String msg) {
		// TT.println(msg);
		String CIP = "";
		String CMD = "";
		int CPORT = 0;

		String[] Strs, tmps;
		Strs = msg.split(SP);

		// ��ȡ�ͻ���ip��ַ
		tmps = Strs[0].split(":");
		if (tmps[0].equals("ip"))
			CIP = tmps[1];
		// ��ȡ�ͻ��˶˿�
		tmps = Strs[1].split(":");
		if (tmps[0].equals("port"))
			CPORT = Integer.parseInt(tmps[1]);
		// ��ȡ����ָ������
		tmps = Strs[2].split(":");
		if (tmps[0].equals("cmd"))
			CMD = tmps[1];
		TT.println(CIP + "  " + CPORT + "  " + CMD);
		CIP = "192.168.137.177";
		switch (CMD) {
		case "INFO":
			tmps = Strs[3].split(":");
			String INFO = "";
			if (tmps[0].equals("info"))
				INFO = tmps[1];
			break;
		case "TRY":
			// TT.println("����TRYBACK");
			// sendMsg(CIP,CPORT,"TRYBACK");
			break;
		}

	}
	private byte[] concat(byte[] a, byte[] b) {
		   byte[] c= new byte[a.length+b.length];
		   System.arraycopy(a, 0, c, 0, a.length);
		   System.arraycopy(b, 0, c, a.length, b.length);
		   return c;
		}
	public void sendMsg(String account, String ip, int port, String msg, TYPE type) {
		new Thread(new Runnable() {
			public void run() {
				try {
					String encodeMsg = encodeHead(msg, type, account);
					byte[] buffer = encodeMsg.getBytes("GBK");
					
					// TT.println("���ͣ�"+encodeMsg);
					/*
					if(type==TYPE.GAME_INFO) {
						
						buffer=concat(buffer,DataBase.Gbuff);
					}*/
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, IP2Name(ip), port);
					UdpSocket.send(packet);
					// TT.println("���ͳɹ�");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();

	}

	/* תip��ʽ */
	private InetAddress IP2Name(String ip) throws UnknownHostException {
		return InetAddress.getByName(ip);
	}

	/* ���ı��� */
	private String encodeHead(String msg, TYPE type, String account) {
		switch (type) {
		case TRY:			//�������ӷ�����
			return "TRY" +  SP + msg;
		case LOGIN_IN:		//�û���¼
			return "LOGIN_IN"+SP+msg;
		case LOGIN_REGISTER://�û�ע��
			return "LOGIN_REGISTER"+SP+msg;
		case LOGIN_UPDATE:
			return "LOGIN_UPDATE"+SP+msg;
		case CHAT_MSG:		//�ı�������Ϣ
			return "CHAT_MSG" + SP + account + SP + msg;
		case CHAT_GIF:		//����������Ϣ
			return "CHAT_GIF" + SP + account + SP + msg;
		case CHAT_REVOKE:	//���쳷��
			return "CHAT_REVOKE" + SP + account + SP + msg;
		case DELETE_MSG:	//�����¼ɾ��
			return "DELETE_MSG" + SP + account + SP + msg;
		case GAME_ENTER:	//��Ϸ����
			return "GAME_ENTER"+SP+account+SP+msg;
		case GAME_INFO:		//��Ϸ��ǰ��Ϣ
			return "GAME_INFO"+SP+account+SP+msg;
		case GAME_DEATH:
			return "GAME_DEATH"+SP+account+SP+msg;
		case GAME_RANKING:
			return "GAME_RANKING"+SP+account+SP+msg;
		default:
			return "";
		}

	}

}
