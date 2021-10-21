package game;

import java.util.ArrayList;
import java.util.List;

import data.Addr;
import data.DataBase;
import test1.TT;

public class Spirit {
	// 消息编码
	private static final String GSP = "__=__";

	public enum GW_TYPE {
		//GREEN, BLUE,ZZ, NONE
		G, B,Z, N
		}
	//public enum MAN_TYPE{MAN1,MAN2}

	// 虚拟舞台
	public static final float W_F = 1080f;
	public static final float H_F = 1920f;
	//public static final float		
	// slm1
	public int gwNum = 20;
	public int gwBlueNum=8;
	public float[][] gwPos;
	public GW_TYPE[] gwType;

	// 人
	//public int manNum = 0;
	private float manSpeed = 10;
	public List<Player> man;
	//private float[][] manPos;
	//食物
	public int foodNum=6;
	public List<Food> foods;
	
	public Spirit() {
		// gw
		gwPos = new float[gwNum][6];
		gwType = new GW_TYPE[gwNum];
		for (int i = 0; i < gwNum; i++) {
			if (i < gwBlueNum)
				gwType[i] = GW_TYPE.B;
			else if(i<gwNum)
				gwType[i] = GW_TYPE.G;
			else
				gwType[i] = GW_TYPE.Z;
		}
		// man
		man=new ArrayList<>();
		//manPos = new float[manNum][4];
		//food
		foods=new ArrayList<>();
		for(int i=0;i<foodNum;i++) {
			foods.add(new Food());
		}
	}

	public void calGwPos(boolean init) {
		if (init) {
			for (int i = 0; i < gwNum; i++) {
				calXYd(gwPos[i], gwSpeed(gwType[i]), init);
			}
		} else {
			for (int i = 0; i < gwNum; i++) {
				gwPos[i][0] += gwPos[i][4];
				gwPos[i][1] += gwPos[i][5];
				if (distance(gwPos[i]) < gwSpeed(gwType[i])) {
					calXYd(gwPos[i], gwSpeed(gwType[i]), false);
				}
			}
		}
	}

	protected float gwSpeed(GW_TYPE type) {
		switch (type) {
		case B:
			return 3;
		case G:
			return 4;
		case Z:
			return 7;
		default:
			return 20;
		}
	}

	protected void calXYd(float[] pos, float speed, boolean init) {
		if (init) {
			pos[0] = (float) (Math.random() * W_F);
			pos[1] = (float) (Math.random() * H_F);
		} else {
			pos[0] = pos[2];
			pos[1] = pos[3];
		}

		do {
			pos[2] = (float) (Math.random() * W_F);
			pos[3] = (float) (Math.random() * H_F);
		} while (distance(pos) < speed * 50);

		float XY_dest = distance(pos);
		pos[4] = (float) (speed * (pos[2] - pos[0]) / XY_dest);
		pos[5] = (float) (speed * (pos[3] - pos[1]) / XY_dest);
	}

	public void setUserPos(String account,float posX, float posY,float X_d,float Y_d) {
		for(Player p:man) {
			if(p.account.equals(account)) {
				p.setFlag=true;
				p.pos[0]=posX;
				p.pos[1]=posY;
				posLimit(p.pos);
				p.pos[2]=X_d;
				p.pos[3]=Y_d;
			}
		}
		
		// float W_tmp=man[idx].getWidth();
		// float H_tmp=man[idx].getHeight();

		// posLimit(manPos[idx],W_tmp,H_tmp);

	}

