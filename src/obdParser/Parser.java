package obdParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.lang.Character;
import java.lang.Integer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import obd2.*;
// the class to parser OBDLinkMX data
public class Parser {
	//Response response;
	int[] bytes;
	
	public Parser() {
	}
	
	// parse the response
	public String ParseSingleResponse(String response) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		String json = "";
		response = response.replace(" ", "");
		
		int id = Integer.parseInt(response.substring(0,2), 16) - 0x40;
		int pid = Integer.parseInt(response.substring(2,4), 16);
		String data = response.substring(4);
		
		bytes = new int[data.length()/2];
		for (int i = 0; i < data.length()/2; i++) {
			bytes[i] = Integer.parseInt(data.substring(2*i,2*i+2), 16);
		}
		if (data.length()/2 != Service01.Bytes(pid)) {
			System.out.println("Error: incorrect format");
			return "";
		}
		// handling special case of PIDs
		if (id == 1) {
			if (Service01.Formula(pid).startsWith("#")) {
				if (pid == 0) {
					for (int i = 0; i < 0x1f; i++) {
						String key = Service01.Description(i + 1);
						int value = (bytes[i/8] << i%8) & 0x80;
						objectNode.put(key, value/128);
					}
				}
				if (pid == 1) {
					
				}
			}
			else {
				String exprString = new String(Service01.Formula(pid));
				for (char c = 'A'; c < 'A' + Service01.Bytes(pid); c++) {
					int index = 0;
					exprString = exprString.replaceAll(Character.toString(c), Integer.toString(bytes[index]));
					index++;
				}
				Expression expr = new ExpressionBuilder(exprString).build();
				double value = expr.evaluate();
				objectNode.put(Service01.Description(pid), value);
			}
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
		}
		return json;
	}
	public String ParseMultiReponses(Response response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		for (String line : response.responseList) {
			ObjectNode objectNode = mapper.createObjectNode();
			String[] words = line.split(" ");
			int length = Integer.parseInt(words[1], 16);
			objectNode.put("ECU", words[0]);
			for (int i = 3; i < length + 3 - 1;) {
				int val = Integer.parseInt(words[i], 16);
					int numOfBytes = Service01.Bytes(val);
					String str = words[2];
					for (int k = i; k <= i + numOfBytes; k++) {
						str += words[k];
					}
					if (val == 0) {
						System.out.println("Shouldn't put PID 00 in multiple responses mode.");
						System.out.println("Auto Ignore PID 00");
					}
					else {
						objectNode.setAll((ObjectNode) mapper.readTree(this.ParseSingleResponse(str)));
					}
					i += numOfBytes + 1;
			}
			arrayNode.add(objectNode);
		}
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
	}
	
}