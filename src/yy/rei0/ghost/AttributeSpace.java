package yy.rei0.ghost;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/*
 * 用于处理属性空间。
 * 这里其实也有问题啦，应该是感知觉属性才对。
 * 至于进化而来的其他心理驱动类属性，放在哪里还不知道，就先不管了。
 */

//20131024
/*
 *说明：
 *按理说每个属性空间都应该有一个自动的，基于最基本二元关系（意象图式）的匹
 *配功能，但那目前仅存在于设计之中，是Rei1的基本特性。
 *这里（ReiSpace=Rei0）仍旧是利用属性值进行直接匹配，非常简单，也非常僵硬。
 *顺便说一句，Rei1和Rei0在属性空间方面根源性的区别在于数据结构方面。Rei1比
 *Rei0更简单，更基本，但在这种数据结构之上的操作要比Rei0的更丰富并且灵活。
*/


public class AttributeSpace {
	
	//20130709
	//对象列表（暂时放在这里）
	List<ObjectNode> objectspace=new ArrayList<ObjectNode>();
	
	//基本感知觉属性相关
	//颜色
	List<ColorNode> colorspace = new ArrayList<ColorNode>();
	//（物理）力
	List<ForceNode> forcespace = new ArrayList<ForceNode>();
	
	//用以记录ForceSpace内的状态转化而成的自然语言语句
	public String theStateOfForceSpace="";

	//用以记录ColorSpace内的状态转化而成的自然语言语句
	public String theStateOfColorSpace="";

	
	public AttributeSpace(){
		//总之先放在这里
		Init();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//                                     核心函数                                                                                                    //
	///////////////////////////////////////////////////////////////////////////////////////
	//颜色
	public void ActivateColorSpace(String symbol){
		//20130709
		//总之先在C++版本上修改
		//1.判断Symbol，当等于“颜色”的时候
		// 1.1则建立一个新的ColorNote
		// 1.2找到对应的Object并填入LinkedObject
		//2.当等于“红”等已知原型色彩的时候
		// 2.1找到对应的Object并填入LinkedObject

		if(symbol.equals("颜色"))//1
		{
			//1.1
			ColorNode newnode=new ColorNode("颜色","",0,0,0);
			
			int low;

			//先找到high
			int GraphSize=MainSpace.CognitiveGraph.size();

			low=MainSpace.GetCurrentSecondHighActivatedNodeAL();
			
			for(int i=0;i<GraphSize;i++)
			{
				if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==low)
				{

					if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(1).RelationType.equals("拥有") && 
							MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(1).Symbol.equals("颜色"))
					{
						newnode.LinkedObject=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
						break;
					}
				}
			}

			for(int i=0;i<GraphSize;i++)
			{
				if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(1).Symbol.equals(newnode.LinkedObject))
				{
					MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol="颜色";
					break;
				}
			}

