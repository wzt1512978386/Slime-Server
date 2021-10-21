package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import test1.TT;

public class RecordManager {
	//聊天记录
	private String chatMsgAddr="./cache/chat_msg.txt";
	private static File chatMsgFile;
	private BufferedReader br;
	//账号
	private String userAddr="./cache/user.txt";
	private static File userFile;
	private BufferedReader br_user;
	//排行
	private String rankAddr="./cache/ranking.txt";
	private static File rankFile;
	private BufferedReader br_rank;
	public RecordManager() {
		
		try {
			fileInit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new timeSavingThread().start();
		
	}
	private void fileInit() throws IOException{
		File cache=new File("./cache");
		if(!cache.exists())
			cache.mkdirs();
		
		//聊天记录
		chatMsgFile=new File(chatMsgAddr);
		if(!chatMsgFile.exists())
			chatMsgFile.createNewFile();
		
		FileReader fr=new FileReader(chatMsgFile);
		br=new BufferedReader(fr);
		msgListInit();
		fr.close();
		br.close();
		
		//用户记录
		userFile=new File(userAddr);
		if(!userFile.exists())
			userFile.createNewFile();
		FileReader fr_user=new FileReader(userFile);
		br_user=new BufferedReader(fr_user);
		userListInit();
		fr_user.close();
		br_user.close();
		
		//排行榜
		rankFile=new File(rankAddr);
		if(!rankFile.exists())
			rankFile.createNewFile();
		FileReader fr_rank=new FileReader(rankFile);
		br_rank=new BufferedReader(fr_rank);
		rankListInit();
		fr_rank.close();
		br_rank.close();
		
		
	}
	private void rankListInit() throws IOException{
		String str;
		while((str=br_rank.readLine())!=null) {
			DataBase.rankList.add(new Record(str));
		}
	}
	private void userListInit() throws IOException{
		String str;
		while((str=br_user.readLine())!=null) {
			DataBase.userList.add(new User(str));
		}
	}
	private void msgListInit() throws IOException {
		String str;
		while((str=br.readLine())!=null) {
			DataBase.chatList.add(new ChatEnity(str));
		}
	}
	private class timeSavingThread extends Thread {
		@Override
		public void run() {
			synchronized(this) {
				while(true) {
					try {
						this.wait(500);
						save();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
		}
	};
	public static void save() throws IOException{
		//聊天记录
		FileWriter fw=new FileWriter(chatMsgFile,false);
		BufferedWriter bw=new BufferedWriter(fw);
		//bw.write("123412");
		//bw.close();
		
		for(ChatEnity chat:DataBase.chatList) {
			bw.write(chat.toEncodeT());
			bw.newLine();
		}
		bw.close();
		fw.close();
		
		
		//用户记录
		FileWriter fw_user=new FileWriter(userFile,false);
		BufferedWriter bw_user=new BufferedWriter(fw_user);
		for(User u:DataBase.userList) {
			bw_user.write(u.toEncode());
			bw_user.newLine();
		}
		bw_user.close();
		fw_user.close();
		
		//排行榜
		FileWriter fw_rank=new FileWriter(rankFile,false);
		BufferedWriter bw_rank=new BufferedWriter(fw_rank);
		for(Record u:DataBase.rankList) {
			bw_rank.write(u.toEncode());
			bw_rank.newLine();
		}
		bw_rank.close();
		fw_rank.close();
	}
	
}
