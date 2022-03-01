package sockets.lucas;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoServidor mimarco = new MarcoServidor();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
class MarcoServidor extends JFrame implements Runnable{
	private JTextArea textarea;
	
	public MarcoServidor() {
		setBounds(600,300,280,350);
		JPanel milamina = new JPanel();
		milamina.setLayout(new BorderLayout());
		textarea = new JTextArea();
		milamina.add(textarea, BorderLayout.CENTER);
		add(milamina);
		setVisible(true);
		Thread mihilo = new Thread(this);
		mihilo.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket servidor = new ServerSocket(4545);//ESTE PUERTO  A LA ESCUCHA Y ABRA ESE PUENTE
			String nick, ip, mensaje;
			ArrayList <String> listaIp = new ArrayList<String>();
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				Socket misocket = servidor.accept();//ACEPTE LAS CONEXIONES QUE VENGAN DEL EXTERIOR
				/*DataInputStream flujoEntrada = new DataInputStream(misocket.getInputStream());//VA A VER UN FLUJO DE DATOS Q UTILICE EL PUERTO DE SOCKET
				String mensajeTexto = flujoEntrada.readUTF();
				textarea.append("\n" + mensajeTexto);*/
				

				ObjectInputStream paqueteDatos = new ObjectInputStream(misocket.getInputStream());// CAPTURA EL FLUJO DE DATOS DE ENTRADA
				paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();// NO SE PUEDE ALMACENAR UNA INSTACIA OUTPUT EN INPUT.
				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				
				if(!mensaje.equals(" online")) {//YA SE CONECTO POR PRIMERA VEZ
				textarea.append("\n" + nick + ": " + mensaje + " para " + ip);
				
				Socket enviaDestinatario = new Socket(ip, 9090);
				ObjectOutputStream paqueteReenvio= new ObjectOutputStream(enviaDestinatario.getOutputStream());
				paqueteReenvio.writeObject(paqueteReenvio);
				
				paqueteReenvio.close();
				enviaDestinatario.close();
				misocket.close();}else {//ES LA PRIMERA VEZ QUE SE CONECTA
					
//--------------------------------------------------DETECTA---ONLINE--------------------------------------------------------------------------------------------
					InetAddress localizacion = misocket.getInetAddress();//ALMACENAR LA DIRECCION DEL CLIENTE CON EL QUE CONECTAMOS
					String ipRemota = localizacion.getHostAddress();
					//System.out.println(ipRemota);
					if(mensaje.equals(" online")) {
					listaIp.add(ipRemota);//AGREGA LAS DIRECCIONES EN EL ARRAY
					}
					if(mensaje.equals(" offline")) {
						listaIp.remove(ipRemota);
					}
					paqueteRecibido.setIps(listaIp);//DENTRO DEL PAQUETE LA LISTA DE IPS
					for(String z: listaIp) {
						//System.out.println("Array : " + z);
						
						Socket enviaDestinatario = new Socket(z, 9090);
						ObjectOutputStream paqueteReenvio= new ObjectOutputStream(enviaDestinatario.getOutputStream());
						paqueteReenvio.writeObject(paqueteReenvio);
						paqueteReenvio.close();
						enviaDestinatario.close();
						misocket.close();
					}
//---------------------------------------------------------------------------------------------------------------	
				
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

