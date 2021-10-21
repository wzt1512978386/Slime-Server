package game;

import test1.TT;

public class Food {
	public float X,Y;
	public int type;
	public boolean change;
	public static final float W_F = 1080f;
	public static final float H_F = 1920f;
	public Food() {
		rand();
	}
	public void rand() {
		change=true;
		X=(float)(Math.random()*W_F*4/5+W_F/10);
		Y=(float)(Math.random()*H_F*4/5+H_F/10);
		//X=W_F/3;
		//Y=H_F/3;
		type=(int)(Math.random()*100)%9;
		//TT.println(X+"  "+Y);
	}
	
}
