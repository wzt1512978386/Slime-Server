package test1;

public class test {
	
    public  float FF2(char t1){

        return ((int)t1*1f/10f-10f);
    }
    public float FF1(char t3,char t2,char t1){
        return ((int)t1+(int)t2*256+(int)t3*256*256)*1f/100f;
    }
	public String FF1_(float f) {
		int tf=(int)(f*100);
		char t1=(char)(tf%256);
		char t2=(char)((tf%(256*256))/256);
		char t3=(char)(tf/(256*256));
		return t3+""+t2+""+t1;
	}
	public String FF2_(float f) {
		int tf=(int)((f+10)*10);
		return (char)(tf)+"";
	}
}
