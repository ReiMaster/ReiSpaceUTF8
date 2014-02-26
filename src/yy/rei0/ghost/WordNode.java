package yy.rei0.ghost;

/*
 * 20130705
 * 这个是不是应该单独一类呢？
 * 以后还要好好考虑。
 */
public class WordNode {

	//这里的变量需要加“public”关键字吗？
	String Symbol;//用来记录词汇符号
	int SemanticType;//语义类型（这点也要仔细考虑。是仍旧按照三分法分类，还是按照处理模块分类？）
	String SemanticOperation;//语义操作序列（好烦）
	
	public WordNode(String symbol,int type,String operation){
		Symbol=symbol;
		SemanticType=type;
		SemanticOperation=operation;
	}
	
	public WordNode(){
		Symbol="";
		SemanticType=0;
		SemanticOperation="";
		
	}
}


/*
test 
*/