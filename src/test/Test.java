package test;
import java.io.*;
import obdParser.*;

public class Test {
	public static void main(String[] args) {
		String unformat_response = ">01 00 01 02 03 04\n" + 
				"7E9 10 0B 41 00 88 18 00 10 \n" + 
				"7E9 21 01 00 00 00 00 00 00 \n" + 
				"7E8 22 01 04 32 00 00 00 00 \n" + 
				"7E8 10 10 41 00 BE 1B 30 13 \n" + 
				"7E8 21 01 00 07 EF 80 03 02 \n" + 
				"7EA 10 0B 41 00 80 08 00 10 \n" + 
				"7EA 21 01 00 00 00 00 00 00";
		Parser parser = new Parser();
		Response response = new Response(unformat_response);
		
		try {
			String json = parser.ParseMultiReponses(response);
			System.out.println(json);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
