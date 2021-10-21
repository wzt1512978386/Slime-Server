package game;

public class Player {
	public enum MAN_TYPE{L,Q};
	public String account;
	public float []pos=new float[4];
	public MAN_TYPE type;
	public int score,HP;
	public boolean setFlag=true;
	public Player(String account,MAN_TYPE type) {
		this.score=0;
		this.HP=100;
		this.account=account;
		this.type=type;
		this.pos[0]=Spirit.W_F/200;
		this.pos[1]=Spirit.H_F/200;
	}
}
