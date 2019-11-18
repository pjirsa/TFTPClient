import java.net.*;

public class MyTest extends Object
{
	public static void main(String[] args)
	{
		try {
			DatagramSocket mysock = new DatagramSocket(5432);
			
			byte[] buf = new byte[1024];
			
			DatagramPacket p = new DatagramPacket(buf,buf.length);
			while(true) {
				mysock.receive(p);
				
				System.out.println("Received datagram!");
				
				mysock.send(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


