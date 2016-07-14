package HadesCELLOAPI;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int d = 30;
		int M = 12;
		int y = 2012;
		
		String dd = "", MM = "", yy = "";
		
		if(d<10) dd = "" + "0" + d;
		else dd = "" + d;
		
		if(M<10) MM = "" + "0" + M;
		else MM = "" + M; 
		
		yy = "" + y;
		
		//return "" + dd + MM + yy.substring(2);
		System.out.println("" + dd + "" + MM + "" + yy.substring(2));
	}

}
