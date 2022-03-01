package sockets.lucas;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Cliente {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoCliente mimarco = new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}		
														
}
class MarcoCliente extends JFrame{
	public MarcoCliente() {
		setBounds(600,300,280,350);
		LaminaMarcoCliente milamina = new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
		addWindowListener(new EnvioOnline());//EJECUTA EL METODO WINDOWOPENED
	}
}
//--------ENVIO DE SEÑAL ONLINE---------------------------------------------------------------------------
	class EnvioOnline extends WindowAdapter{//SE CREA ACA POR Q ES UN EVENTO DE VENTANA.
		public void windowOpened(WindowEvent e) {//AL ABRIR LA VENTANA.
			try {
				Socket misocket = new Socket("192.168.1.107", 4545);//PUERTO DEL SERVIDOR QUE ESTA A LA ESCUCHA
				PaqueteEnvio datos = new  PaqueteEnvio();
				
				datos.setMensaje(" online");
				ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());//ENVIAR EL OBJETO DATOS AL SERVIDOR.
			}catch(Exception e2) {
				
			}
		}
		public void windowClosed(WindowEvent x) {
			try {
				Socket misocket = new Socket("192.168.1.107", 4545);
				PaqueteEnvio datos = new  PaqueteEnvio();
				datos.setMensaje(" offline");
				ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
				System.out.println("Se fye xd");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//------------------------------------------------------------------------------------------------------
class LaminaMarcoCliente extends JPanel implements Runnable{
	private JLabel texto;
	private JTextField textfield; 
	private JComboBox ip;
	private JLabel nick;
	private JButton boton;
	private JTextArea campochat;
	public LaminaMarcoCliente() {
		String nick_usuario = JOptionPane.showInputDialog("Nick : ");
		JLabel n_nick=  new JLabel("Nick: ");
		add(n_nick);
		nick = new JLabel();
		nick.setText(nick_usuario);
		add(nick);
		texto = new JLabel("Online: ");
		add(texto);
		ip = new JComboBox();
	/*	ip.addItem("Usuario 1");
		ip.addItem("Usuario 2");
		ip.addItem("Usuario 3");*/
		add(ip);
		textfield = new JTextField(20);
		add(textfield);
		campochat = new JTextArea(12,20);
		add(campochat);
		boton = new JButton("Enviar");
		EnviaTexto mievento = new EnviaTexto();
		boton.addActionListener(mievento);
		add(boton);
		Thread mihilo = new Thread(this);
		mihilo.start();
	}
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				Socket misocket = new Socket("192.168.1.107", 4545);
				/*DataOutputStream flujoSalida = new DataOutputStream(misocket.getOutputStream());
				flujoSalida.writeUTF(textfield.getText());//EN EL FLUJO DE SALIDA VA A VIAJAR LO Q HAY EN EL CAMPO 1. CIRCULA POR EL SOCKET Q SE DIRIJE AL SERVIDOR
				flujoSalida.close();*/
				
				PaqueteEnvio datos = new PaqueteEnvio();//DEBE CONVERTIRSE EN UNA SUCESION DE BYTES PARA PODER SER ENVIADO POR LA RED, PARA ESO
				//IMPLEMENTAR LA INTERFAZ SERIALIZABLE EN LA CLASE DEL OBJETO.
				datos.setNick(nick.getText());
				datos.setIp(ip.getSelectedItem().toString());
				datos.setMensaje(textfield.getText());
				
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());//EL FLUJO CIRCULA POR ESTE SOCKET.
				paqueteDatos.writeObject(datos);//ESCRIBE EN EL FLUJO LOS DATOS.
				campochat.append("\n" + datos.getMensaje());
				misocket.close();
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	@Override
	public void run() {//IMPLEMENTA LA INTERFAZX RUNNABLE PARA Q EN UN SEGUNDO HILO ESTA CLASE ESTE SEIMPRE A LA ESPERA.
		// TODO Auto-generated method stub
		try {
			ServerSocket servidorCliente = new ServerSocket(9090);
			Socket cliente;
			PaqueteEnvio paqueteRecibido;
			
			while(true) {//CONSTANTEMENTE ACEPTANDO CONEXIONES DEL SERVIDOR
				cliente = servidorCliente.accept();
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();
				
				if(!paqueteRecibido.getMensaje().equals(" online")) {
					campochat.append("\n" + paqueteRecibido.getNick() + " :" + paqueteRecibido.getMensaje());
				} else if(paqueteRecibido.getMensaje().equals(" online") || paqueteRecibido.getMensaje().equals(" offline")){
				//campochat.append("\n" + paqueteRecibido.getIps());
					ArrayList <String> IpsMenu = new ArrayList<String>();
					IpsMenu = paqueteRecibido.getIps();
					ip.removeAllItems();
					for(String z : IpsMenu) {
						ip.addItem(z);
					}
				}
				
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}
class PaqueteEnvio implements Serializable{
	private String nick, ip, mensaje;
	private ArrayList<String> Ips;

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}

