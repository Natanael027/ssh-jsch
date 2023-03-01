package com.dart.ssh;

import com.jcraft.jsch.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootTest
public class TestForwarding2 {

	@Test
	 void testForwarding2(){
		TestForwarding2 t=new TestForwarding2();
		try{
			t.go();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void go() throws Exception{
		StringBuilder outputBuffer = new StringBuilder();

		String host="192.168.1.244"; // First level target
		String user="app";
		String password="qwertywagwbc2";

		String tunnelRemoteHost="secondlevelhost"; // The host of the second target
		String secondPassword="targetsystempassword";
		int port=22;

		JSch jsch=new JSch();
		Session session=jsch.getSession(user, host, port);
		session.setPassword(password);
		localUserInfo lui=new localUserInfo();
		session.setUserInfo(lui);
		session.setConfig("StrictHostKeyChecking", "no");
		// create port from 2233 on local system to port 22 on tunnelRemoteHost
		session.setPortForwardingL(2233, tunnelRemoteHost, 22);
		session.connect();
		session.openChannel("direct-tcpip");

		// create a session connected to port 2233 on the local host.
		Session secondSession = jsch.getSession(user, "localhost", 2233);
		secondSession.setPassword(secondPassword);
		secondSession.setUserInfo(lui);
		secondSession.setConfig("StrictHostKeyChecking", "no");

		secondSession.connect(); // now we're connected to the secondary system
		Channel channel=secondSession.openChannel("exec");
		((ChannelExec)channel).setCommand("hostname");

		channel.setInputStream(null);

		InputStream stdout=channel.getInputStream();

		channel.connect();

		while (true) {
			byte[] tmpArray=new byte[1024];
			while(stdout.available() > 0){
				int i=stdout.read(tmpArray, 0, 1024);
				if(i<0)break;
				outputBuffer.append(new String(tmpArray, 0, i));
			}
			if(channel.isClosed()){
				System.out.println("exit-status: "+channel.getExitStatus());
				break;
			}
		}
		stdout.close();

		channel.disconnect();

		secondSession.disconnect();
		session.disconnect();

		System.out.print(outputBuffer.toString());
	}
}
class localUserInfo implements UserInfo{
	String passwd;
	public String getPassword(){ return passwd; }
	public boolean promptYesNo(String str){return true;}
	public String getPassphrase(){ return null; }
	public boolean promptPassphrase(String message){return true; }
	public boolean promptPassword(String message){return true;}
	public void showMessage(String message){}
}