package com.byto.vo;

import java.lang.reflect.Field;

public class Server implements ValueObject {

	private String seq, host, port, state, regDate;

	public Server(String seq, String host, String port, String state,
			String regDate) {
		this.seq = seq;
		this.host = host;
		this.port = port;
		this.state = state;
		this.regDate = regDate;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	@Override
	public String toString() {
		return "Server [\n" +
				"\t" + "seq=" + seq + ",\n" +
				"\t" + "host=" + host + ",\n" +
				"\t" + "port=" + port + ",\n" +
				"\t" + "state=" + state + ",\n" +
				"\t" + "regDate=" + regDate + "\n" + 
				"]\n";
	}

	
	@Override
	public boolean equals(Object obj) {
		Server serv = (Server) obj;
		if(obj instanceof Server) {
			Server server = (Server) obj;

			if
			(
				seq.equals(server.getSeq()) &&
				host.equals(server.getHost()) &&
				port.equals(server.getPort()) &&
				state.equals(server.getState()) &&
				regDate.equals(server.getRegDate())
			) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		Field[] fields = Server.class.getFields();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<fields.length; i++) {
			sb.append(fields[i].getName());
		}
		
		return sb.toString().hashCode();
	}
}