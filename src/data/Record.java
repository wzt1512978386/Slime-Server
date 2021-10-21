package data;

public class Record {
	public String account;
	public int no,score;
	public Record() {
	}
	public Record(int no,String account,int score) {
		this.account=account;
		this.no=no;
		this.score=score;
	}
	public Record(String str) {
		String []strs=str.split(" ");
		no=Integer.parseInt(strs[0]);
		account=strs[1];
		score=Integer.parseInt(strs[2]);
		
	}
	public String toEncode() {
		return no+" "+account+" "+score+" ";
	}

}
