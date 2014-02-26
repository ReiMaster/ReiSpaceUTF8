package yy.rei0.ghost;

import ICTCLAS.I3S.AC.ICTCLAS50;

/*
 * 暂时一个方法就够了，以后还有需要的话再添加
 */
public class ICTCLAS {//这个感觉更像是接口啊
	
	public static String PPStr=null;//ParagraphProcessString的缩写
	
	public static String testICTCLAS_ParagraphProcess(String sInput)
	{
		
		try
		{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
			}

			//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			testICTCLAS50.ICTCLAS_SetPOSmap(2);

			//导入用户词典前分词
			byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0, 0);//分词处理(仅分词，未标注)
			System.out.println(nativeBytes.length);
			PPStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			
			
			//输入文件名
			String Inputfilename = "test.txt";
			byte[] Inputfilenameb = Inputfilename.getBytes();//将文件名string类型转为byte类型

			//分词处理后输出文件名
			String Outputfilename = "test_result.txt";
			byte[] Outputfilenameb = Outputfilename.getBytes();//将文件名string类型转为byte类型

			//文件分词(第一个参数为输入文件的名,第二个参数为文件编码类型,第三个参数为是否标记词性集1 yes,0 no,第四个参数为输出文件名)
			testICTCLAS50.ICTCLAS_FileProcess(Inputfilenameb, 0, 0, Outputfilenameb);
			
	
			//释放分词组件资源
			testICTCLAS50.ICTCLAS_Exit();
				
		}
		catch (Exception ex)
		{
			//do noting
		}
		
		//返回结果
		return PPStr;
	}

}
