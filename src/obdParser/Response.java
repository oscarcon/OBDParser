package obdParser;
import java.io.*;
import java.util.*;
import java.lang.*;
// the response of the OBDLinkMX store in this class
public class Response {
	// parameters of OBDLinkMX response
	String unformated;
	String[] responseArray;
	ArrayList<String> responseList;
	int[] ecu;
	int[] id;
	int[] pid;
	String[] data;
	
	private int compare(String rep1, String rep2) {
		String[] word1 = rep1.split(" ", 2);
		String[] word2 = rep2.split(" ", 2);
		int headerCompare = word1[0].compareTo(word2[0]);
		if (headerCompare == 0) {
			int orderCompare = word1[1].compareTo(word2[1]);
			if (orderCompare < 0) {
				return -1;
			}
			else if (orderCompare == 0) {
				return 0;
			}
			else return 1;
		}
		else {
			if (headerCompare < 0) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}
	
	private void formatResponse() {
		unformated = unformated.replace(" \n", "\n");
		int endline_pos = unformated.indexOf('\n');
		unformated = unformated.substring(endline_pos + 1, unformated.length());
		responseArray = unformated.split("\n");
		for (int i = 0; i < responseArray.length - 1; i++) {
			int min_pos = i;
			for (int k = i + 1; k < responseArray.length; k++) {
				if (this.compare(responseArray[k], responseArray[min_pos]) < 0) {
					min_pos = k;
				}
			}
			String temp = responseArray[i];
			responseArray[i] = responseArray[min_pos];
			responseArray[min_pos] = temp;

		}
		responseList = new ArrayList<String>();
		int sameValueIndex = 0;
		String[] sameValueArray = responseArray[0].split(" ", 3);
		String data = sameValueArray[0] + " " + sameValueArray[2];
		for (int i = 1; i < responseArray.length; i++) {
			String[] array = responseArray[i].split(" ", 3);
			
			if (array[0].compareTo(sameValueArray[0]) == 0) {
				data += " ";
				data += array[2];
			}
			else {
				responseList.add(data);
				data = "";
				sameValueIndex = i;
				sameValueArray = responseArray[i].split(" ", 3);
				data = sameValueArray[0] + " " + sameValueArray[2];
			}
			if (i == responseArray.length - 1) {
				responseList.add(data);
			}
		}
		for (int i = 0; i < 3; i++) {
			System.out.println(responseList.get(i));
		}
	}
	
	public Response(String _unformated) {
		unformated = _unformated;
		formatResponse();
	}
}
