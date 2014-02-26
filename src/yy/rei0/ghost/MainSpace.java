package yy.rei0.ghost;

import java.util.*;


public class MainSpace {
	
	//测试用变量
	int thetimes=0;
	
	public static List<GraphNodeList> CognitiveGraph=new ArrayList<GraphNodeList>();
	
	public MainSpace(){
		GraphNodeList newGraphNodeList=new GraphNodeList();
		CognitiveGraph.add(newGraphNodeList);
	}
	

    ///////////////////////////////////////////////////////////////////////////////////////
    //                                     核心函数                                                                                                    //
    ///////////////////////////////////////////////////////////////////////////////////////

	public void ActivateCognitiveGraph(String symbol, int type){

		if(CognitiveGraph.size()==1){//判断是不是分析过程中出现的第一个对象
			if(CognitiveGraph.get(0).ObjOrAttri.get(0).Symbol.equals("")){
				CognitiveGraph.get(0).ObjOrAttri.get(0).Symbol=symbol;
				CognitiveGraph.get(0).ObjOrAttri.get(0).OriginalType=type;
			}
		}
		else{//不是分析过程中出现的第一个对象。同样分两种情况。
			
			//看是不是第一次出现的对象（还有属性啥的）
			int GraphSize=CognitiveGraph.size();
			boolean ifExist=false;
			for(int i=0;i<GraphSize;i++){
				int GraphListSize=CognitiveGraph.get(i).ObjOrAttri.size();
				for(int j=0;j<GraphListSize;j++){
					if(CognitiveGraph.get(i).ObjOrAttri.get(j).Symbol.equals(symbol)){
						CognitiveGraph.get(i).ObjOrAttri.get(j).ActivatedLevel=7;
						ifExist=true;
					}
				}
				
			}
			
			if(!ifExist){//如果是第一次出现
				GraphNodeList newGraphNodeList=new GraphNodeList(symbol, type);
				CognitiveGraph.add(newGraphNodeList);
			}
		}

	}
	
