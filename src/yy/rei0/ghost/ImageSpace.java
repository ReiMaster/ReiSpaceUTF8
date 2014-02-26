package yy.rei0.ghost;

import java.util.ArrayList;
import java.util.List;

/*
 * 用于处理意象空间的推理。
 * 感觉这东西最后会成为最复杂的底层推理模块。
 */


public class ImageSpace {
	
	List<ImageSpaceNode> imagespace=new ArrayList<ImageSpaceNode>();
	
	//20131024
	//用以记录ImageSpace内的状态转化而成的自然语言语句
	public String theStateOfImageSpace="";

	//20130705目前还是二维的意象空间
	int high;
	int width;
	
	public ImageSpace(){
		//先放在这里
		high=100;
		width=100;
	}
	
	int count=0;
	
	
    ///////////////////////////////////////////////////////////////////////////////////////
    //                                     核心函数                                                                                                    //
    ///////////////////////////////////////////////////////////////////////////////////////
	public void ActivateImageSpace(String symbol){
		//首先要判断是不是第一个对象
		if(imagespace.size()==0){//说明是第一个对象
			ImageSpaceNode newImageSpaceNode=new ImageSpaceNode(symbol,(int)(high/2),(int)(width/2));//置于意象空间的中心
			imagespace.add(newImageSpaceNode);
		}
		else{//同样要判断是否已经出现在imagespace中了
			boolean ifExist=false;
			int imagespaceSize=imagespace.size();
			for(int i=0;i<imagespaceSize;i++){
				if(imagespace.get(i).LinkedObject.equals(symbol)){
					imagespace.get(i).ActivatedLevel=7;
					ifExist=true;
					break;
				}
			}
			
			if(!ifExist){//说明还没有出现。如果没有确定的位置值，那么就需要随机赋值。
				boolean ifSame=true;
				int count=0;
				int random;
				do{
					random=(int)Math.floor(Math.random()*99+1);
					for(int i=0;i<imagespaceSize;i++){
						if(imagespace.get(i).position_x==random){
							count++;
							break;
						}
					}
					if(count==0){
						ifSame=false;
					}
				}while(ifSame);
				ImageSpaceNode newImageSpaceNode=new ImageSpaceNode(symbol,random,(int)Math.floor(Math.random()*99+1));
				imagespace.add(newImageSpaceNode);
			}
		}
		
	}
	
	public void ActivateImageSpace(){
		ImageSpaceNode newnode=new ImageSpaceNode();
		newnode.LinkedObject="";
		newnode.ActivatedLevel=7;//表示为最大激活状态
		newnode.position_x=0;//也就是没有值
		newnode.position_y=0;
		imagespace.add(newnode);//加入类表队尾;
	}
	
	public void BasicMove(int changeX, int changeY){
		//功能：
		//修改位移对象的位置属性值。由于是自参照的，所以只要找到激活强度最高的节点就好。
		
		//先找high
		int high;
		int imagespacesize=imagespace.size();
		high=imagespace.get(0).ActivatedLevel;
		for(int i=0;i<imagespacesize;i++){
			if(imagespace.get(i).ActivatedLevel>high){
				high=imagespace.get(i).ActivatedLevel;
			}
		}

		for(int i=0;i<imagespacesize;i++)
		{
			if(imagespace.get(i).ActivatedLevel==high)
			{
				imagespace.get(i).position_x+=changeX;
				imagespace.get(i).position_y+=changeY;
				break;
			}
		}
	}
	
