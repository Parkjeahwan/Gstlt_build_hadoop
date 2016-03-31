package com.clunix.NLP.data_create;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class mecab {
		
	public List<String> analyzer_space(String str, int n) throws IOException, InterruptedException{
		Random random = new Random();
		String r = Integer.toString(random.nextInt(1000000000)+1);
		
		String dir = "/tmp/";
		String shell_file = r+".sh";
		String tmp_file = r+".tmp";
		
		File file = new File(dir+shell_file);
		file.setExecutable(true, true);
		file.setReadable(true, true);
		file.setWritable(true, true);
		
		String permission = "chmod -R 777 "+dir+shell_file;
		
		BufferedWriter bw_sh = new BufferedWriter(new FileWriter(dir+shell_file));
		BufferedWriter bw_tmp = new BufferedWriter(new FileWriter(dir+tmp_file));
		bw_tmp.close();
		str = str.replaceAll("\\p{Space}+"," ");
		
		str = str.replaceAll("\\\\", "\\\\\\\\");
		str = str.replaceAll("\"", "\\\\\"").replaceAll("'", "\'");
		str = str.replaceAll("\\`", "\\\\\\`");

		String command = "echo \""+str.trim()+"\" | "+ "mecab -b 1024000 -N "+Integer.toString(n)+" -d /usr/local/lib/mecab/dic/mecab-ko-dic/ &> "+dir+tmp_file;
		bw_sh.write(command);
		bw_sh.close();
		
		Process process = Runtime.getRuntime().exec(permission);
		process.waitFor();
		
		process = new ProcessBuilder(dir+"./"+shell_file).start();
		process.waitFor();
		
		BufferedReader br1 = new BufferedReader(new FileReader(dir+tmp_file));
		String line;
		
		List<ArrayList<String>> raw_data_list = new ArrayList<ArrayList<String>>();
		List<String> raw_data = new ArrayList<String>();
		while((line = br1.readLine()) != null){
			if(line.equals("EOS")){
				raw_data_list.add(new ArrayList<String>(raw_data));
				raw_data.clear();
				continue;
			}
			if(!line.trim().equals("")){
				raw_data.add(line);
			}
		}
		br1.close();
		List<String> raw_data2 = pre_process(raw_data_list, str.trim().replaceAll("\\\\", ""));
		
		process = Runtime.getRuntime().exec("rm -f "+dir+shell_file);
		process.waitFor();
		process = Runtime.getRuntime().exec("rm -f "+dir+tmp_file);
		process.waitFor();
		
		return raw_data2;	
	}
	
	public List<String> analyzer(String str, int n) throws IOException, InterruptedException{
		Random random = new Random();
		String r = Integer.toString(random.nextInt(1000000000)+1);
		
		String dir = "/tmp/";
		String shell_file = r+".sh";
		String tmp_file = r+".tmp";
		
		File file = new File(dir+shell_file);
		file.setExecutable(true, true);
		file.setReadable(true, true);
		file.setWritable(true, true);
		
		String permission = "chmod -R 777 "+dir+shell_file;
		
		BufferedWriter bw_sh = new BufferedWriter(new FileWriter(dir+shell_file));
		BufferedWriter bw_tmp = new BufferedWriter(new FileWriter(dir+tmp_file));
		bw_tmp.close();
		str = str.replaceAll("\\p{Space}+"," ");
		
		
		str = str.replaceAll("\"", "\\\\\"").replaceAll("`", "\\\\`").replaceAll("\\\\","\\\\");
		str = str.replaceAll("\\`", "\\\\\\`");

		String command = "echo \""+str.trim()+"\" | "+ "mecab -b 1024000 -N "+Integer.toString(n)+" -d /usr/local/lib/mecab/dic/mecab-ko-dic/ &> "+dir+tmp_file;
		bw_sh.write(command);
		bw_sh.close();
		
		Process process = Runtime.getRuntime().exec(permission);
		process.waitFor();
		
		process = new ProcessBuilder(dir+"./"+shell_file).start();
		process.waitFor();
		
		BufferedReader br1 = new BufferedReader(new FileReader(dir+tmp_file));
		String line;
		
		List<ArrayList<String>> raw_data_list = new ArrayList<ArrayList<String>>();
		List<String> raw_data = new ArrayList<String>();
		while((line = br1.readLine()) != null){
			if(line.equals("EOS")){
				raw_data_list.add(new ArrayList<String>(raw_data));
				raw_data.clear();
				continue;
			}
			if(!line.trim().equals("")){
				raw_data.add(line);
			}
		}
		br1.close();
		List<String> raw_data2 = pre_process(raw_data_list);

		process = Runtime.getRuntime().exec("rm -f "+dir+shell_file);
		process.waitFor();
		process = Runtime.getRuntime().exec("rm -f "+dir+tmp_file);
		process.waitFor();
		
		return raw_data2;
	}
	
	public List<String> analyzer_comp(String str, int n) throws IOException, InterruptedException{
		Random random = new Random();
		String r = Integer.toString(random.nextInt(1000000000)+1);
		
		String dir = "/tmp/";
		String shell_file = r+".sh";
		String tmp_file = r+".tmp";
		
		File file = new File(dir+shell_file);
		file.setExecutable(true, true);
		file.setReadable(true, true);
		file.setWritable(true, true);
		
		String permission = "chmod -R 777 "+dir+shell_file;
		
		BufferedWriter bw_sh = new BufferedWriter(new FileWriter(dir+shell_file));
		BufferedWriter bw_tmp = new BufferedWriter(new FileWriter(dir+tmp_file));
		bw_tmp.close();
		str = str.replaceAll("\\p{Space}+"," ");
		
		str = str.replaceAll("\"", "\\\\\"").replaceAll("'", "\'");
		str = str.replaceAll("\\`", "\\\\\\`");

		String command = "echo \""+str.trim()+"\" | "+ "mecab -b 1024000 -N "+Integer.toString(n)+" -d /usr/local/lib/mecab/dic/mecab-ko-dic/ &> "+dir+tmp_file;
		bw_sh.write(command);
		bw_sh.close();
		
		Process process = Runtime.getRuntime().exec(permission);
		process.waitFor();
		
		process = new ProcessBuilder(dir+"./"+shell_file).start();
		process.waitFor();
		
		BufferedReader br1 = new BufferedReader(new FileReader(dir+tmp_file));
		String line;
		
		List<ArrayList<String>> raw_data_list = new ArrayList<ArrayList<String>>();
		List<String> raw_data = new ArrayList<String>();
		while((line = br1.readLine()) != null){
			if(line.equals("EOS")){
				raw_data_list.add(new ArrayList<String>(raw_data));
				raw_data.clear();
				continue;
			}
			if(!line.trim().equals("")){
				raw_data.add(line);
			}
		}
		br1.close();
		List<String> raw_data2 = pre_process_comp(raw_data_list);
		
		process = Runtime.getRuntime().exec("rm -f "+dir+shell_file);
		process.waitFor();
		process = Runtime.getRuntime().exec("rm -f "+dir+tmp_file);
		process.waitFor();

		
		return raw_data2;
	}
	

	public String trans_ECS(String str) {
		String t = str;
		String[] arr = t.split(" ");
		String result = "";
		String temp;

		for (int i = 0; i < arr.length; i++) {
			temp = arr[i];
			if (temp.contains("/EC")) {
				for (int j = i + 1; j < arr.length; j++) {
					if (arr[j].contains("/EP") || arr[j].contains("/SPACE")) {
						continue;
					}

					if (arr[j].contains("/VX") || arr[j].contains("/XSV")) {
						temp = temp.replaceAll("/EC", "/ECS");

					}
					break;
				}
			}
			if (temp.equals("라고/EC") || temp.equals("다고/EC")
					|| temp.equals("려고/EC")
					|| temp.equals("라구/EC")
					|| temp.equals("다구/EC")
					|| temp.equals("려구/EC")
					|| temp.equals("라/EC")
					|| temp.equals("이라/EC")) {
				temp = temp.replaceAll("/EC", "/ECS");
			}
			
			if(!temp.contains("도록/ECS") && temp.contains("도록/EC")){
				temp = temp.replaceAll("/EC", "/ECS");
			}
						
			result = result + temp + " ";
		}

		return result.trim();
	}
	
	public String trans_JKB(String str){
		String result ="";

		if(str.contains("게/EC")){
			String t = str;
			String[] arr = t.split(" ");
			for(int i=0; i<arr.length; i++){
				if(arr[i].equals("게/EC")){
					if(i > 0 && (arr[i-1].contains("/VA") || arr[i-1].contains("/XSA") || arr[i-1].contains("/VCN") || arr[i-1].contains("/VCP"))){		//151001 modify
						arr[i] = "게/JKB";
					}
				}
				
				result = result+arr[i]+" ";
			}
		}else{
			return str;
		}
		
		return result.trim();
	}
	
	public String trans_vxecv(String str){
		String temp ="";
		
		if(str.contains("않/VX") && str.contains("/EC")){
			String[] arr = str.split(" ");
			
			for(int i=0; i<arr.length; i++){
				if(arr[i].equals("않/VX")){
					if(i+2<arr.length){
						String tmp = arr[i+1]+" ";
						if(tmp.contains("/EC ") && (arr[i+2].contains("/V") || arr[i+2].contains("/XSV") || arr[i+2].contains("/XSA"))){
							arr[i+1] = arr[i+1].replaceAll("/EC", "/ECS");
						}
					}
				}
				temp = temp+arr[i]+" ";
			}
			
			return temp.trim();
			
		}else{
			return str;
		}
		
	}
	
	
	
	

	public List<String> pre_process(List<ArrayList<String>> rawlist, String ori_str){
		List<Integer> space = new ArrayList<Integer>();
		ori_str = ori_str.trim();
				
		List<String> sentence_list = new ArrayList<String>();
//		List<ArrayList<String>> sentence_list2 = new ArrayList<ArrayList<String>>();		
		
		for(int i=0; i<rawlist.size(); i++){
			String sentence="";
			String line;
			String word;
			String pos;
			String[] strarr;
			String[] posarr;
			String str = ori_str;
				
			String[] spacearr = str.split("");
			for(int sa=0; sa<spacearr.length; sa++){
				if(spacearr[sa].equals(" ")){
					space.add(sa);
				}
			}
				
			for(int j=0; j<rawlist.get(i).size(); j++){
				line = rawlist.get(i).get(j);
				strarr = line.split("\t");
				word = strarr[0];
				if(str.length() >= word.length()){
					str = str.substring(word.length());
				}
					
				posarr = strarr[1].split(",");
				pos = posarr[0];
					
				if(posarr[4].equals("Inflect")) {
					String temp2 = posarr[7];
					
					temp2 = trans_unicode(temp2);
					temp2 = temp2.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\u3130-\u318E/]", " ");
					String[] tarr = temp2.split(" ");
					if(tarr.length ==1){
						sentence = sentence+word+"/"+tarr[0].split("/")[1]+" ";				
					}else{
	   				for(int i2=0; i2<tarr.length; i2++){
	   					if(tarr[i2].equals("")){
	   						continue;
	        					}  
	   					
	   					if (tarr[i2].endsWith(("/"))) { // mecab-ko-dic update
	   							tarr[i2] = tarr[i2].substring(0, tarr[i2].length()-1);	   					
	   							}
	   					
	   					sentence = sentence+tarr[i2]+" ";	   					
	        				}
					}
				}else{					
					sentence = sentence+word+"/"+pos+" ";
				}
					
				if(str.length() >0 && str.substring(0, 1).equals(" ")){					
					sentence = sentence+"###/SPACE"+" ";
					str = str.substring(1);
				}
					
					
			}
		//	if(sentence.contains(" 게/EC")){
		//		sentence = sentence.replaceAll(" 게/EC", " 게/JKB");
		//	}
		//	sentence_list.add(new ArrayList<String>);			
			sentence_list.add(sentence.trim());
		}
		
		
		return sentence_list;
	}
	
	public List<String> pre_process(List<ArrayList<String>> rawlist){
		List<String> sentence_list = new ArrayList<String>();
		
		for(int i=0; i<rawlist.size(); i++){
			String sentence="";
			String line;
			String word;
			String pos;
			String[] strarr;
			String[] posarr;
			
			for(int j=0; j<rawlist.get(i).size(); j++){
				line = rawlist.get(i).get(j);
				strarr = line.split("\t");
				word = strarr[0];
				posarr = strarr[1].split(",");
				pos = posarr[0];
				
				if(posarr[4].equals("Inflect")) {
					String temp2 = posarr[7];
				
					temp2 = trans_unicode(temp2);
					temp2 = temp2.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\u3130-\u318E/]", " ");
					String[] tarr = temp2.split(" ");
					if(tarr.length ==1){
						sentence = sentence+word+"/"+tarr[0].split("/")[1]+" ";
					}else{
        				for(int i2=0; i2<tarr.length; i2++){
        					if(tarr[i2].equals("")){
        						continue;
        						}  
        					sentence = sentence+tarr[i2]+" ";
        					}
					}
				}else{
					sentence = sentence+word+"/"+pos+" ";
				}
			}
			sentence_list.add(sentence);
		}
		
		return sentence_list;
	}
	
	public List<String> pre_process_comp(List<ArrayList<String>> rawlist){
		List<String> sentence_list = new ArrayList<String>();
		
		for(int i=0; i<rawlist.size(); i++){
			String sentence="";
			String line;
			String word;
			String pos;
			String[] strarr;
			String[] posarr;
			
			for(int j=0; j<rawlist.get(i).size(); j++){
				line = rawlist.get(i).get(j);
				strarr = line.split("\t");
				word = strarr[0];
				posarr = strarr[1].split(",");
				pos = posarr[0];
				
				sentence = sentence+word+"/"+pos+" ";
				
			}
			sentence_list.add(trans_ECS(sentence));
		}
		
		return sentence_list;
	}

	private String trans_unicode(String temp) {
		String r = temp;

		r = r.replaceAll("\u11A8", "\u3131")
				.replaceAll("\u11A9", "\u3132")
				.replaceAll("\u11AA", "\u3133")
				.replaceAll("\u11AB", "\u3134")
				.replaceAll("\u11AC", "\u3135")
				.replaceAll("\u11AD", "\u3136")
				.replaceAll("\u11AE", "\u3137")
				.replaceAll("\u11AF", "\u3139")
				.replaceAll("\u11B0", "\u313A")
				.replaceAll("\u11B1", "\u313B")
				.replaceAll("\u11B2", "\u313C")
				.replaceAll("\u11B3", "\u313D")
				.replaceAll("\u11B4", "\u313E")
				.replaceAll("\u11B5", "\u313F")
				.replaceAll("\u11B6", "\u3140")
				.replaceAll("\u11B7", "\u3141")
				.replaceAll("\u11B8", "\u3142")
				.replaceAll("\u11B9", "\u3144")
				.replaceAll("\u11BA", "\u3145")
				.replaceAll("\u11BB", "\u3146")
				.replaceAll("\u11BC", "\u3147")
				.replaceAll("\u11BD", "\u3148")
				.replaceAll("\u11BE", "\u314A")
				.replaceAll("\u11BF", "\u314B")
				.replaceAll("\u11C0", "\u314C")
				.replaceAll("\u11C1", "\u314D")
				.replaceAll("\u11C2", "\u314E");

		r = r.replaceAll("\u1100", "\u3131")
				.replaceAll("\u1101", "\u3132")
				.replaceAll("\u1102", "\u3104")
				.replaceAll("\u1103", "\u3107")
				.replaceAll("\u1104", "\u3108")
				.replaceAll("\u1105", "\u3109")
				.replaceAll("\u1106", "\u3141")
				.replaceAll("\u1107", "\u3142")
				.replaceAll("\u1108", "\u3143")
				.replaceAll("\u1109", "\u3145")
				.replaceAll("\u110A", "\u3146")
				.replaceAll("\u110B", "\u3147")
				.replaceAll("\u110C", "\u3148")
				.replaceAll("\u110D", "\u3149")
				.replaceAll("\u110E", "\u314A")
				.replaceAll("\u110F", "\u314B")
				.replaceAll("\u1110", "\u314C")
				.replaceAll("\u1111", "\u314D")
				.replaceAll("\u1112", "\u314E");

		return r;
	}
	
	public static String phonetic_reduced(String str){
		String tmp = " "+str+" ";
		if(tmp.contains("/J") || tmp.contains("/E")){
			if(tmp.contains(" 은")){
				tmp = tmp.replaceAll("은", "은는");
			}else if(tmp.contains(" 는")){
				tmp = tmp.replaceAll("는", "은는");
			}else if(tmp.contains(" 이/")){
				tmp = tmp.replaceAll("이", "이가");
			}else if(tmp.contains(" 가/")){
				tmp = tmp.replaceAll("가", "이가");
			}else if(tmp.contains(" 을/")){
				tmp = tmp.replaceAll("을", "을를");
			}else if(tmp.contains(" 를/")){
				tmp = tmp.replaceAll("를", "을를");
			}else if(tmp.contains(" 로/")){
				tmp = tmp.replaceAll("로", "로으로");
			}else if(tmp.contains(" 으로/")){
				tmp = tmp.replaceAll("으로", "로으로");
			}else if(tmp.contains(" 나/")){
				tmp = tmp.replaceAll("나", "나이나");
			}else if(tmp.contains(" 이나/")){
				tmp = tmp.replaceAll("이나", "나이나");
			}else if(tmp.contains(" 랑/")){
				tmp = tmp.replaceAll("랑", "랑이랑");
			}else if(tmp.contains(" 이랑/")){
				tmp = tmp.replaceAll("이랑", "랑이랑");
			}else if(tmp.contains(" 와/")){
				tmp = tmp.replaceAll("와", "와과");
			}else if(tmp.contains(" 과/")){
				tmp = tmp.replaceAll("과", "와과");
			}else if(tmp.contains(" 어야/")){
				tmp = tmp.replaceAll("어야", "어야아야");
			}else if(tmp.contains(" 아야/")){
				tmp = tmp.replaceAll("아야", "어야아야");
			}else if(tmp.contains(" ㅓ야/")){
				tmp = tmp.replaceAll("ㅓ야", "어야아야");
			}else if(tmp.contains(" ㅏ야/")){
				tmp = tmp.replaceAll("ㅏ야", "어야아야");
			}else if(tmp.contains(" 야/")){
				tmp = tmp.replaceAll(" 야", "어야아야");
			}
			
			else if(tmp.contains(" ㄹ")){
				tmp = tmp.replaceAll(" ㄹ", "을를");
			/*}else if(tmp.contains(" ㄴ")){
				tmp = tmp.replaceAll(" ㄴ", "은는");*/
			}else if(tmp.contains("으란")){
				tmp = tmp.replaceAll("으란", "란으란");
			}else if(tmp.contains("란")){
				tmp = tmp.replaceAll("란", "란으란");
			}else if(tmp.contains("아")){
				tmp = tmp.replaceAll("아", "어아");
			}else if(tmp.contains("어")){
				tmp = tmp.replaceAll("어", "어아");
			}else if(tmp.contains("ㅏ")){
				tmp = tmp.replaceAll("ㅏ", "어아");
			}else if(tmp.contains("ㅓ")){
				tmp = tmp.replaceAll("ㅓ", "어아");
			}else if(tmp.contains("았었")){
				tmp = tmp.replaceAll("았었", "었았");
			}else if(tmp.contains("었았")){
				tmp = tmp.replaceAll("었았", "었았");
			}else if(tmp.contains("었")){
				tmp = tmp.replaceAll("었", "었았");
			}else if(tmp.contains("았")){
				tmp = tmp.replaceAll("았", "었았");
			}else if(tmp.contains("어서")){
				tmp = tmp.replaceAll("어서", "어아서");
			}else if(tmp.contains("아서")){
				tmp = tmp.replaceAll("아서", "어아서");
			}else if(tmp.contains(" 서")){
				tmp = tmp.replaceAll(" 서", "어아서");
			}else if(tmp.contains("으면서")){
				tmp = tmp.replaceAll("으면서", "면서으면셔");
			}else if(tmp.contains("면서")){
				tmp = tmp.replaceAll("면서", "면서으면셔");
			}else if(tmp.contains("으면")){
				tmp = tmp.replaceAll("으면", "면으면");
			}else if(tmp.contains("면")){
				tmp = tmp.replaceAll("면", "면으면");
			}else if(tmp.contains("으시")){
				tmp = tmp.replaceAll("으시", "시으시");
			}else if(tmp.contains("시")){
				tmp = tmp.replaceAll("시", "시으시");
			}else if(tmp.contains("으며")){
				tmp = tmp.replaceAll("으며", "며으며");
			}else if(tmp.contains("며")){
				tmp = tmp.replaceAll("며", "며으며");
			}else if(tmp.contains("/E") && tmp.contains("도")){
				tmp = tmp.replaceAll("도", "어아도");
			}else if(tmp.contains("으려던")){
				tmp = tmp.replaceAll("으려던", "려던으려던");
			}else if(tmp.contains("려던")){
				tmp = tmp.replaceAll("려던", "려던으려던");
			}else if(tmp.contains("으려는")){
				tmp = tmp.replaceAll("으려는", "려는으려는");
			}else if(tmp.contains("려는")){
				tmp = tmp.replaceAll("려는", "려는으려는");
			}else if(tmp.contains("으라는")){
				tmp = tmp.replaceAll("으라는","라는으라는");
			}else if(tmp.contains("라는")){
				tmp = tmp.replaceAll("라는", "라는으라는");
			}else if(tmp.contains("느냐고")){
				tmp = tmp.replaceAll("느냐고", "냐고느냐고");
			}else if(tmp.contains("냐고")){
				tmp = tmp.replaceAll("냐고", "냐고느냐고");
			}
			
			
			if(tmp.equals(" 다/ECS ")){
				tmp = tmp.replaceAll("다", "다라");
			}else if(tmp.equals(" 라/ECS ")){
				tmp = tmp.replaceAll("라", "다라");
			}else if(tmp.equals(" 다고/ECS ")){
				tmp = tmp.replaceAll("다", "다라");
			}else if(tmp.equals(" 라고/ECS ")){
				tmp = tmp.replaceAll("라", "다라");
			}else if(tmp.equals(" 은는다고/ECS ")){
				tmp = tmp.replaceAll("다", "다라");
			}else if(tmp.equals(" 은는라고/ECS ")){
				tmp = tmp.replaceAll("라", "다라");
			}
			

			return tmp.trim();			
		}else{
			return str;
		}
	}

}