			colorspace.add(newnode);
		}
		else
		{
			String temp="";
			int high;
			
			high=MainSpace.GetCurrentMostHighActivatedNodeAL();
			
			int GraphSize=MainSpace.CognitiveGraph.size();
			
			for(int i=0;i<GraphSize;i++)
			{
				if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==high)
				{
					if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(1).RelationType.equals("属于"))
					{
						temp=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(1).Symbol;
						break;
					}
				}
			}


			int ListSize=colorspace.size();
			for(int i=0;i<ListSize;i++)
			{
				if(colorspace.get(i).Symbol.equals(symbol))
				{
					colorspace.get(i).LinkedObject=temp;
					break;
				}
			}

		}
	}
	
	//（物理）力
	public void ActivateForceSpace(){
		//20130709总之现在C++的版本上进行修改
		//马上发现一个巨大的问题，这里的Symbol是从ObjectList中得到的，但我这次没有建OjectList……
		//另外就是一个比较长远的问题，那就是我肯定是要有一个类似ObjectList的东西的，但远比现在这个什么都没有，只有Symbol属性的复杂。
		//但放在那里好呢？MainSpace里，还是Mind里？这一方面是个理论问题，另一方面是个实现的问题。因为要控制可访问性。
		//如果仅是为一时使用的话，可以把Object放在Attribute里。
		//1.找到当前激活强度为2的节点（ObjectList？），提取出Symbol
		//2.建立两个ForceSpaceNote，一个的Subject为Symbol，另一个的Object为Symbol，其余为NULL
		//3.Power的话先赋个1吧（相等）

		String subject_symbol="";
		int objectspacesize=objectspace.size();
		int high,low;//这里还需要修改。参考MainSpace中的同类算法。
		high=objectspace.get(0).ActivatedLevel;
		for(int i=0;i<objectspacesize;i++)
		{
			if(objectspace.get(i).ActivatedLevel>high){
				high=objectspace.get(0).ActivatedLevel;
			}
		}
		low=objectspace.get(0).ActivatedLevel;
//		boolean tag=false;
		for(int i=0;i<objectspacesize;i++)
		{
			if(objectspace.get(i).ActivatedLevel>low){
				int temp=objectspace.get(0).ActivatedLevel;
				if(temp==high){
//					tag=true;
					subject_symbol=objectspace.get(0).Symbol;
					break;
				}
				else{
					low=temp;
				}
			}
		}

		String object_symbol="";
		ForceNode newnode1=new ForceNode(subject_symbol,object_symbol);
		forcespace.add(newnode1);

		ForceNode newnode2=new ForceNode(object_symbol,subject_symbol);
		forcespace.add(newnode2);
	}
	

	public void AssignmentSymbolInForceSpace(String symbol){

		int ListSize=forcespace.size();
		for(int i=0;i<ListSize;i++)
		{
			if(forcespace.get(i).Object.equals(""))
			{
				forcespace.get(i).Object=symbol;
			}
			else if(forcespace.get(i).Subject.equals(""))
			{
				forcespace.get(i).Subject=symbol;
			}
		}
		
	}
	
	public void ActivateObjectSpace(String word)
	{
		if(objectspace.size()==0)//说明是分析过程中出现的第一个对象要素
		{
			ObjectNode newnote=new ObjectNode(word);
			objectspace.add(newnote);
		}
		else//说明不是分析过程中出现的第一个对象要素
		{
			int ListSize=objectspace.size();
			int count=0;//用于记录对象是否存在
			for(int i=0;i<ListSize;i++)
			{
				if(objectspace.get(i).Symbol.equals(word))
				{
					objectspace.get(i).ActivatedLevel=7;
					count=1;
					break;
				}
			}
			if(count==0)
			{
				ObjectNode newnote=new ObjectNode(word);
				objectspace.add(newnote);
			}
		}
	}

	public void ActivateObjectSpace()
	{
		ObjectNode newnode=new ObjectNode();
		objectspace.add(newnode);
	}

	public void ActivateObjectSpace(int activatedlevel)//这东西感觉用不到啊
	{
		int ListSize=objectspace.size();
		for(int i=0;i<ListSize;i++)
		{
			if(objectspace.get(i).ActivatedLevel==activatedlevel)
			{
				objectspace.get(i).ActivatedLevel=7;
			}
		}
	}
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//                                        其他辅助用函数                                                                                                     //
	///////////////////////////////////////////////////////////////////////////////////////////////
	//考虑到不管有没有激活颜色空间，都应该载入原色，所以下面两个方法可以考虑改成类方法（静态方法）
	//另外，其他的一些有原型的感知觉属性空间也可以作类似处理。
	public void GetAttribute(){
		//20130708总之先放在这里
		GetRelationFromForceSpace();
		GetRelationFromColorSpace();
	}
	
	public void DynamicForceReasoning(){
		//1.首先搜索语义网中的节点是否有“接触”、“受力”、“被强制”关系
		//2.找到相关的SymbolA（施力者），SymbolB（受力者）
		//3.修改ForceSpace内的节点的值

		String SymbolA="";
		String SymbolB="";
		String SymbolTemp="";


		
		//1&2
		int GraphSize=MainSpace.CognitiveGraph.size();
		int reasons=0;//记录是否满足所有三个条件

		for(int i=0;i<GraphSize;i++)//广度（宽度）遍历？
		{
			int ListSize=MainSpace.CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++)
			{
				if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==6)
				{
					SymbolTemp=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol;
				}
				else if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel!=6 && 
						MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType.equals("接触"))
				{
					SymbolA=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol;
					reasons++;
				}
				else if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel!=6 && 
						MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType.equals("受力"))
				{
					SymbolA=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol;
					reasons++;
				}
				else if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel!=6 && 
						MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType.equals("被强制"))
				{
					SymbolA=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol;
					reasons++;
				}
			}
		}
		if(reasons==3)
		{
			SymbolB=SymbolTemp;
		}

		//3
		int forcespacesize=forcespace.size();
		for(int i=0;i<forcespacesize;i++)
		{
			if(forcespace.get(i).Object.equals(SymbolB) && forcespace.get(i).Subject.equals(SymbolA))
			{
				forcespace.get(i).power=0;
			}
			else if(forcespace.get(i).Object.equals(SymbolA) && forcespace.get(i).Subject.equals(SymbolB))
			{
				forcespace.get(i).power=2;
			}
		}
		
	}
	
	//20131024
	//用以将ForceSpace内的状态转化为自然语言语句
	public void GetRelationFromForceSpace(){
		//20130709
		//总之在C++版本上修改
		//算法：
		//将theStateOfForceSpace设置为空字符串
		//遍历list
		//找到Object和Subject都不等于空字符串的节点，根据power的值生成说明语句
		
		theStateOfForceSpace="";	
		int forcespacesize=forcespace.size();
		for(int i=0;i<forcespacesize;i++)
		{
			if(!forcespace.get(i).Object.equals("") && !forcespace.get(i).Subject.equals(""))
			{
				int force=forcespace.get(i).power;
				switch(force)
				{
					case 0:
						{
							theStateOfForceSpace+=forcespace.get(i).Object+"对"+
									forcespace.get(i).Subject+"的力小于"+forcespace.get(i).Subject+"对"+
									forcespace.get(i).Object+"的力\n";
						}
						break;
					case 1:
						{
							theStateOfForceSpace+=forcespace.get(i).Object+"对"+
									forcespace.get(i).Subject+"的力等于"+forcespace.get(i).Subject+
									"对"+forcespace.get(i).Object+"的力\n";
						}
						break;
					case 2:
						{
							theStateOfForceSpace+=forcespace.get(i).Object+"对"+
									forcespace.get(i).Subject+"的力大于"+forcespace.get(i).Subject+"对"+
									forcespace.get(i).Object+"的力\n";
						}
						break;
				}
			}
		}
	}
	
	//20131024
	//用以将ColorSpace内的状态转化为自然语言语句
	public void GetRelationFromColorSpace(){
		//算法：
		//将theStateOfColorSpace设置为空字符串
		//遍历list
		//找到LinkedObject不等于空字符串的节点，取得Red,Green,Blue的值，生成说明语句
		
		theStateOfColorSpace="";
		int ListSize=colorspace.size();
		int red,green,blue;
		for(int i=0;i<ListSize;i++)
		{
			if(!colorspace.get(i).LinkedObject.equals(""))
			{
				red=colorspace.get(i).Red;
				green=colorspace.get(i).Green;
				blue=colorspace.get(i).Blue;
				theStateOfColorSpace+=colorspace.get(i).LinkedObject+"的颜色为：Red("+red+");Green("+green+");Blue("+blue+")。\n";
			}
		}
	}
	

	public void ColorReasoning(int in){
		//20130709
		//总之在C++版本上修改
		//1.找到激活状态为1的节点的上位SymbolA
		//2.找到激活状态为2的节点的上位SymbolB
		//3.根据Symbol找到ColorSpace对应的节点，再根据operation计算
		//  其中0为“浅”；1为“相等”；2为“深”

		//1&2
		String SymbolA="";
		String SymbolB="";

		int low=0;
		int high=0;
		int GraphSize=MainSpace.CognitiveGraph.size();
		
		high=MainSpace.GetCurrentMostHighActivatedNodeAL();
		low=MainSpace.GetCurrentSecondHighActivatedNodeAL();
		
		for(int i=0;i<GraphSize;i++)
		{
			if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==low)
			{
				SymbolA=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
				break;
			}
		}
		for(int i=0;i<GraphSize;i++)
		{
			if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==high)
			{
				SymbolB=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
				break;
			}
		}


		//3
		int red=-1;
		int green=-1;
		int blue=-1;

		int ListSize=colorspace.size();
		for(int i=0;i<ListSize;i++)
		{
			if(colorspace.get(i).LinkedObject.equals(SymbolB))
			{
				red=colorspace.get(i).Red;
				green=colorspace.get(i).Green;
				blue=colorspace.get(i).Blue;
				break;
			}
		}

		if(in==2)//深//20131024 修改
		{
			if(red<205)
			{
				red+=50;
			}
			else
			{
				red=255;
			}

			if(green<205)
			{
				green+=50;
			}
			else
			{
				green=255;
			}

			if(blue<205)
			{
				blue+=50;
			}
			else
			{
				blue=255;
			}
		}
		else if(in==1)//相等
		{
			;//其实就是什么都不做
		}
		else//浅//20131024 修改
		{
			if(red>50)
			{
				red-=50;
			}
			else
			{
				red=0;
			}

			if(green>50)
			{
				green-=50;
			}
			else
			{
				green=0;
			}

			if(blue>50)
			{
				blue-=50;
			}
			else
			{
				blue=0;
			}
		}

		for(int i=0;i<ListSize;i++)
		{
			if(colorspace.get(i).LinkedObject.equals(SymbolA))
			{
				colorspace.get(i).Red=red;
				colorspace.get(i).Green=green;
				colorspace.get(i).Blue=blue;
				break;
			}
		}
	}
	
	public void Process(int id){
		switch(id){
		case 241:ColorReasoning(0);//浅 
			break;
		case 242:ColorReasoning(1);//相等
			break;
		case 243:ColorReasoning(2);//深
			break;
		case 301:ActivateForceSpace();
			break;
		case 341:DynamicForceReasoning();
			break;
		case 401:ActivateObjectSpace();
			break;
		case 261:GetRelationFromColorSpace();
		    break;
		case 351:GetRelationFromForceSpace();
	    	break;
		case 399:GetAttribute();
	    	break;
		}
	}
	
	public void Init(){
		LoadColor();
	}
	
	public void LoadColor(){
		ColorNode node;
		node=new ColorNode("红色","",255,0,0);
		colorspace.add(node);
		node=new ColorNode("绿色","",0,255,0);
		colorspace.add(node);
		node=new ColorNode("蓝色","",0,0,255);
		colorspace.add(node);
		node=new ColorNode("黑色","",0,0,0);
		colorspace.add(node);
		node=new ColorNode("白色","",255,255,255);
		colorspace.add(node);
	}
	
	public void ReduceActivatedLevel(){
		
		//20130709总之在C++的版本上修改
		int objectspacesize=objectspace.size();
		for(int i=0;i<objectspacesize;i++)
		{
			if(objectspace.get(i).ActivatedLevel!=0)
			{
				objectspace.get(i).ActivatedLevel-=1;;
			}
			else
			{
				
			}
		}

	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//                                             辅助类                                                                                                               //
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public class ColorNode {
		
		String Symbol;
		String LinkedObject;
		int Red;
		int Green;
		int Blue;
		
		public ColorNode(String symbol, String object, int red, int green, int blue){
			Symbol=symbol;
			LinkedObject=object;
			Red=red;
			Green=green;
			Blue=blue;
		}

	}
	
	public class ForceNode{
		
		String Subject;//主观施力者
		String Object;//客观受力者
		int power;//力量大小
		
		public ForceNode(String subject, String object){
			Subject=subject;
			Object=object;
			power=1;//“1”是平衡
		}
	}
	
	public class ObjectNode{
		String Symbol;
		int ActivatedLevel;
		
		public ObjectNode(){
			Symbol="";
			ActivatedLevel=7;
		}
		
		public ObjectNode(String symbol){
			Symbol=symbol;
			ActivatedLevel=7;
		}
		
	}

	

}
