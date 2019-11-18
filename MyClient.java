import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TftpException extends Exception {
  public TftpException() { super(); }
  public TftpException(String s) { super(s); }
}

class UseException extends Exception {
  public UseException() { super(); }
  public UseException(String s) { super(s); }
}

public class MyClient extends JFrame
{
	JPanel btnPan = new JPanel(),
			filePan = new JPanel(),
			statusPan = new JPanel(),
			mainPan = new JPanel();
	JButton quitBtn = new JButton(),
			sendBtn = new JButton(),
			recBtn = new JButton();
	JLabel lbl = new JLabel("Filename:"),
		lbl2 = new JLabel("Host:"),
		statLbl = new JLabel("bytes transferred");
	JTextField fileName = new JTextField(20),
		hostName = new JTextField(20),
		status = new JTextField(10);
	
	public MyClient(String s){
		super(s);
		filePan.setLayout(new GridLayout(2,2));
		filePan.add(lbl);
		filePan.add(fileName);
		filePan.add(lbl2);
		filePan.add(hostName);
		
		quitBtn.setText("Quit");
		sendBtn.setText("Send");
		recBtn.setText("Get");
		
		btnPan.setLayout(new FlowLayout());
		btnPan.add(sendBtn);
		btnPan.add(recBtn);
		btnPan.add(quitBtn);
		
		status.setText("0");
		status.setHorizontalAlignment(JTextField.RIGHT);
		statusPan.setLayout(new FlowLayout());
		statusPan.add(status);
		statusPan.add(statLbl);
		
		mainPan.setLayout(new BorderLayout());
		mainPan.add(filePan, BorderLayout.CENTER);
		mainPan.add(btnPan, BorderLayout.NORTH);
		mainPan.add(statusPan, BorderLayout.SOUTH);
		
		quitBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}});
		recBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Integer p = getFile();
				status.setText(p.toString());
				System.out.print(p);
				System.out.print(" bytes received\n");
			}});
		sendBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Integer p = sendFile();
				status.setText(p.toString());
				System.out.print(p);
				System.out.print(" bytes sent\n");
			}});
		
		this.setContentPane(mainPan);
	}
	
	public static void main(String[] args)
	{	
		MyClient win = new MyClient("PKB TFTP Client");
		
		win.pack();
		win.show();
	}
	
	Integer getFile(){
		int bytesOut = 512,
				byteCount = 0;		try {
			// Get host and file names			
			String host=hostName.getText();
			String daFile=fileName.getText();

			// Create socket and open output file

			InetAddress server = InetAddress.getByName(host);
			DatagramSocket sock = new DatagramSocket();

			FileOutputStream outFile = new FileOutputStream(daFile);

			// Send request to server

			TFTPread reqPak = new TFTPread(daFile);
			reqPak.send(server,sock);

			// Process the transfer
	
			while(bytesOut == 512){				TFTPpacket inPak = TFTPpacket.receive(sock);
				
				if (inPak instanceof TFTPerror)  {
					TFTPerror p=(TFTPerror)inPak;
					throw new TftpException(p.message());
			    }
			    else if (inPak instanceof TFTPdata) {
					TFTPdata p=(TFTPdata)inPak;

					int blockNum=p.blockNumber();
					bytesOut=p.write(outFile);

					TFTPack ack=new TFTPack(blockNum);
					ack.send(p.getAddress(),p.getPort(),sock);
				}				else					throw new TftpException("Unexpected response from server");
				byteCount = byteCount + bytesOut;
			}

			outFile.close();
			sock.close();			
		}

		catch (UnknownHostException e) { System.out.println("Unknown host "); }
		catch (IOException e) { System.out.println("IO error, transfer aborted"); }
		catch (TftpException e) { System.out.println(e.getMessage()); }				return new Integer(byteCount);
	}
	
	Integer sendFile(){
		FileInputStream source;
		int byteCount = 0;
		
		try {
			// Get host and file names			
			String host=hostName.getText();
			String daFile=fileName.getText();

			// Create socket and open input file

			InetAddress server = InetAddress.getByName(host);
			DatagramSocket sock = new DatagramSocket();
			File srcFile = new File(daFile);
			if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {
				source = new FileInputStream(srcFile);			} else 				throw new TftpException("Access Violation");
        
			// Send request to server

			TFTPwrite reqPak = new TFTPwrite(daFile);
			reqPak.send(server,sock);
			TFTPpacket ackPak = TFTPpacket.receive(sock);
			int servPort = ackPak.getPort();
			
			// Process the transfer
			int bytesRead = TFTPpacket.maxTftpPakLen;
			for (int blkNum=1; bytesRead==TFTPpacket.maxTftpPakLen; blkNum++) {
				TFTPdata outPak=new TFTPdata(blkNum,source);
				bytesRead=outPak.getLength();
				outPak.send(server,servPort, sock);
				byteCount = byteCount + bytesRead;
				TFTPpacket ack=TFTPpacket.receive(sock);
				if (!(ack instanceof TFTPack)) break;
			}
			
			sock.close();
		}
		catch (UnknownHostException e) { System.out.println("Unknown host "); }
		catch (IOException e) { System.out.println("IO error, transfer aborted"); }
		catch (TftpException e) { System.out.println("Error" + e.getMessage()); }
		return new Integer(byteCount);
	}
}




