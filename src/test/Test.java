package test;
import java.io.*;
import obdParser.*;

public class Test {
	public static void main(String[] args) {
		Parser parser = new Parser();
		System.out.println("Hello World!");
		
		Response response = new Response(1, 12, "35 93");
		try {
			parser.DecodeResponse(response);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
