package test;
import java.io.*;
import obdParser.*;

public class Test {
	public static void main(String[] args) {
		Parser parser = new Parser();
		
		Response response = new Response(1, 12, "35 93");
		try {
			String json = parser.DecodeResponse(response);
			System.out.println(json);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