	public void BasicSpaceReasoning(int changeX, int changeY){
		//功能：
		//找到当前激活强度最高和次高的节点，然后将激活强度最高的节点的位置属性值根据次高节点的位置属性值进行修改
		
		//找到激活强度最高和次高的节点的激活强度
		int low;
		int high;
		//先找high
		int imagespacesize=imagespace.size();
		high=imagespace.get(0).ActivatedLevel;
		low=imagespace.get(0).ActivatedLevel;
		for(int i=0;i<imagespacesize;i++){
			if(imagespace.get(i).ActivatedLevel>high){
				high=imagespace.get(i).ActivatedLevel;
			}
		}
		//再找low
		int temp=0;
		for(int i=0;i<imagespacesize;i++){
			if(imagespace.get(i).ActivatedLevel>low){
				temp=imagespace.get(i).ActivatedLevel;
				if(temp==high){
					break;
				}
				else{
					low=temp;
				}
			}
		}
		
		//1
		int x=0;//20130706这种赋值到底怎么才能不出那个搞笑的错误提示？
		int y=0;

		for(int i=0;i<imagespacesize;i++)
		{
			if(imagespace.get(i).ActivatedLevel==low)
			{
				x=imagespace.get(i).position_x;
				y=imagespace.get(i).position_y;
				break;
			}
		}

		//2
		for(int i=0;i<imagespacesize;i++)
		{
			if(imagespace.get(i).ActivatedLevel==high)
			{
				imagespace.get(i).position_x=x+changeX;
				imagespace.get(i).position_y=y+changeY;
				break;
			}
		}
		
	}
	
	public void AssignmentSymbolInImageSpace(String symbol){
		//功能：
		//找到激活强度最高的节点，对这个节点的Symbol属性进行赋值
		
		//先找high
		int high;
		int imagespacesize=imagespace.size();
		high=imagespace.get(0).ActivatedLevel;
		for(int i=0;i<imagespacesize;i++){
			if(imagespace.get(i).ActivatedLevel>high){
				high=imagespace.get(i).ActivatedLevel;
			}
		}

		for(int i=0;i<imagespacesize;i++)
		{
			if(imagespace.get(i).LinkedObject.equals("") && imagespace.get(i).ActivatedLevel==high)
			{
				imagespace.get(i).LinkedObject=symbol;
			}
		}
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//                                        其他辅助用函数                                                                                                     //
	///////////////////////////////////////////////////////////////////////////////////////////////
	//20131024
	//用以将ImageSpace内的状态转化为自然语言语句
	public void GetRelationFromImageSpace(){
		//算法：
		//将theStateOfImageSpace设置为空字符串
		//遍历list
		//找到LinkedObject不等于空字符串的节点，取得position_x和position_y，生成说明语句
		
		theStateOfImageSpace="";
		int x=0;
		int y=0;
		String str="";	
		int imagespacesize=imagespace.size();
		for(int i=0;i<imagespacesize;i++){
			if(!imagespace.get(i).LinkedObject.equals("") && imagespace.get(i).LinkedObject!=null){
				x=imagespace.get(i).position_x;
				y=imagespace.get(i).position_y;
				str=imagespace.get(i).LinkedObject;
				theStateOfImageSpace+=str+"的位置是：X("+x+") ; Y("+y+")。\n";
			}
		}

	}
	
	public void ReduceActivatedLevel(){
		
		//20130708总之在C++的版本上修改
		int imagespacesize=imagespace.size();
		for(int i=0;i<imagespacesize;i++)
		{
			if(imagespace.get(i).ActivatedLevel!=0)
			{
				imagespace.get(i).ActivatedLevel-=1;;
			}
			else
			{
				;//是0的话就什么都不做
			};
		}
	}
	
	public void Process(int id){
		switch(id){
			case 102:ActivateImageSpace();
				break;
			case 141:BasicSpaceReasoning(-5,0);//左
				break;
			case 142:BasicSpaceReasoning(5, 0);//右
				break;
			case 143:BasicSpaceReasoning(0, -5);//上
				break;
			case 144:BasicSpaceReasoning(0, 5);//下
				break;
			case 145:BasicSpaceReasoning(0, -3);//下
				break;
			case 151:BasicMove(0,-3);
				break;
			case 199:{
				GetRelationFromImageSpace();
//				count++;//测试用
			}
				break;
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//                                             辅助类                                                                                                               //
	//////////////////////////////////////////////////////////////////////////////////////////////////	
	class ImageSpaceNode{

		String LinkedObject;//与这个位置相关的对象。
		int ActivatedLevel;//节点的激活等级
		int position_x;//x轴的坐标
		int position_y;//y轴的坐标
		
		//20130705姑且提供两个构造函数，先放着，到底有没有用以后再说。
		public ImageSpaceNode(){
			
		}
		
		public ImageSpaceNode(String symbol, int x, int y){
			LinkedObject=symbol;
			position_x=x;
			position_y=y;
			ActivatedLevel=7;
		}
		
	}

}
