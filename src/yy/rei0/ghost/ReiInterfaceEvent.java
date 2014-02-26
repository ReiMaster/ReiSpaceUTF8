package yy.rei0.ghost;

import java.awt.event.*;

public class ReiInterfaceEvent implements ActionListener {

	ReiInterface rei;
	Mind reiMind;
	String[] words;
	
	public ReiInterfaceEvent(ReiInterface in){
		rei=in;
		reiMind=new Mind();
	}
	
    public void actionPerformed(ActionEvent event){
    	String command=event.getActionCommand();
        if(command.equals("对零说")){
        	Process();
        }
    }
    
    public void Process(){
    	String strInput=rei.inPut.getText();
    	String strOutput;
    	strOutput=ICTCLAS.testICTCLAS_ParagraphProcess(strInput);
    	words=strOutput.split(" ");
    	
    	reiMind.CognitiveProcess(words);
    	
        //测试用
    	int test=0;
    	System.out.println(test);
    	System.out.println(reiMind.answer);
    	
    	rei.outPut.setText(strOutput);
    	
    }
}