	private String manEncode() {
		String MSG = man.size() + GSP;
		for(Player p:man) {
			MSG=MSG+p.account+" ";
		}
		MSG=MSG+GSP;
		for(Player p:man) {
			MSG=MSG+p.type;
		}
		MSG=MSG + GSP;
		for(Player p:man) {
			for(int j=0;j<4;j++) {
				MSG=MSG+p.pos[j]+" ";
			}
		}
		/*
		for(int i=0;i<man.size();i++) {
			//TT.println(p.pos[0]+"    "+p.pos[1]);
			Player p=man.get(i);
			FF1(p.pos[0],DataBase.Gbuff,8*i);
			FF1(p.pos[1],DataBase.Gbuff,8*i+3);
			FF2(p.pos[2],DataBase.Gbuff,8*i+6);
			FF2(p.pos[3],DataBase.Gbuff,8*i+7);
			//TT.println(FF1(p.pos[0])+" "+FF1(p.pos[1])+" "+FF2(p.pos[2])+" "+FF2(p.pos[3]));
			//for(int j=0;j<4;j++) 
				//MSG=MSG+(int)(p.pos[j])+" ";
			
		}*/
		/*
		for (int i = 0; i < manNum; i++) {
			for (int j = 0; j < 4; j++) {
				MSG = MSG + manPos[i][j] + " ";
			}
		}*/
		return MSG + GSP;
	}
	private String foodEncode() {
		String MSG=foodNum+GSP;
		for(Food F:foods)
			//MSG=MSG+ff(F.X)+" "+ff(F.Y)+" ";
			MSG=MSG+(int)(F.X)+" "+(int)(F.Y)+" ";
		//TT.println(foods.get(0).X+"  "+foods.get(0).Y);
		
		MSG = MSG + GSP;
		for(Food F:foods)
			MSG=MSG+F.type;
		/*
		MSG = MSG + GSP;
		for(Food F:foods)
			MSG=MSG+F.change+" ";
			*/
		MSG = MSG + GSP;
		for(Player p:man) 
			MSG=MSG+p.score+" "+p.HP+" ";
		
		return MSG+GSP;
	}
	private String gwEncode() {
		String MSG = gwNum + GSP;
		for (int i = 0; i < gwNum; i++)
			MSG = MSG + gwType[i];
		MSG = MSG + GSP;
		for (int i = 0; i < gwNum; i++) {
			MSG = MSG + ff(gwPos[i][0]) + " ";
			MSG = MSG + ff(gwPos[i][1]) + " ";
			MSG = MSG + ff(gwPos[i][4]) + " ";
			MSG = MSG + ff(gwPos[i][5]) + " ";
		
		}
		/*
		for (int i = 0; i < gwNum; i++) {
			FF1(gwPos[i][0],DataBase.Gbuff,8*(i+man.size()));
			FF1(gwPos[i][1],DataBase.Gbuff,8*(i+man.size())+3);
			FF2(gwPos[i][4],DataBase.Gbuff,8*(i+man.size())+6);
			FF2(gwPos[i][5],DataBase.Gbuff,8*(i+man.size())+7);
			//MSG=MSG+FF1(gwPos[i][0])+FF1(gwPos[i][1])+FF2(gwPos[i][4])+FF2(gwPos[i][5]);
			//for (int j = 0; j < 6; j++) 
				//MSG = MSG + ff(gwPos[i][j]) + " ";
				//MSG = MSG + (int)(gwPos[i][j]) + " ";
		}*/
		return MSG + GSP;
	}

	public String toEncode() {
		/*
		if(DataBase.Gbuff==null) {
			DataBase.Gbuff=new byte[8*(man.size()+gwNum)];
		}
		if(DataBase.Gbuff.length!=8*(man.size()+gwNum)) {
			DataBase.Gbuff=new byte[8*(man.size()+gwNum)];
		}*/
		return manEncode() + gwEncode()+foodEncode();
	}

	private void posLimit(float []pos){
        if(pos[0]<0)
            pos[0]=0;
        else if(pos[0]>W_F)
            pos[0]=W_F;
        if(pos[1]<0)
            pos[1]=0;
        else if(pos[1]>H_F)
            pos[1]=H_F;
        /*
        if(pos[0]<w/2)
            pos[0]=w/2;
        else if(pos[0]>W_F-w/2)
            pos[0]=W_F-w/2;
        if(pos[1]<h)
            pos[1]=h;
        else if(pos[1]>H_F)
            pos[1]=H_F;
         */
    }
	/* 映射 */
	private float f2S(float a) {
		if (a > 3)
			return 1;
		else if (a > 0)
			return -0.5f * (float) (Math.cos(a / 4 * Math.PI) - 1);
		else if (a > -3)
			return 0.5f * (float) (Math.cos(a / 4 * Math.PI) - 1);
		else
			return -1;
	}

	public float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	private float distance(float[] pos) {
		return (float) Math.sqrt(Math.pow(pos[0] - pos[2], 2) + Math.pow(pos[1] - pos[3], 2));
	}
	/*
	private void FF1(float f,byte[]b,int idx) {
		int tf=(int)(f*100);
		b[idx+0]=(byte)((tf>>16)&0xff);
		b[idx+1]=(byte)((tf>>8)&0xff);
		b[idx+2]=(byte)(tf&0xff);
	}
	private void FF2(float f,byte[]b,int idx) {
		int tf=(int)((f+10)*10);
		b[idx]=(byte)(tf&0xff);
		
	}*/
	private String ff(float f) {
		return String.format("%.2f", f);
	}
	
}
