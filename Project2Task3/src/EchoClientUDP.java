import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a UDP client.
 * the client may request either an “add” or “subtract” or “view”
 * operation be performed by the server. In addition, each request will pass along an integer ID. Thus, the
 * client will form a packet with the following values: ID, operation (add or subtract or view), and value (if
 * the operation is other than view).
 * The client is menu driven and
 * will repeatedly ask the user for the user ID, operation, and value (if not a view request). When the
 * operation is “view”, the value held on the server is returned. When the operation is “add” or “subtract”
 * the server performs the operation and simply returns “OK”. During execution, the client will display
 * each returned value from the server to the user. This returned value will be either “OK” or a value (if a
 * view request was made).
 */

public class EchoClientUDP{
    DatagramSocket aSocket;
    InetAddress aHost;
    int serverPort = 6789; //server port

    //constructor
    EchoClientUDP(){
        System.out.println("Client Running"); //indicate start of the client
        try{
            aHost = InetAddress.getByName("localhost"); //server address
            aSocket = new DatagramSocket(); //initialize the socket to receive/send packets
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }
    }
    private void close(){
        if (aSocket != null){
            aSocket.close();
        };
    }
    public static void main(String args[]){
        EchoClientUDP client = new EchoClientUDP();
        Scanner s = new Scanner(System.in);
        char running = 'y';
        while(running == 'y'){
            System.out.print("Please provide an ID: ");
            int cal_id = s.nextInt();
            System.out.println("1. add");
            System.out.println("2. subtract");
            System.out.println("3. view");
            System.out.print("Please choose an operation to perform: ");
            int ops = s.nextInt();
            String result = null;
            int val;
            switch (ops){
                case(1):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(cal_id + ",1,"+val);
                    break;
                case(2):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(cal_id + ",2,"+val);
                    break;
                case(3):
                    result = client.process(cal_id + ",3");
                    break;
            }
            System.out.println(result);
            s.nextLine();//clear inputs
            System.out.print("Enter y to continue: ");
            running = s.nextLine().charAt(0);
        }
        client.close();
    }
    private String process(String requestString){
        byte [] m = requestString.getBytes(); //get the msg in byte
        //create a DatagramPacket for sending a message to the server with address and port of the server
        DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
        try {
            this.aSocket.send(request);//send the request
            byte[] buffer = new byte[1000];//the byte array to wrap messages
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);//instantiate a DatagramPacket to receive incoming messages
            aSocket.receive(reply); //call the receive method on the socket. It stores the message inside the byte array of the DatagramPacket passed to it.
            //create a new byte array with length = msg length
            byte[] data = new byte[reply.getLength()];
            //copy the msg from request to data
            System.arraycopy(reply.getData(), reply.getOffset(), data, 0, reply.getLength());
            //get the reply string
            String replyVal = new String(data);
            return replyVal;
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }
        return null;
    }
}