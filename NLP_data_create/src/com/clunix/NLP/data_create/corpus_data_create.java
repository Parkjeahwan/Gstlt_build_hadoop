package com.clunix.NLP.data_create;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.clunix.NLP.data_create.mecab;
import com.clunix.NLP.data_create.post_modif;
import com.clunix.NLP.data_create.pre_modif;

public class corpus_data_create
{
	public static String result_No_space;
	public static String result;
	public static List<String> S = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException
	{
		String str = "";
		
		String filepath1 = "/shr/data/NLP_160330/corpus_1000_article.txt";
		String filepath2 = "/shr/data/NLP_160330/corpus_mecab_space_pr1.txt";
		String filepath3 = "/shr/data/NLP_160330/corpus_mecab_pr1.txt";
		
/*		String filepath1 = args[0];
		String filepath2 = args[1];
		String filepath3 = args[2];
		int start = Integer.valueOf(args[3]);
		int end = Integer.valueOf(args[4]);*/
		
		BufferedReader br = new BufferedReader(new FileReader(filepath1));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(filepath2, false));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(filepath3, false));
		post_modif.importVLNoun("/shr/backup_tmp/mecab_trans_table.txt");
		
		int read_start = 0;
		
		while ((str = br.readLine()) != null) {
			read_start++;
			if (read_start < 0) 
				continue;
			if (read_start == 1000001)
				break;
			analyzer(str);
			if (read_start % 100000 == 0) {
				System.out.print(read_start + " ");
			}
			if (read_start % 1000000 == 0) {
				System.out.println("");
			}
			bw1.write(result+"\n");
			bw2.write(result_No_space+"\n");
		}
	
		br.close();
		bw1.close();
		bw2.close();
	}

	public static String analyzer(String str) throws IOException, InterruptedException
	{
		result = "";
		result_No_space = "";
		/*pre_modif Modif = new pre_modif();										// 전처리 함수
		Modif.set_modif_config();			
		//Modif.set_collect_mode(true);
		//Modif.set_regist_mode(true);		
		Modif.register_undefined(str);*/
		
		mecab mecab = new mecab();
		List<String> list = mecab.analyzer_space(str, 1); // 형태소 분석 함수
		String rm_str = mecab.trans_ECS(list.get(0)); // 형태소 분석후 알맞게 전처리 하는 함수
		rm_str = mecab.trans_JKB(rm_str); // 형태소 분석후 알맞게 전처리 하는 함수
		rm_str = mecab.trans_vxecv(rm_str); // 형태소 분석후 알맞게 전처리 하는 함수
		String[] arr2 = rm_str.split(" ");
		String tmp_t = "";
		for (int i = 0; i < arr2.length; i++) {
			tmp_t = tmp_t + mecab.phonetic_reduced(arr2[i]) + " ";
		}
		rm_str = tmp_t;
			// 후처리 함수
		rm_str = post_modif.revise_parsing3(rm_str);	// 후처리 함수
		rm_str = post_modif.revise_parsing2(rm_str);	// 후처리 함수
		rm_str = post_modif.revise_parsing(rm_str);		// 후처리 함수
		rm_str = post_modif.revise_parsing4(rm_str);
		
		String[] arr = rm_str.split(" ");
		String tmp = "";
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals("###/SPACE")) {
				S.add(tmp.trim());
				result_No_space = result_No_space + tmp;
				tmp = "";
			}
			else {
				tmp = tmp + arr[i] + " ";
			}
		}
		if (!tmp.trim().equals("")) {
			S.add(tmp.trim());
			result_No_space = result_No_space + tmp;
		}
		String node = "";
		for (int i = 0; i < S.size() - 1; i++) {
			node = node + S.get(i) + " ###/SPACE ";
		}
		if(S.size() > 0){
			node = node + S.get(S.size() - 1);
		}
		result = node;
		result = result.trim();
		result_No_space = result_No_space.trim();
		
		//System.out.println(result);
		//System.out.println(result_No_space);
		S.clear();
		return result;
	}
}