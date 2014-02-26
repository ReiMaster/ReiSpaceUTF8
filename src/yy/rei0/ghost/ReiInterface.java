package yy.rei0.ghost;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;

public class ReiInterface {

	private JFrame frame;
	
	//事件对象
	ReiInterfaceEvent reiEvent=new ReiInterfaceEvent(this);
	
	//界面上的控件
	JLabel InputInfor=new JLabel("你想聊些什么？",JLabel.CENTER);
	JLabel OutputInfor=new JLabel("这是零的回答：",JLabel.CENTER);
    JButton Think = new JButton("对零说");
    JTextArea inPut=new JTextArea(4,40);
    JTextArea outPut=new JTextArea(4,40);


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReiInterface window = new ReiInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public ReiInterface() {
		initialize();
	}


	private void initialize() {
		frame = new JFrame("智能机器接口 Type0 Version 0.01");
        setLookAndFeel();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    inPut.setLineWrap(true);
		inPut.setWrapStyleWord(true);
		outPut.setLineWrap(true);
		outPut.setWrapStyleWord(true);
		
		GridLayout layout=new GridLayout(5,1);
		frame.setLayout(layout);
		frame.add(InputInfor);
		frame.add(inPut);
		frame.add(Think);
		frame.add(OutputInfor);
		frame.add(outPut);
		
        //Add listeners
		Think.addActionListener(reiEvent);
	}
	
    private void setLookAndFeel(){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch(Exception exc){
            //ignore error//好吧
        }
    }

}
