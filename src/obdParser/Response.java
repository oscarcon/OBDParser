package obdParser;
import java.io.*;
import java.lang.*;
// the response of the OBDLinkMX store in this class
public class Response {
	// parameters of OBDLinkMX response
	int id;
	int pid;
	String data;
	public Response(int _id, int _pid, String _data) {
		id = _id;
		pid = _pid;
		data = _data;
	}
	public int getId() {
		return id;
	}
	public int getpid() {
		return pid;
	}
	public String getData() {
		return data;
	}
}