	public void ActivateNewNodeInCognitiveGraph(){

		GraphNodeList newGraphNodeList=new GraphNodeList();
		CognitiveGraph.add(newGraphNodeList);
	}

	
	public void ActivateSecondHighActivatedLevelNodeInCognitiveGraph(){
		
		//功能：
		//找到激活强度次高的节点，将它的激活强度设定到最高
		int Graphsize=CognitiveGraph.size();
		int low=GetCurrentSecondHighActivatedNodeAL();

		for(int i=0;i<Graphsize;i++)//广度（宽度）遍历？
		{
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();;
			for(int ii=0;ii<ListSize;ii++)
			{
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==low)
				{
					CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel=7;
				}
			}
		}
	}
	
	public void AssignmentRelation(String relation){
		//功能：
		//找到当前激活状态最高的节点，对其RelationType赋值
		//备注：
		//根据输入的关系，找到相对的关系OpposeRelation
		//根据已经找到的节点，找到对应节点，并对其RelationType赋值
		//这部分设计深刻的对象间关系，不是现在能一股脑处理好的。暂时只能这样了。
		

		String OpposeRelation="";
		if(relation.equals("左"))
		{
			OpposeRelation="右";
		}
		else if(relation.equals("右"))
		{
			OpposeRelation="左";
		}
		else if(relation.equals("上"))
		{
			OpposeRelation="下";
		}
		else if(relation.equals("下"))
		{
			OpposeRelation="上";
		}
		else if(relation.equals("接触"))
		{
			OpposeRelation="接触";
		}
		else if(relation.equals("施力"))
		{
			OpposeRelation="受力";
		}
		else if(relation.equals("强制"))
		{
			OpposeRelation="被强制";
		}
		else if(relation.equals("拥有"))
		{
			OpposeRelation="属于";
		}
		else if(relation.equals("属于"))
		{
			OpposeRelation="拥有";
		}

		String SymbolA="";//与当前要赋值的节点连接的节点的所表示的词
		String SymbolB="";//当前要赋值的节点所表示的词
		
		//找到当前激活强度最高的节点的激活强度
		int high;

		high=GetCurrentMostHighActivatedNodeAL();
		
		int GraphSize=CognitiveGraph.size();
		int count=0;//因为需要赋值的应该只有一个，所以只要执行了赋值，就跳出循环
		for(int i=0;i<GraphSize;i++)
		{
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			SymbolA=CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
			for(int ii=1;ii<ListSize;ii++)//从第二个开始，所以是“1”不是“0”
			{
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==high && CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType.equals(""))
				{
					CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType=relation;
					SymbolB=CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol;
					count=1;
					break;
				}
			}
			if(count==1)
			{
				break;
			}
		}
		
		count=0;//同样因为需要赋值的应该只有一个，所以只要执行了赋值，就跳出循环

		for(int i=0;i<GraphSize;i++)
		{
			if(CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol.equals(SymbolB))
			{
				int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
				for(int ii=1;ii<ListSize;ii++)//同样从list中的第二个元素开始找
				{
					if(CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol.equals(SymbolA) && CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType.equals(""))
					{
						CognitiveGraph.get(i).ObjOrAttri.get(ii).RelationType=OpposeRelation;
						count=1;
						break;
					}
				}
			}
			if(count==1)
			{
				break;
			}
		}
	}
	

	public void AssignmentSymbolInCognitiveGraph(String symbol){
		//功能：
		//找到激活强度最高的节点，对节点的Symbol属性赋值
		
		//先找到high
		int high;
		int GraphSize=CognitiveGraph.size();
		high=CognitiveGraph.get(0).ObjOrAttri.get(0).ActivatedLevel;
		for(int i=0;i<GraphSize;i++){
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++){
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel>high){
					high=CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel;
				}
			}
		}

		for(int i=0;i<GraphSize;i++)
		{
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++)
			{
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol.equals("") && CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==high)
				{
					CognitiveGraph.get(i).ObjOrAttri.get(ii).Symbol=symbol;
					break;

				}
			}
		}
	}
	
	public void AssignmentTypeInCognitiveGraph(int type){
	
		int high=GetCurrentMostHighActivatedNodeAL();;
		int GraphSize=CognitiveGraph.size();
		for(int i=0;i<GraphSize;i++)//广度遍历
		{
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++)
			{
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel==high)
				{
					CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel=7;
					CognitiveGraph.get(i).ObjOrAttri.get(ii).OriginalType=type;
					break;//一个list<GraphNote>中应该至多有一个符合条件（至少现在是这样）

				}
			}
		}
	}
	
	public void LinkInCognitiveGraph(){
		//0.找到激活强度最高和次高的节点的激活级别，分别记为high和low
		//1.找到激活状态最高的节点，构建一个和它相同的GraphNode
		//2.找到激活状态次高的节点，将刚刚完成的GraphNote连接到它的队尾
		//3.构建一个和激活状态次高的节点相同的节点，连接它到激活状态最高的节点的后面
		
		//0
		int low=GetCurrentSecondHighActivatedNodeAL();
		int high=GetCurrentMostHighActivatedNodeAL();

		int GraphSize=CognitiveGraph.size();

		GraphNode first=new GraphNode();
		GraphNode second=new GraphNode();
		
		//1
		for(int i=0;i<GraphSize;i++){
			if(CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==high){
				
				first.ActivatedLevel=high;
				first.Symbol=CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
				first.OriginalType=CognitiveGraph.get(i).ObjOrAttri.get(0).OriginalType;
				first.RelationType=CognitiveGraph.get(i).ObjOrAttri.get(0).RelationType;
				break;
			}
		}
		
		//2(3)
		for(int i=0;i<GraphSize;i++){
			if(CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==low){
				
				CognitiveGraph.get(i).ObjOrAttri.add(first);//把first连接上去
				
				second.ActivatedLevel=CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel;
				second.Symbol=CognitiveGraph.get(i).ObjOrAttri.get(0).Symbol;
				second.OriginalType=CognitiveGraph.get(i).ObjOrAttri.get(0).OriginalType;
				second.RelationType=CognitiveGraph.get(i).ObjOrAttri.get(0).RelationType;
				break;
			}
		}
		
		//3
		for(int i=0;i<GraphSize;i++){
			if(CognitiveGraph.get(i).ObjOrAttri.get(0).ActivatedLevel==high){

				CognitiveGraph.get(i).ObjOrAttri.add(second);//把second连接上去
				break;
			}
		}
		
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//                                        其他辅助用函数                                                                                                     //
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void GetObjectRelation(){
		int fuck=0;
		System.out.println(fuck);
	}
	
	public void ReduceActivatedLevel(){
		thetimes++;
		
		//20130708总之直接在C++的版本上修改
		int GraphSize=CognitiveGraph.size();
		for(int i=0;i<GraphSize;i++)//广度遍历
		{
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++)
			{
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel!=0)
				{
					CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel-=1;
				}
				else
				{
					;//是0的话就什么都不做
				}
			}
		}
	}
	
	public static int GetCurrentMostHighActivatedNodeAL(){
		//功能：
		//得到当前激活强度最高的节点的激活强度
		int high;
		high=CognitiveGraph.get(0).ObjOrAttri.get(0).ActivatedLevel;
		int GraphSize=CognitiveGraph.size();
		for(int i=0;i<GraphSize;i++){
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++){
				if(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel>high){
					high=CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel;
				}
			}
		}
		
		return high;
	}
	
	public static int GetCurrentSecondHighActivatedNodeAL(){
		//功能：
		//得到当前激活强度次高的节点的激活强度

		int low=0;
		int GraphSize=CognitiveGraph.size();
		Vector<Integer> useforsort = new Vector<Integer>();
		for(int i=0;i<GraphSize;i++){
			int ListSize=CognitiveGraph.get(i).ObjOrAttri.size();
			for(int ii=0;ii<ListSize;ii++){
				useforsort.add(CognitiveGraph.get(i).ObjOrAttri.get(ii).ActivatedLevel);
				}
		}

		int length=useforsort.size();
		for(int i=1;i<length;i++){
			for(int j=length-1;j>=i;j--){
				if(useforsort.get(j-1)<useforsort.get(j)){
					int temp=useforsort.get(j-1);
					useforsort.set(j-1, useforsort.get(j));
					useforsort.set(j, temp);
				}
			}
		}
		
		int max=useforsort.get(0);
		for(int i=0;i<length;i++)
		{
			if(useforsort.get(i)<max){
				low=useforsort.get(i);
				break;
			}
		}
		return low;
	}
	
	public void Process(int id){
		switch(id){
			case 1:ActivateNewNodeInCognitiveGraph();
				break;
			case 2://现在是空的;
				break;
			case 3:ActivateSecondHighActivatedLevelNodeInCognitiveGraph();
				break;
			case 11:AssignmentRelation("上");
				break;
			case 12:AssignmentRelation("下");
				break;
			case 13:AssignmentRelation("左");
				break;
			case 14:AssignmentRelation("右");
				break;
			case 15:AssignmentRelation("接触");
				break;
			case 16:AssignmentRelation("施力");
				break;
			case 17:AssignmentRelation("强制");
				break;
			case 41:LinkInCognitiveGraph();
				break;
			case 51:GetObjectRelation();
				break;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//                                             辅助类                                                                                                               //
	//////////////////////////////////////////////////////////////////////////////////////////////////	
	public class GraphNode {

		String Symbol;
		String RelationType;
		int OriginalType;
		int ActivatedLevel;
		
		
		public GraphNode(String symbol, int type){
			Symbol=symbol;
			OriginalType=type;
			ActivatedLevel=7;//修改过的激活等级
			RelationType="";
		}
		
		public GraphNode(){//无参数构造函数
			Symbol="";
			OriginalType=4;//未确定类型
			ActivatedLevel=7;//修改过的激活等级
			RelationType="";
		}
	}
	
	public class GraphNodeList{
		
		List<GraphNode> ObjOrAttri=new ArrayList<GraphNode>();
		
		public GraphNodeList(String symbol, int type){
			
			GraphNode theNode=new GraphNode(symbol,type);
			ObjOrAttri.add(theNode);

		}
		
		public GraphNodeList(){//同样的无参数构造函数
			GraphNode theNode=new GraphNode();
			ObjOrAttri.add(theNode);
		}
	}


}
