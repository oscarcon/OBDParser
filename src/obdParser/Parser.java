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
	
	// load the format of OBD-II standard
//	public int LoadFormat(final String csvPath) throws IOException {
//		try (
//			Reader reader = Files.newBufferedReader(Paths.get(csvPath));
//			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
//		) {
//			String[] nextRecord;
//			while ((nextRecord = csvReader.readNext()) != null) {
//				Dictionary<String, String> dict = new Hashtable<String, String>();
//				dict.put("pid", nextRecord[0]);
//				dict.put("bytes", nextRecord[1]);
//				dict.put("description", nextRecord[2]);
//				dict.put("supported", nextRecord[3]);
//				dict.put("min", nextRecord[4]);
//				dict.put("max", nextRecord[5]);
//				dict.put("unit", nextRecord[6]);
//				dict.put("formula", nextRecord[7]);
//				format.add(dict);
//			}
//		}
//		return 0;
//	}
	
	// parse the response
	public String DecodeResponse(Response response) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		String json = "";
		String data = response.data.replaceAll(" ", "");
		bytes = new int[data.length()/2];
		for (int i = 0; i < data.length()/2; i++) {
			bytes[i] = Integer.parseInt(data.substring(2*i,2*i+2), 16);
		}
		if (data.length()/2 != Service01.Bytes(response.pid)) {
			System.out.println("Error: incorrect format");
			return "";
		}
		// handling special case of PIDs
		if (Service01.Formula(response.pid).startsWith("#")) {
			if (response.pid == 0) {
				for (int i = 0; i < 0x1f; i++) {
					String key = Service01.Description(i + 1);
					int value = (bytes[i/8] << i%8) & 0x80;
					objectNode.put(key, value/128);
				}
			}
			if (response.pid == 1) {
				
			}
		}
		else {
			String expString = new String(Service01.Formula(response.pid));
			for (char c = 'A'; c < 'A' + Service01.Bytes(response.pid); c++) {
				int index = 0;
				expString = expString.replaceAll(Character.toString(c), Integer.toString(bytes[index]));
				index++;
			}
			Expression exp = new ExpressionBuilder(expString).build();
			double value = exp.evaluate();
			objectNode.put(Service01.Description(response.pid), value);
		}
		json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
		return json;
	}
}