
package ppdchat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Matheus
 */
public class ServerFile extends UnicastRemoteObject implements ServerFileInterface, Serializable{

    public ServerFile() throws RemoteException {
    }
    @Override
    public void uploadFileToServer(byte[] mydata, String serverpath, int length) throws RemoteException {

        try {
            File serverpathfile = new File(serverpath);
            FileOutputStream out = new FileOutputStream(serverpathfile);
            byte[] data = mydata;

            out.write(data);
            out.flush();
            out.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        System.out.println("Done writing data...");

    }
}
