package yy.rei0.ghost;
import java.util.*;

/*
 * 用于整合并控制所有的分析模块。具体工作如下：
 * 1）接受任务（待分析字符串）
 * 2）汇总结果（结果字符串）
 * 3）根据接收到的字符串分配任务给各个模块（在理论上有些问题）
 * 4）（未确定）同步字词的激活状态
 */

/*
 *20130715
 *以后工作日志会在专门的Word文档里写，就不在这里写了 
 *
 *20131025
 *结果还是没写。好吧。看来在项目内部放一个工作日志文件会比较实际。
 */

/*
 * 20130705
 * 关于激活强度：
 * 仍旧根据工作记忆来确定激活强度的等级。由于工作记忆量大约是7+-2，这次就定在“7”。
 * 原先只有4级（3，2，1，0），现在变为8级（7，6，5，4，3，2，1，0）。
 * 另外就是，“激活强度”这个词本身可能也会根据应用的模块进行修改，比如在考虑言语输出的时候，改为：窗口大小。
 * 
 * *关于语义类型：
 * 暂时还按照三分法分类。
 * 用整形数表示。
 * 对象：0
 * 对象的属性1：1（感知觉属性值）
 * 对象的属性2：2（感知觉属性名）
 * 对象间的关系：3
 * 未确定类型：4（20130706）
 * 
 * 
 * 20130715
 * 不过测试数据还是要放在这里的。
 * 
 * 1）兔子的左边是松鼠。松鼠的上边是麻雀。兔子和麻雀的关系是什么？
 * 
 * 2）小王推箱子。箱子动。为什么？
 * 
 * 3）玫瑰是红色。月季的颜色比玫瑰浅。月季的颜色是什么？
 * 3‘）玫瑰是红色月季的颜色比玫瑰浅月季的颜色是什么
 * 
 * 20131024
 * 将原来的代码转为UFT-8格式，加入了简单的结果收集函数，修改了
 * 一些不太明显的错误——主要和第三个例子有关。修改了MainSpace中用来
 * 找激活强度最大和次大节点的函数。
 * 然后浏览一遍其他的类，痛苦地发现实现同样功能的函数都有问题，需要修改。
 * 这里的有问题是指逻辑上有问题，但实际使用中可能反而没问题（其实现在就
 * 是这种情况）。因为在各个属性空间和对象空间中，往往没有激活状态这个变量，
 * 就算有，除了处于最低激活状态的节点，其他都节点不太可能有相同的激活状态。
 * 这种情况和MainSpace中的节点是完全不同的。
 * 
 * 
 * 20131025
 * 第三个例子仍旧有问题，发现是“。”和“？”这类punctuation没有很好地处理，
 * 也发现了存在了n久但一直没发现的错误。程序目前的状态姑且算是可以当作
 * “实验室中的玩具”系统了。想想之前的c++版本竟然硬是让我运行起来，而且结果
 * 看起来都对，我就觉得真是侥幸。那个东西到底有多僵硬，到底有多少隐藏的错误，
 * 真是难以想象。
 * 今天还要把项目代码中的注释整理下，去掉那些没有意义的，日志形式的注释。
 */


public class Mind {
	
	int totalcount;//输入字符串的计数
	int strSize;//输入字符串的长度
	int thetimes=0;//用于测试的变量
	
	boolean iffinished;
	
	List<WordNode> wordList = new ArrayList<WordNode>();
	String[] CurrentSemanticOperationList=null;
	List<Integer> DelayedOperationList=new ArrayList<Integer>();
	
	//其他各个模块对象
	MainSpace reiMainSpace;
	ImageSpace reiImageSpace;
	AttributeSpace reiAttributeSpace;
	
	//用于记录各个模块当前状态的string
	String answer="";

	
	public Mind(){
		Init();
		reiMainSpace=new MainSpace();
		reiImageSpace=new ImageSpace();
		reiAttributeSpace=new AttributeSpace();
	}
	
	//初始化函数
	//总之装载字典什么的工作都在这里。
	public void Init(){
		totalcount=0;
		LoadDictionary();
		iffinished=false;
	}
	
