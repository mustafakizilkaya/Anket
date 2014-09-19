package com.LH.androidtcp.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TCPClient {
    public String serverMessage;
    public static final String SERVERIP = "192.168.2.94"; //ip adresi
    public static final int SERVERPORT = 13759;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    // server mesajlarını dinler
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    //clienttan servera mesaj yollar
    public void sendMessage(String message) {
        String messageWithStartandEnd = "<" + message + ">";
        if (out != null && !out.checkError()) {
            out.println(messageWithStartandEnd);
            out.flush();
        }
    }

    Socket socket;

    public void stopClient() {
        mRun = false;
    }

    public void run() {
        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            socket = new Socket(serverAddr, SERVERPORT);
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                byte FIRST_BYTE = (byte) 60;
                byte LAST_BYTE = (byte) 62;

                InputStream stream = socket.getInputStream();
                byte[] data = new byte[100];

                while (mRun) {
                    int firstByte = stream.read();

                    if ((byte) firstByte != FIRST_BYTE) {
                        //Başlangıç byte'ı değil, bu karakteri atla!
                        continue;
                    }

                    //Mesajı oku
                    ArrayList<Byte> bList = new ArrayList<Byte>();

                    while ((byte) (firstByte = stream.read()) != LAST_BYTE) {
                        bList.add((byte) firstByte);
                    }

                    byte[] result = new byte[bList.size()];

                    for (int i = 0; i < bList.size(); i++) {
                        result[i] = bList.get(i).byteValue();
                    }

                    serverMessage = new String(result, "UTF-8");

                    if (serverMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(serverMessage);
                    }
                }
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }
            //socket.close();
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}


