package com.clunix.NLP.morpheme_analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.clunix.NLP.data_create.pre_modif;

public class mecab_updater
{
	float mecab_time;
	morphemes_analyzer mecab = new morphemes_analyzer();
	List<String> sentence_set;
	String mecab_ko_dic_path;

	public void set_mecab_time()
	{
		mecab_time = 0;
	}

	public float get_mecab_time()
	{
		return mecab_time;
	}

	public void set_mecab_ko_dic_path(String str)
	{
		this.mecab_ko_dic_path = str;
	}

	public List<String> combi(String str)
	{
		List<String> combi = new ArrayList<String>();
		int winsize;
		for (int i = 0; i < str.length(); i++) {
			winsize = 1;
			for (int j = str.length(); (winsize - i - 1) < j && (winsize + i) < str.length() + 1; winsize++) {
				combi.add(str.substring(i, (i + winsize)));
			}
		}
		return combi;
	}

	public boolean new_words(String str)
	{
		for (int i = 0; i < sentence_set.size(); i++) {
			if (sentence_set.get(i).contains(str)) {
				return true;
			}
		}
		return false;
	}

	public List<String> is_NNG_COMBI(List<String> list) throws IOException, InterruptedException
	{
		List<String> in_nng = new ArrayList<String>();
		String tmp;
		for (int i = 0; i < list.size(); i++) {
			tmp = list.get(i);
			String comb = mecab.analyzer(tmp, 1).get(0);
			String[] arr = comb.split(" ");
			for (int j = 0; j < arr.length; j++) {
				if (arr[j].contains("NNG") || arr[j].contains("SL")) {
					in_nng.add(list.get(i));
					break;
				}
			}
		}
		return in_nng;
	}

	public List<String> update_dic(String sentence) throws IOException, InterruptedException
	{
		sentence = sentence.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\u3130-\u318E/]", " ").trim();
		String[] arr = sentence.split(" ");
		List<String> candidate = new ArrayList<String>();
		for (int a = 0; a < arr.length; a++) {
			List<String> combi = combi(arr[a]);
			Collections.sort(combi, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2)
				{
					// TODO Auto-generated method stub
					if (s1.length() > s2.length()) {
						return -1;
					}
					else if (s1.length() < s2.length()) {
						return 1;
					}
					else {
						return 0;
					}
				}
			});
			String word;
			boolean test = false;
			for (int i = 0; i < combi.size(); i++) {
				word = combi.get(i).trim();
				if (word.trim().equals("")) {
					continue;
				}
				if (test == true) {
					//System.out.println("[" + word + "] 사전에 등록되어 있는 단어");
					break;
				}
				else {
					if (new_words(word)) {
						//System.out.println("[" + word + "] sentence_set에 존재함");
						continue;
					}
				}
			}
			combi.clear();
		}
		List<String> unique_candidate = new ArrayList<String>(new HashSet<String>(candidate));
		for (int i = 0; i < unique_candidate.size(); i++) {
			for (int j = 0; j < unique_candidate.size(); j++) {
				if (i != j && unique_candidate.get(i).contains(unique_candidate.get(j))) {
					if (last_is_num(unique_candidate.get(i)) && last_is_num(unique_candidate.get(j))) {
						unique_candidate.set(j, " ");
					}
					else if (is_eng(unique_candidate.get(i)) && is_eng(unique_candidate.get(j))) {
						unique_candidate.set(j, " ");
					}
					else {
						unique_candidate.set(i, " ");
					}
					break;
				}
			}
		}
		List<String> result = new ArrayList<String>();
		for (int k = 0; k < unique_candidate.size(); k++) {
			if (!unique_candidate.get(k).trim().equals("") && is_kor_num(unique_candidate.get(k))) {
				result.add(unique_candidate.get(k).trim());
			}
		}
		return is_NNG_COMBI(result);
	}

	public boolean last_is_num(String str)
	{
		String isnum = str.substring(str.length() - 1);
		try {
			Double.parseDouble(isnum);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean is_eng(String str)
	{
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!((0x61 <= c && c <= 0x7A) || (0x41 <= c && c <= 0x5A))) {
				return false;
			}
		}
		return true;
	}

	public boolean is_kor_num(String str)
	{
		int kor = 0;
		int num = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (((0x61 <= c && c <= 0x7A) || (0x41 <= c && c <= 0x5A))) {}
			else if (0x30 <= c && c <= 0x39) {
				num = 1;
			}
			else {
				kor = 1;
			}
		}
		if (kor + num == 2) {
			return false;
		}
		else {
			return true;
		}
	}

	public void updater(List<String> listAddCandi, String korpath) throws IOException, InterruptedException
	{
		String tag = "";
		for (int icnt = 0; icnt < listAddCandi.size(); icnt++) {
			if (listAddCandi.get(icnt).contains("/NNG"))
				tag = "NNG";
			else
				tag = "SL";
			String l_tag = upper_lower(tag);
			BufferedWriter bw = new BufferedWriter(new FileWriter(korpath + l_tag + ".csv", true));
			pre_modif Modif = new pre_modif();
			String str = Modif.undo(listAddCandi.get(icnt));
			bw.write(str + ",,,," + tag + ",*,T," + str + ",*,*,*,*,*");
			bw.newLine();
			bw.close();
		}
		user_dic_compile();
	}

	public void user_dic_compile() throws IOException, InterruptedException
	{
		String command = "/usr/local/bin/mecab-ko-dic-2.0.1-20150920/tools/add-mydic.sh";
		Process process = new ProcessBuilder(command).start();
		process.waitFor();
		process = new ProcessBuilder("/usr/local/bin/mecab-ko-dic-2.0.1-20150920/make_all.sh").start();
		process.waitFor();
	}

	public String upper_lower(String str)
	{
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 65 && c <= 90) {
				result += String.valueOf(c).toLowerCase();
			}
			else if (c >= 97 && c <= 122) {
				result += String.valueOf(c).toUpperCase();
			}
			else {
				result += c;
			}
		}
		return result;
	}
}