	//语义理解的总函数。
	//作用：
	//0.记录输入字符串的长度
	//1.读入字符，判断是否为标点，如果是标点，则跳过2，并且跳过之后语义操作实现的部分。
	//2.所有节点激活强度-1
	//3.比对字典
	//4.执行延时的语义操作
	//5.实现当前语义操作
	//6.根据标志判断程序是否已经终结

	public void CognitiveProcess(String[] in){
		
		//0.记录输入字符串的长度
		strSize=in.length;
		boolean isPunctuation=false;
		
		while(totalcount<strSize){//当输入的字符串没有处理完的时候
			//1&2.读入字符，判断是否为标点，如果是标点，则跳过1
			String str=in[totalcount];
		
			if(!str.equals("。") && !str.equals("?")){
				ReduceActivatedLevel();
				++thetimes;
			}
			else{
				isPunctuation=true;
			}
		

			//3.比对字典
			//暂时没有处理在字典中不存在的词的情况
			int dictionarysize=wordList.size();
			WordNode testwordnode=new WordNode();
			WordNode currentWordNode;
			for(int i=0;i<dictionarysize;i++){
				testwordnode=wordList.get(i);
				if(str.equals(testwordnode.Symbol)){
					break;
				}
			}
			currentWordNode=testwordnode;
		
		
			//4.执行延时的语义操作
			boolean ifdelayed=DoDelayedOperation(currentWordNode.Symbol);
			
			if(!isPunctuation){
				//5.实现当前语义操作
				if(currentWordNode.SemanticType==3){
					//1
					FillCurrentSemanticOperationList(currentWordNode.Symbol);
					int length=CurrentSemanticOperationList.length;
					for(int i=0;i<length;i++){
						int id=Integer.parseInt(CurrentSemanticOperationList[i]);
						SemanticOperationDistribute(id);
					}
				}
				else if(!ifdelayed && currentWordNode.SemanticType==0){
					//当遇上对象的时候
					reiMainSpace.ActivateCognitiveGraph(currentWordNode.Symbol, 0);
					reiImageSpace.ActivateImageSpace(currentWordNode.Symbol);
					reiAttributeSpace.ActivateObjectSpace(currentWordNode.Symbol);
				}
				else if(currentWordNode.SemanticType==2){
					//当遇上属性的时候
					reiMainSpace.AssignmentRelation("拥有");//这个好像是重复了
					reiMainSpace.AssignmentTypeInCognitiveGraph(currentWordNode.SemanticType);
					reiMainSpace.AssignmentSymbolInCognitiveGraph(currentWordNode.Symbol);
					reiAttributeSpace.ActivateColorSpace(currentWordNode.Symbol);
				}
			}
			
			isPunctuation=false;
			
			//6.根据标志判断程序是否已经终结
			if(iffinished)
			{
				break;//总感觉这里有问题
			}
			
			++totalcount;
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//                                          延时操作相关                                                                                                                   //
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public void InsertDelayedOperationList(int operationnum){
		Integer newint=new Integer(operationnum); 
		DelayedOperationList.add(newint);
	}
	
	public void DeleteDelayedOperationList(){//这个貌似也不需要呢
		if(DelayedOperationList.isEmpty()){
			//什么都不做
		}
		else{
			DelayedOperationList.remove(0);//把第一个元素去掉
		}
	}
	
	public boolean DoDelayedOperation(String word){
		int j=DelayedOperationList.size();
		if(j!=0)//如果延时操作的话
		{
			int count=0;
			if(j!=0)
			{
				for(int i=0;i<j;i++)
				{
					switch(DelayedOperationList.get(i).intValue())
					{
					case 3:
						{
							reiMainSpace.ActivateCognitiveGraph(word,GetType(word));
							count++;
						}
					break;
					case 4:
						{
							reiMainSpace.AssignmentSymbolInCognitiveGraph(word);
							count++;
						}
						break;
					case 5:
						{
							reiImageSpace.AssignmentSymbolInImageSpace(word);
							count++;
						}
						break;
					case 6:
						{
							reiMainSpace.ActivateSecondHighActivatedLevelNodeInCognitiveGraph();
							count++;
						}
						break;
					case 7:
						{
							reiAttributeSpace.AssignmentSymbolInForceSpace(word);
							count++;
						}
					break;
			
					case 10:
						{
							ActivateCognitiveGraphEX(word,Match(GetType(word)));
							count++;
						}
						break;
					}
				}
			}
			for(int i=0;i<count;i++)
			{
				DeleteDelayedOperationList();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//                                         其他语义理解辅助函数                                                                                                       //
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public int Match(int type){
		//20130709
		//同样是在C++的版本上直接改
		//1.找到当前激活强度为1的节点，提取其Symbol（A）和类型
		//2.从note中读入Symbol（B）和类型
		//3.根据类型生成返回值
		// 3.1 对象 && 属性值 0
		// 3.2 对象 && 属性名 1
		// 3.3 对象 && 关系   2
		// 3.4 对象 && 对象   3
		//话说，Symbol好像没有提取的必要啊……
		
		//这里也要改，因为编号改了……
		//
		// 20130705
		// 关于语义类型：
		// 暂时还按照三分法分类。
		// 用整形数表示。
		// 对象：0
		// 对象的属性1：1（感知觉属性值）
		// 对象的属性2：2（感知觉属性名）
		// 对象间的关系：3
		// 未确定类型：4（20130706）
		//
		// 0&1=0
		// 0&2=1
		// 0&3=2
		// 0&0=3
		// 1&0=4
		
		//1&2
		int A=100;
		int B=100;

		B=type;

		int high=-1;
		int GraphSize=MainSpace.CognitiveGraph.size();
		high=MainSpace.CognitiveGraph.get(0).ObjOrAttri.get(0).ActivatedLevel;
		for(int i=0;i<GraphSize;i++){
			int ListSize=MainSpace.CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++){
				if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel>high){
					high=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel;
				}
			}
		}

		int count=0;
		for(int i=0;i<GraphSize;i++)
		{
			if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==high)
			{
				A=MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(0).OriginalType;
				count=1;
			}
		}

		if(count==0)
		{
			//报告一个错误
		}

		//3
		if(A==0 && B==1 )
		{
			return 0;
		}
		else if(A==0 && B==2 )
		{
			return 1;
		}
		else if(A==0 && B==3 )
		{
			return 2;
		}
		else if(A==0 && B==0 )// 
		{
			return 3;
		}
		else if(A==2 && B==0)//
		{
			return 4;
		}
		else
		{
			return 100;
		}
	}
	
	public int GetType(String word){
		int type=100;
		int ListSize=wordList.size();
		for(int i=0;i<ListSize;i++){
			if(wordList.get(i).Symbol.equals(word)){
				type=wordList.get(i).SemanticType;
				break;
			}
		}
		return type;
		
	}
	
	public void ReduceActivatedLevel(){
		reiMainSpace.ReduceActivatedLevel();
		reiImageSpace.ReduceActivatedLevel();
		reiAttributeSpace.ReduceActivatedLevel();
	}
	
	public void ActivateCognitiveGraphEX(String symbol, int matchresult){

		if(symbol.equals("红色") || symbol.equals("玫瑰")){
			int result;
			result=matchresult;

			//1
			if(result==0)//前一个词是对象，后一个词是属性值
			{
				//1.建立一个空节点
				//2.将这个空节点链接到表示前一个词的节点后面
				//3.对节点的Symbol和Relation赋值//注意，在例子中，属性名只有颜色。所以这里就把过程简单化了，否则应该检查属性名是什么
			
				//1
				reiMainSpace.ActivateNewNodeInCognitiveGraph();
				//2
				reiMainSpace.LinkInCognitiveGraph();
				//3
				reiMainSpace.AssignmentRelation("拥有");
				reiMainSpace.AssignmentSymbolInCognitiveGraph(symbol);
				reiAttributeSpace.ActivateColorSpace(symbol);
			}
			else if(result==4)//前一个词是属性(名)，后一个词是对象
			{
				//1.将属性（也就是前一个词）的激活状态改为3
				//2.找到对象的对应属性（这样说起来，“拥有”和“属于”其实有很大的问题啊），把属性的激活状态改为3
				// 2.1找到后一个词的属性，记录Symbol
				// 2.2根据Symbol将激活状态改为3

				//1
				int GraphSize=MainSpace.CognitiveGraph.size();
				int second=MainSpace.GetCurrentSecondHighActivatedNodeAL();
				for(int i=0;i<GraphSize;i++)//广度遍历
				{
					int ListSize=MainSpace.CognitiveGraph.get(i).ObjOrAttri.size();
					for(int ii=0;ii<ListSize;ii++)
					{
						if(MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==second)//20131024 这里需要修改
						{
							MainSpace.CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel=6;
						}
					}
				}

				reiMainSpace.ActivateCognitiveGraph(symbol,0);
			}
			else 
			{
				;
			}
		}
	}
	
	public void FillCurrentSemanticOperationList(String word){
		//功能：
		//找到当前处理词的语义操作列表，并填充CurrentSemanticOperationList
		
		String SemanticOperation="";
		int ListSize=wordList.size();
		for(int i=0;i<ListSize;i++){
			if(wordList.get(i).Symbol.equals(word)){
				SemanticOperation=wordList.get(i).SemanticOperation;
				break;
			}
		}
		CurrentSemanticOperationList=SemanticOperation.split(" ");
	}

	
	public void SemanticOperationDistribute(int id){
		//功能：
		//根据Id调用对应语义推理模块的处理程序
		
		if(id>=1 && id<100){
			reiMainSpace.Process(id);
		}
		else if(id>=101 && id<200){
			reiImageSpace.Process(id);
		}
		else if(id>=201 && id<500){
			reiAttributeSpace.Process(id);
		}
		else if(id>=1001){
			Process(id);
		}
		else if(id==0){
			answer+=reiImageSpace.theStateOfImageSpace;
			answer+=reiAttributeSpace.theStateOfColorSpace;
			answer+=reiAttributeSpace.theStateOfForceSpace;
			iffinished=true;
		}
	}
	
	public void Process(int id){
		switch(id){
			case 1003:InsertDelayedOperationList(3);
				break;
			case 1004:InsertDelayedOperationList(4);
				break;
			case 1005:InsertDelayedOperationList(5);
				break;
			case 1006:InsertDelayedOperationList(6);
				break;
			case 1007:InsertDelayedOperationList(7);
				break;
			case 1010:InsertDelayedOperationList(10);
				break;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//                                        坑爹的初始化相关                                                                                                                 //
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//装载字典？感觉好坑爹。不过暂时只能这样
	//20131024 
	//每个数字代表的操作见Documentation.txt
	public void LoadDictionary(){
		WordNode node;
		//第一个例子中的词汇
		node=new WordNode("兔子",0,"");
		wordList.add(node);
		node=new WordNode("的",3,"1 41");
		wordList.add(node);
		node=new WordNode("左边",3,"13 102 141");
		wordList.add(node);
		node=new WordNode("是",3,"1005 1004 1010");
		wordList.add(node);
		node=new WordNode("松鼠",0,"");
		wordList.add(node);
		node=new WordNode("上边",3,"11 102 143");
		wordList.add(node);
		node=new WordNode("麻雀",0,"");
		wordList.add(node);
		node=new WordNode("和",3,"1006 1003");
		wordList.add(node);
		node=new WordNode("关系",3,"499 199 0");//20131024 在原来的基础上加入199
		wordList.add(node);
		node=new WordNode("什么",3,"399 0");
		wordList.add(node);
		
		//第二个例子中的词汇
		node=new WordNode("小王",0,"");
		wordList.add(node);
		node=new WordNode("推",3,"1 401 301 102 41 41 41 15 16 17 145 1004 1004 1004 1005 1007");
		wordList.add(node);
		node=new WordNode("箱子",0,"");
		wordList.add(node);
		node=new WordNode("动",3,"151 341");
		wordList.add(node);
		node=new WordNode("为什么",3,"399 0");
		wordList.add(node);
		
		//第三个例子中的词汇
		node=new WordNode("玫瑰",0,"");
		wordList.add(node);
		node=new WordNode("月季",0,"");
		wordList.add(node);
		node=new WordNode("红色",1,"");
		wordList.add(node);
		node=new WordNode("颜色",2,"");
		wordList.add(node);
		node=new WordNode("比",3,"1010");
		wordList.add(node);
		node=new WordNode("浅",3,"241");
		wordList.add(node);			
	}
	
	//20131024 
	//为了循环输入，有必要重置所有状态，重置所有属性空间。
	//不过在公司是不想写了。
	//姑且把以前的明显的错误都改了。
	public void Reset(){
		//
	}

}
