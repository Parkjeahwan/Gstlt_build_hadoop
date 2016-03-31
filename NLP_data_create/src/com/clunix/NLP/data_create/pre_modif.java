package com.clunix.NLP.data_create;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.clunix.NLP.Jnld;
import com.clunix.NLP.morpheme_analyzer.morphemes_analyzer;
import com.clunix.NLP.morpheme_analyzer.mecab_updater;

public class pre_modif
{
	// 전처리기 김정환
	private Jnld jnld = new Jnld();
	private morphemes_analyzer mecab = new morphemes_analyzer();
	private mecab_updater mu = new mecab_updater();
	private List<String> list_registered_words;
	private List<String> list_mecab = new ArrayList<String>();
	private Hashtable<String, Integer> mecab_dic = new Hashtable<String, Integer>();
	private List<String> list_NewWords;
	private int newWordsCnt;
	private int DIC_UPDATE_COUNT = 1;
	private List<String> EF;
	private List<String> EC;
	private float collect_time; // for debug..
	private boolean is_collect_mode; // for test..
	private boolean is_regist_mode; // for test..
	private String ko_path; // mecab dictionary path
	private String dic_data_path; // dictionary file /tmp/dic.data
	private String server_ip;
	private int server_port;
	private long cnt_registered_word;
/*	private final char[] CHO =
	 ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ 
	{ 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
			0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
	private final char[] JUN =
	 ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ 
	{ 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
			0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
			0x3163 };
	 X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ 
	private final char[] JON = { 0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
			0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
			0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };*/
	private List<String> SC = Arrays.asList("SF", "SC", "SE", "SSO", "SSC", "SC", "SY");

	public void set_modif_config() throws FileNotFoundException, InterruptedException
	{
		list_registered_words = new ArrayList<String>();
		list_NewWords = new ArrayList<String>();
		newWordsCnt = 0;
		DIC_UPDATE_COUNT = 1;
		collect_time = 0;
		cnt_registered_word = 0;
		is_collect_mode = true;
		is_regist_mode = true;
		server_ip = "192.168.12.76";
		server_port = 912;
		dic_data_path = "/tmp/dic.data";
		ko_path = "/usr/local/bin/mecab-ko-dic-1.6.1-20140814/"; // mecab dictionary path
		long start = System.currentTimeMillis();
		mu.set_mecab_ko_dic_path(ko_path);
		ko_path = "/usr/local/bin/mecab-ko-dic-1.6.1-20140814/user-dic/";
		long end = System.currentTimeMillis();
		float all_time = (float) ((end - start) / 1000.0);
		//System.out.println("data loading alltime : " + all_time);
	}

	public boolean my_search_PATTERN(String str)
	{
		long start = System.currentTimeMillis();
		int check = jnld.NGSWinsizeSearchPattern(str, server_ip, server_port);
		long check2 = System.currentTimeMillis();
		float all_time = (float) ((check2 - start) / 1000.0);
		//System.out.println("input: " + str);
		//System.out.println("check for my_search_pattern: " + all_time);
		if (check > 0) {
			return true;
		}
		return false;
	}

	public boolean my_search_PATTERN2(String str)
	{
		long start = System.currentTimeMillis();
		//System.out.println("input: " + str);
		int check = jnld.NGSWinsizeSearch(str, server_ip, server_port);
		long check2 = System.currentTimeMillis();
		float all_time = (float) ((check2 - start) / 1000.0);
		//System.out.println("output : " + check);
		//System.out.println("check for my_search_pattern2: " + all_time);
		if (check > 0) {
			return true;
		}
		return false;
	}

	public void list_pattern_update(String key)
	{
		long start = System.currentTimeMillis();
		jnld.NGSWinsizeRegist(key, "word_winsize_total_nospace.txt", server_ip, server_port);
		long check2 = System.currentTimeMillis();
		float all_time = (float) ((check2 - start) / 1000.0);
		//System.out.println("word_winsize update : " + key + " " + all_time);
	}

	public void set_mecab_dic() throws IOException, InterruptedException
	{
		// String cmd =
		// "/usr/local/bin/mecab-ko-dic-2.0.1-20150707/./make_alldic.sh";
		String cmd = "/usr/local/bin/mecab-ko-dic-1.6.1-20140814/./make_alldic.sh";
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		BufferedReader br = new BufferedReader(new FileReader(dic_data_path));
		String line;
		while ((line = br.readLine()) != null) {
			mecab_dic.put(line.trim(), 1);
		}
		br.close();
	}

	public boolean my_search_DIC(String str)
	{
		//System.out.println("my_search_DIC : " + str);
		int check = jnld.NGSMecabSearch(str, server_ip, server_port);
		if (check > 0)
			return true;
		return false;
	}

	public long get_registered_word_count()
	{
		return cnt_registered_word;
	}

	public List<String> get_registered_words()
	{
		return list_registered_words;
	}

	public void set_regist_mode(boolean is_regist)
	{
		this.is_regist_mode = is_regist;
	}

	public boolean set_mecab_dic(String dic_path)
	{
		mu.set_mecab_ko_dic_path(dic_path);
		//System.out.println("set mecab dictionary failed IOException");
		//System.out.println("set mecab dictionary failed InterruptedException");
		return true;
	}

	public void set_collect_mode(boolean is_collect)
	{
		this.is_collect_mode = is_collect;
	}

	private String remove_symbol(String str)
	{
		String match_symbol = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		str = str.replaceAll(match_symbol, " ");
		String match_space = "\\s{2,}";
		str = str.replaceAll(match_space, " ");
		str = str.trim();
		return str;
	}

	private String remove_spaceAll(String str)
	{
		str.replaceAll("\\s", " ");
		return str;
	}

	private void remove_IC(List<String> listCandi)
	{
		for (int icnt = 0; icnt < listCandi.size(); icnt++) {
			if (listCandi.get(icnt).contains("/IC")) {
				listCandi.remove(icnt);
				icnt--;
			}
		}
	}

	private final char[] number_char = { '일', '이', '삼', '사', '오', '육', '칠', '팔', '구', '십', '백', '천', '만', '억', '조', '경', '해' };
	private final String[] number_str = { "킬로", "메가", "기가", "테라", "피코", "나노", "마이크로", "밀리", "센티", "데시" };
	private final String[] J_str = { "이", "가", "와", "과", "의", "은", "는", "을", "를", "에", "도" };
	private final String[] J_str2 = { "으로", "도록", "에서" };

	private String is_J_string(String str)
	{
		if (str.length() > 0) {
			String lastchar = str.charAt(str.length() - 1) + "";
			for (int icnt = 0; icnt < J_str.length; icnt++) {
				if (lastchar.equals(J_str[icnt])) {
					return str.substring(0, str.length() - 1);
				}
			}
			if (str.length() <= 1)
				return null;
			String laststr = str.charAt(str.length() - 2) + "" + str.charAt(str.length() - 1) + "";
			for (int icnt = 0; icnt < J_str2.length; icnt++) {
				if (laststr.equals(J_str2[icnt])) {
					return str.substring(0, str.length() - 2);
				}
			}
			lastchar = str.charAt(str.length() - 1) + "";
			if (lastchar.equals("로"))
				return str.substring(0, str.length() - 1);
		}
		return null;
	}

	private boolean is_number_string(String str)
	{
		char lastchar;
		String laststr;
		if (str.length() > 0) {
			lastchar = str.charAt(str.length() - 1);
			for (int icnt = 0; icnt < number_char.length; icnt++) {
				if (lastchar == number_char[icnt])
					return true;
			}
		}
		if (str.length() > 1) {
			laststr = str.charAt(str.length() - 2) + "" + str.charAt(str.length() - 1) + "";
			for (int icnt = 0; icnt < number_str.length; icnt++) {
				if (laststr.equals(number_str[icnt]))
					return true;
			}
		}
		return false;
	}

	private void remove_NNBC(List<String> listCandi)
	{
		for (int icnt = 0; icnt < listCandi.size(); icnt++) {
			String[] arr = listCandi.get(icnt).split(" ");
			for (int jcnt = icnt + 1; jcnt < arr.length; jcnt++) {
				if (arr[jcnt].contains("/SL")) {
					if (arr[jcnt - 1].contains("/NR")) {
						listCandi.remove(icnt);
						icnt--;
						break;
					}
					else if (is_number_string(undo(arr[jcnt - 1]))) {
						listCandi.remove(icnt);
						icnt--;
						break;
					}
				}
			}
		}
		if (listCandi.isEmpty())
			return;
		for (int icnt = 0; icnt < listCandi.size(); icnt++) {
			String[] arr = listCandi.get(icnt).split(" ");
			for (int jcnt = icnt; jcnt < arr.length; jcnt++) {
				if (arr[jcnt].contains("/SN")) {
					if (arr.length - 1 <= jcnt)
						continue;
					if (arr[jcnt + 1].contains("/NR")) {
						listCandi.remove(icnt);
						icnt--;
						break;
					}
					else if (is_number_string(undo(arr[jcnt + 1]))) {
						listCandi.remove(icnt);
						icnt--;
						break;
					}
				}
			}
		}
	}

	public void register_undefined(String original_str) throws IOException, InterruptedException
	{
		int candidate_number = 5;
		original_str = remove_emphasis_symbol(original_str);
		original_str = remove_symbol(original_str);
		List<String> original_words = set_original_list(original_str);
		List<ArrayList<String>> listAllCandi = candidate_analyze(original_str, candidate_number);
		for (int icnt = 0; icnt < listAllCandi.size(); icnt++)
			list_mecab.addAll(listAllCandi.get(icnt));
		//System.out.println("list_mecab : " + list_mecab);
		for (int i = 0; i < original_words.size(); i++) {
			// if(!my_search_PATTERN(original_words.get(i))){
			if (!my_search_PATTERN2(original_words.get(i))) {
				boolean is_registered = false;
				collect_time = 0;
				List<String> listCandi = new ArrayList<String>();
				List<String> listAddCandi = new ArrayList<String>();
				List<String> listCorpus = new ArrayList<String>();
				List<String> listOriginCorpus = new ArrayList<String>();
				//System.out.println("in_mecab " + original_words.get(i));
				long start = System.currentTimeMillis();
				for (int k = 0; k < listAllCandi.size(); k++) {
					if (!listAllCandi.get(k).isEmpty()) {
						if (listAllCandi.get(k).size() > i) {
							listCandi.add(listAllCandi.get(k).get(i));
						}
					}
					continue;
				}
				remove_IC(listCandi);
				remove_NNBC(listCandi);
				if (listCandi.isEmpty()) {
					//System.out.println("Candidate Empty.. Next Words Go");
					continue;
				}
				//System.out.println("Candidate Morpheme: " + listCandi);
				for (int idx = 0; idx < listCandi.size(); idx++) {
					String firstmor = first_morpheme(listCandi.get(idx));
					if (firstmor.contains("/MAG")) {
						if (firstmor.length() > 2) {
							//System.out.println("firstmor : " + firstmor);
							continue;
						}
					}
					String lastmor = last_morpheme(listCandi.get(idx));
					if (lastmor.contains("/VX") || lastmor.contains("/MAG")) {
						//System.out.println("lastmor : " + lastmor);
						continue;
					}
					if (is_all_UNKNOWN(listCandi.get(idx)))
						continue;
					if (make_candidate_prev_pattern4(listCandi.get(idx), listAddCandi))
						break;
					if (listCandi.get(idx).split(" ").length > 3 &&
							(firstmor.contains("/NNG") || firstmor.contains("/NNP")) &&
							(lastmor.contains("/NNG") || lastmor.contains("/NNP")) ||
							listCandi.get(idx).contains("/UNKNOWN"))
						listAddCandi.add(original_words.get(i) + "/NNG");
					else if (make_candidate_prev_pattern(listCandi.get(idx), listAddCandi)) ;
					else if (make_candidate_prev_pattern2(listCandi.get(idx), listAddCandi)) ;
					else {
						String strCandi = original_words.get(i);
						String match_symbol = "[^\uAC00-\uD7A3xfea-zA-Z\\s]";
						strCandi = strCandi.replaceAll(match_symbol, "");
						String res2 = null;
						if ((res2 = is_J_string(strCandi)) != null) { // add...........
							listAddCandi.add(res2 + "/NNG"); // 특수관계사 제외 한 어절
						}
						else {
							if (!is_all_english(strCandi))
								listAddCandi.add(strCandi + "/NNG");
							else
								listAddCandi.add(strCandi + "/SL");
						}
					}
				}
				remove_equal_candi(listAddCandi);
				for (int icnt = 0; icnt < listAddCandi.size(); icnt++) {
					if (!(original_words.get(i).contains(undo(listAddCandi.get(icnt))))) {
						listAddCandi.remove(icnt);
						icnt--;
					}
				}
				Collections.sort(listAddCandi, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2)
					{
						return o2.length() - o1.length();
					}
				});
				remove_dic_candi(listAddCandi);
				if (listAddCandi.isEmpty()) {
					//System.out.println("Candidate Empty.. Next Words Go");
					continue;
				}
				//System.out.println("Candidate Final	: " + listAddCandi);
				collect_time = make_noduplicated_corpus(original_words.get(i), listAddCandi, listCorpus, listOriginCorpus);
				if (!is_empty_candidate(listAddCandi)) {
					if (is_updated_prev_pattern(listOriginCorpus, listAddCandi, original_words.get(i))) {
						is_registered = true;
						cnt_registered_word++;
					}
				}
				// if (newWordsCnt >= DIC_UPDATE_COUNT && is_regist_mode) {
				// modif_update();
				// }
				/*
				 * else { System.out.println("else phase"); List<String>
				 * listComb = make_available_combination(original_words.get(i));
				 * listAddCandi = make_candidate_combination_pattern(listComb,
				 * listOriginCorpus, original_words.get(i));
				 * remove_duplicate2(listAddCandi);
				 * update_combination_pattern(listAddCandi, listCorpus); }
				 */
				// if (is_registered && is_regist_mode)
				float corpus_time = 0;
				if (is_registered) {
					corpus_time = add_registered_word(listOriginCorpus);
				}
				listCandi.clear();
				// if (is_registered) {
				long end = System.currentTimeMillis();
				float all_time = (float) ((end - start) / 1000.0);
				String time = String.format("%.3f", (all_time - collect_time - corpus_time));
				//System.out.println(original_words.get(i) + " all_time : " + all_time + " : collect_time(" + collect_time + " ) corpus_time(" + corpus_time + ") + local_time(" + time + ")");
				// }
			}
			else {
				//System.out.println(original_words.get(i).trim() + " is already existed in word list");
			}
		}
		if (newWordsCnt >= DIC_UPDATE_COUNT && is_regist_mode) {
			modif_update();
		}
	}

	public void modif_update() throws IOException, InterruptedException
	{
		String new_words = "";
		for (int icnt = 0; icnt < list_NewWords.size(); icnt++) {
			new_words += list_NewWords.get(icnt) + " ";
		}
		new_words = new_words.trim();
		//System.out.println("modif update : " + new_words + "/end..");
		int check = 0;
		if (!new_words.isEmpty()) {
			check = jnld.NGSMecabRegist(new_words, server_ip, server_port);
		}
		//System.out.println("modif_update : " + check);
		// mu.updater(list_NewWords, ko_path);
		// BufferedWriter bw_word_winsize = new BufferedWriter(new
		// FileWriter(word_winsize_total_nospace_path, true));
		/*
		 * for (int kcnt = 0; kcnt < list_NewWords.size(); kcnt++) {
		 * //bw_word_winsize.write(undo(list_NewWords.get(kcnt)) + "\t" + 1);
		 * //bw_word_winsize.newLine(); }
		 * 
		 * //bw_word_winsize.close();
		 */
		for (int kcnt = 0; kcnt < list_NewWords.size(); kcnt++) {
			list_NewWords.remove(kcnt);
			kcnt--;
		}
	}

	private float add_registered_word(List<String> listOriginCorpus) throws IOException, InterruptedException
	{

		long start = System.currentTimeMillis();
		for (int icnt = 0; icnt < listOriginCorpus.size(); icnt++) {
			String strOrigin = listOriginCorpus.get(icnt);
			if (!is_existed_original_corpus(strOrigin)) {
				jnld.NGSFileRegist(strOrigin, "corpus_1000_article.txt", server_ip, server_port);
				List<String> corpus_no_space = candidate_analyze(strOrigin, 1).get(0);
				String res = "";
				for (int jcnt = 0; jcnt < corpus_no_space.size(); jcnt++) {
					res += corpus_no_space.get(jcnt);
					if (jcnt != corpus_no_space.size() - 1)
						res += " ";
				}
				jnld.NGSFileRegist(res, "corpus_article_pr_last.txt", server_ip, server_port);
				jnld.NGSFileRegist(res, "corpus_article_space_pr_last.txt", server_ip, server_port);
			}
		}
		long end = System.currentTimeMillis();
		float all_time = (float) ((end - start) / 1000.0);
		//System.out.println("add_register word : " + all_time);
		return (float) ((end - start) / 1000.0);
	}

	private boolean is_existed_original_corpus(String strOriginCorpus)
	{
		long start = System.currentTimeMillis();
		int check = jnld.NGSCorpusSearch(strOriginCorpus, server_ip, server_port);
		long check2 = System.currentTimeMillis();
		float all_time = (float) ((check2 - start) / 1000.0);

		//System.out.println("check for is_existed_original_corpus: " + check + " / " + all_time);
		if (check > 0)
			return true;
		return false;
	}

	private void remove_equal_candi(List<String> listAddCandi)
	{
		for (int co = 0; co < listAddCandi.size(); co++) {
			String key = listAddCandi.get(co);
			for (int co2 = 0; co2 < listAddCandi.size(); co2++) {
				if (co == co2) {
					continue;
				}
				if (listAddCandi.get(co2).equals(key) || listAddCandi.get(co2).equals("/NNG")) {
					listAddCandi.set(co2, "");
				}
			}
		}
		for (int icnt = 0; icnt < listAddCandi.size(); icnt++) {
			if (listAddCandi.get(icnt).equals("")) {
				listAddCandi.remove(icnt);
				icnt--;
			}
		}
	}

	private void remove_dic_candi(List<String> listAddCandi) throws IOException, InterruptedException
	{
		for (int icnt = 0; icnt < listAddCandi.size(); icnt++) {
			if (my_search_DIC(undo(listAddCandi.get(icnt))) || my_search_PATTERN(undo(listAddCandi.get(icnt)))) {
				//System.out.println(undo(listAddCandi.get(icnt)) + " is already existed dictionary or wordlist");
				listAddCandi.remove(icnt);
				icnt--;
			}
		}
	}

	private String MP_to_Str(String[] arr)
	{
		String res = "";
		for (int icnt = 0; icnt < arr.length; icnt++) {
			res = res + arr[icnt] + " ";
		}
		return res;
	}

	private char[] pair_symbol = { '[', ']', '{', '}', '<', '>', '(', ')', '\'', '"' };

	private boolean is_pair_symbol(char symbol)
	{
		for (int jcnt = 0; jcnt < pair_symbol.length; jcnt++) {
			if (symbol == pair_symbol[jcnt])
				return true;
		}
		return false;
	}

	private String remove_all_pair_symbol(String sentence, char symbol)
	{
		int firstcnt = 0, lastcnt = 0, samecnt = 0;
		String res = sentence;
		char firstpair = symbol;
		char lastpair;
		if (firstpair == '[')
			lastpair = ']';
		else if (firstpair == '{')
			lastpair = '}';
		else if (firstpair == '(')
			lastpair = ')';
		else if (firstpair == '<')
			lastpair = '>';
		else if (firstpair == '\'')
			lastpair = firstpair;
		else if (firstpair == '"')
			lastpair = firstpair;
		else
			return res;
		for (int icnt = 0; icnt < sentence.length(); icnt++) {
			char compare_char = sentence.charAt(icnt);
			if (lastpair != firstpair) {
				if (lastpair == compare_char)
					lastcnt++;
				if (firstpair == compare_char)
					firstcnt++;
			}
			else if (firstpair == compare_char)
				samecnt++;
		}
		if (lastpair != firstpair && lastcnt != firstcnt) {
			res = res.replaceAll("[\\" + firstpair + "]", " ");
			res = res.replaceAll("[\\" + lastpair + "]", " ");
		}
		else if (lastpair == firstpair && samecnt % 2 != 0) {
			res = res.replaceAll("[\\" + firstpair + "]", " ");
		}
		return res;
	}

	private String remove_pair_symbol(String sentence)
	{
		String res = sentence;
		for (int icnt = 0; icnt < sentence.length(); icnt++) {
			char symbol = sentence.charAt(icnt);
			if (is_pair_symbol(symbol)) {
				res = remove_all_pair_symbol(res, symbol);
			}
		}
		return res.trim();
	}

	private boolean is_has_symbol(String str)
	{
		if (!str.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝| |]*"))
			return true;
		return false;
	}

	private int get_symbol_cnt(String str, String symbol)
	{
		int cnt = 0;
		for (int icnt = 0; icnt < str.length(); icnt++) {
			String strchar = str.charAt(icnt) + "";
			if (strchar.equals(symbol)) {
				cnt++;
			}
		}
		return cnt;
	}

	private String remove_first_symbol(String sentence)
	{
		String res = "";
		String[] arrWord = sentence.split(" ");
		for (int icnt = 0; icnt < arrWord.length; icnt++) {
			String word = arrWord[icnt];
			if (word.length() <= 1) {
				res = res + word + " ";
				continue;
			}
			if (is_pair_symbol(word.charAt(0))) {
				res = sentence;
				break;
			}
			String first_str = word.charAt(0) + "";
			if (is_has_symbol(first_str))
				res = res + word.substring(1, word.length()) + " ";
			else
				res = res + word + " ";
		}
		return res;
	}

	private String remove_last_symbol(String sentence)
	{
		String res = "";
		String[] arrWord = sentence.split(" ");
		for (int icnt = 0; icnt < arrWord.length; icnt++) {
			String word = arrWord[icnt];
			if (word.length() <= 1) {
				res = res + word + " ";
				continue;
			}
			if (is_pair_symbol(word.charAt(word.length() - 1))) {
				res = sentence;
				break;
			}
			String last_str = word.charAt(word.length() - 1) + "";
			if (last_str.contains(".") || last_str.contains(",") || last_str.contains("!") || last_str.contains("?")) {
				res = res + word;
				continue;
			}
			if (is_has_symbol(last_str))
				res = res + word.substring(0, word.length() - 1) + " ";
			else
				res = res + word + " ";
		}
		return res;
	}

	private String remove_same_symbol(String word)
	{
		String res = word;
		for (int icnt = 0; icnt < word.length(); icnt++) {
			String symbol = word.charAt(icnt) + "";
			if (!is_has_symbol(symbol))
				continue;
			if (is_pair_symbol(word.charAt(icnt)))
				continue;
			int symbol_equal_cnt = get_symbol_cnt(word, symbol);
			if (symbol_equal_cnt >= 2) {
				res = res.replaceAll("[\\" + symbol + "]", "");
			}
		}
		return res;
	}

	public String remove_emphasis_symbol(String sentence)
	{
		if (!is_has_symbol(sentence))
			return sentence;
		String res_sentence = remove_pair_symbol(sentence);
		// System.out.println("remove_pari_symbol res : " + res_sentence);
		res_sentence = remove_first_symbol(res_sentence);
		// System.out.println("remove_first_symbol res : " + res_sentence);
		res_sentence = remove_last_symbol(res_sentence);
		// System.out.println("remove_last_symbol res : " + res_sentence);
		String[] arrWord = res_sentence.split(" ");
		List<String> listWord = new ArrayList<String>();
		for (int icnt = 0; icnt < arrWord.length; icnt++) {
			if (!arrWord[icnt].isEmpty())
				listWord.add(remove_same_symbol(arrWord[icnt]));
		}
		String removed_sentence = "";
		for (int icnt = 0; icnt < listWord.size(); icnt++)
			removed_sentence = removed_sentence + listWord.get(icnt).trim() + " ";
		// System.out.println("return : " + removed_sentence);
		return removed_sentence;
	}

	public String revise_parsing(String strMP, String str) throws IOException, InterruptedException
	{
		// S에 NNG에 인접해 한 /MM이 후속 출현하면
		/*
		 * if(s.contains("/NNG") && s.contains("한/MM")){ // s에 NNG에 인접해 한/MM이 후속
		 * 출현하면 System.out.println("1 phase.."); s = replace_HAN_MM(s); }
		 */
		// S에 NNG에 인접해 한 /MM이 후속 출현하면 ;
		String s = strMP;
		String[] arrMP = s.split(" ");
		for (int icnt = 0; icnt < arrMP.length; icnt++) {
			if (arrMP[icnt].contains("/ECS") && arrMP[icnt + 1].contains("한/MM")) {
				arrMP[icnt + 1].replaceAll("한/MM", "하/XSV 은는/ETM");
			}
		}
		s = MP_to_Str(arrMP);
		/*
		 * // 문장 끝 자음소(받침)을 제거하거나, 받침을 저거한 그 어절이 어떤 종결어미EF와 모음 하나만 다르게 끝날때
		 * String[] larr = s.split(" "); String last = larr[larr.length-1];
		 * String[] larr2 = last.split("/"); String lastmor = larr2[0];
		 * List<String> syllable = devide(lastmor); //List<String> syllable;
		 * 
		 * PhonemeList ph = new PhonemeList(); ph.split_PN("종"); //syllable =
		 * ph.get_ListPNs(); //if (ph.split_PN(lastmor)) if (ph.split_PN("종"))
		 * System.out.println("phonemeList : " + ph.toString() +
		 * ph.get_ListPNs() + syllable); else System.out.println(
		 * "phonemeList failed");
		 * 
		 * for(int i=0; i<syllable.size(); i++){
		 * System.out.println(syllable.get(i)); }
		 * 
		 * if(syllable.size()-4==0 || (syllable.size()-5 >-1 &&
		 * syllable.get(syllable.size()-5).equals("\\|#\\|"))){
		 * System.out.println("2 phase.."); String a =
		 * syllable.get(syllable.size()-4).split("\\|#\\|0x")[1]; String b =
		 * syllable.get(syllable.size()-3).split("\\|#\\|0x")[1];
		 * 
		 * System.out.println("list : " + a + " " + b);
		 * 
		 * long av = Long.parseLong(a, 16); long bv = Long.parseLong(b, 16);
		 * char temp = (char)(0xAC00 + 28 * 21 *(av) + 28 * (bv) ); String
		 * relast = String.valueOf(temp);
		 * 
		 * System.out.println("relast : " + relast);
		 * 
		 * s=""; for(int l=0; l<larr.length-1; l++){ s = s+larr[l]+" "; }
		 * 
		 * if(EF.contains(relast+"/EF")){ s = s+relast+"/EF"; }else
		 * if(EC.contains(relast+"/EC")){ s = s+relast+"/EC"; }else{
		 * s=s+larr[larr.length-1]; } }
		 * 
		 * // ETM 바로 다음에 VV가 오면 if(s.contains("/VV") && s.contains("/ETM")){
		 * System.out.println("3 phase.."); String[] arr = s.split(" "); int
		 * toggle=0; for(int i=0; i<arr.length; i++){
		 * if(type(arr[i]).equals("VV")){ for(int j=i-1; j>=0; j--){
		 * if(arr[j].equals("###/SPACE")){ continue; }
		 * 
		 * toggle=0; if(type(arr[j]).equals("ETM")){ for(int k=i+1;k<arr.length;
		 * k++){ if(type(arr[k]).equals("ETN")){ toggle =1; break; } }
		 * 
		 * if(toggle==0){ List<ArrayList<String>> candi =
		 * candidate_analyze(str,5);
		 * 
		 * int k3=0; for(; k3<candi.get(0).size(); k3++){
		 * if(candi.get(0).get(k3).contains("ETM")){ break; } }
		 * 
		 * for(int k2 = 1; k2<candi.size(); k2++){
		 * if(type(last_morpheme(candi.get(k2).get(k3))).equals("NNG")){
		 * candi.get(0).set(k3, candi.get(k2).get(k3)); s=""; for(int k4=0;
		 * k4<candi.get(0).size()-1; k4++){ s = s+candi.get(0).get(k4)+
		 * " ###/SPACE "; } s = s+candi.get(0).get(candi.get(0).size()-1);
		 * s.trim(); break; } } } toggle =1; } if(toggle==1){ break; } } }
		 * if(toggle ==1){ break; } } }
		 * 
		 * // 문장s의 처음부터 보아, s에 포함된 길이 5인 형태소 연속체가 말뭉치 상에 출현한 적이 없을 경우 String[]
		 * sarr = s.split(" "); for(int k1=0; k1<sarr.length; k1++){ String tmp
		 * = ""; int num=0; for(int k2=k1; num<5&& k2<sarr.length; k2++){
		 * if(sarr[k2].equals("###/SPACE")){ tmp = tmp+"###/SPACE "; continue; }
		 * 
		 * tmp = tmp+sarr[k2]+" "; num++; } String tmp2 = tmp.replaceAll(
		 * " ###/SPACE ", " "); if(!search_corpus(tmp2,1)){ System.out.println(
		 * "4 phase.."); List<ArrayList<String>> candi =
		 * candidate_analyze(str,5); String rp = ""; for(int i=1;
		 * i<candi.size(); i++){ String s2 = ""; for(int j=0;
		 * j<candi.get(i).size()-1; j++){ s2 = s2+ candi.get(i).get(j)+
		 * " ###/SPACE "; } s2 = s2+ candi.get(i).get(candi.get(i).size()-1); s2
		 * = s2.trim();
		 * 
		 * String[] s2arr = s2.split(" "); String tmp21 = ""; int num21=0;
		 * for(int k22=k1; num<5 && k22<s2arr.length; k22++){
		 * if(s2arr[k22].equals("###/SPACE")){ tmp21 = tmp21+"###/SPACE ";
		 * continue; } tmp21 = tmp21+s2arr[k22]+" "; num++; } String tmp22 =
		 * tmp21.replaceAll(" ###/SPACE ", " "); if(search_corpus(tmp22,1)){ rp
		 * = tmp21; break; } } if(!rp.equals("")){ s = s.replaceAll(tmp, rp); }
		 * } }
		 * 
		 * // ???/NNG-하/XSV-게/EC 패턴이 출현하면 String s2 = s.replaceAll(" ###/SPACE "
		 * , " "); if(s2.contains("/NNG 하/XSV 게/EC")){ System.out.println(
		 * "5 phase.."); String[] arr = s.split("하/XSV 게/EC"); String[] arr2 =
		 * arr[0].trim().split(" ");
		 * if(type(arr2[arr2.length-1]).equals("NNG")){ if(!FOC("을를/JKO "
		 * +arr2[arr2.length-1])){ s = s.replaceAll(arr2[arr2.length-1]+
		 * " 하/XSV 게/EC", arr2[arr2.length-1]+" 하/XSA 게/JKB"); } }else
		 * if(arr2[arr2.length-1].equals("###/SPACE")){ if(!FOC("을를/JKO "
		 * +arr2[arr2.length-1])){ s = s.replaceAll(arr2[arr2.length-2]+
		 * " ###/SPACE 하/XSV 게/EC", arr2[arr2.length-2]+" ###/SPACE 하/XSA 게/JKB"
		 * ); } } }
		 * 
		 * 
		 * // ???/NNG-하/XSV 패턴이 출현하면 s2 = s.replaceAll(" ###/SPACE ", " ");
		 * if(s2.contains("/NNG 하/XSV")){ System.out.println("6 phase..");
		 * String[] arr = s.split(" "); for(int i=0; i<arr.length; i++){
		 * if(arr[i].contains("하/XSV")){ if(arr[i].equals("###/SPACE")){
		 * continue; } if(type(arr[i-1]).equals("NNG")){ // if() } break; } } }
		 */
		return s;
	}

	public List<ArrayList<String>> candidate_analyze(String str, int cnt) throws IOException, InterruptedException
	{
		List<ArrayList<String>> list2 = new ArrayList<ArrayList<String>>();
		str = str.trim();
		List<String> list = mecab.analyzer_space(str, cnt);
		for (int i = 0; i < list.size(); i++) {
			String rm_str = mecab.trans_ECS(list.get(i));
			rm_str = mecab.trans_JKB(rm_str);
			rm_str = mecab.trans_vxecv(rm_str);
			// revise parsing...
			List<String> mor = new ArrayList<String>();
			String[] arr = rm_str.split(" ");
			String tmp_str = "";
			for (int j = 0; j < arr.length; j++) {
				if (arr[j].equals("###/SPACE")) {
					mor.add(tmp_str.trim());
					tmp_str = "";
				}
				else {
					tmp_str = tmp_str + morphemes_analyzer.phonetic_reduced(arr[j]) + " ";
				}
			}
			if (!tmp_str.trim().equals("")) {
				mor.add(tmp_str.trim());
			}
			list2.add(new ArrayList<String>());
			list2.get(list2.size() - 1).addAll(mor);
			mor.clear();
		}
		return list2;
	}

	private String type(String str)
	{
		String[] arr = str.split("/");
		return arr[arr.length - 1];
	}

	private String first_morpheme(String str)
	{
		String[] arr = str.split(" ");
		for (int i = 0; i < arr.length - 1; i++) {
			if (SC.contains(type(arr[i]))) {
				continue;
			}
			return arr[i];
		}
		return "";
	}

	private String last_morpheme(String str)
	{
		String[] arr = str.split(" ");
		for (int i = arr.length - 1; i >= 0; i--) {
			if (SC.contains(type(arr[i]))) {
				continue;
			}
			return arr[i];
		}
		return "";
	}

	private List<String> set_original_list(String originalal_str)
	{
		List<String> s = new ArrayList<String>();
		originalal_str = originalal_str.trim();
		String[] arr = originalal_str.split(" ");
		for (int i = 0; i < arr.length; i++)
			s.add(arr[i]);
		return s;
	}

	private float make_noduplicated_corpus(String word, List<String> listAddCandi, List<String> listCorpus, List<String> listOriginCorpus)
			throws InterruptedException, IOException
	{
		String prev_x = "";
		float process_time = 0;
		for (int j4 = 0; j4 < listAddCandi.size(); j4++) {
			if (prev_x.equals(undo(listAddCandi.get(j4)))) {
				prev_x = undo(listAddCandi.get(j4));
				continue;
			}
			if (word.contains(undo(listAddCandi.get(j4)))) {
				process_time = make_corpus(listCorpus, listOriginCorpus, undo(listAddCandi.get(j4)));
				prev_x = undo(listAddCandi.get(j4));
			}
		}
		return process_time;
	}

	private float make_corpus(List<String> listCorpus, List<String> listOriginCorpus, String word) throws InterruptedException, IOException
	{
		long start = System.currentTimeMillis();
		List<String> search_res = new ArrayList<String>();
		if (is_collect_mode) {
			// search_res = collector.google_search(word);
		}
		// @SuppressWarnings("resource")
		// BufferedReader br = new BufferedReader(new FileReader(collect_path));
		String line;
		for (int icnt = 0; icnt < search_res.size(); icnt++) {
			line = search_res.get(icnt);
			line = remove_emphasis_symbol(line);
			//System.out.println("line : " + line);
			// String result = mecab.analyzer_space(remove_symbol(line).trim(),
			// 1).get(0);
			String result = mecab.analyzer_space(line.trim(), 1).get(0);
			// String result =
			// mecab.analyzer_space(line.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]",
			// "").trim(), 1).get(0);
			result = mecab.trans_ECS(result);
			result = mecab.trans_JKB(result);
			result = mecab.trans_vxecv(result);
			listCorpus.add(result);
			listOriginCorpus.add(line);
			// listOriginCorpus.add(remove_symbol(line));
		}
		// br.close();
		long end = System.currentTimeMillis();
		return (float) ((end - start) / 1000.0);
	}

	private boolean is_verb_morpheme(String strCandi)
	{
		// 하/XSV, 하/XSA, 이/VCP, 아니/VCN , 되/XSV, 되/XSA
		if (strCandi.contains("하/XSV") || strCandi.contains("하/XSA") || strCandi.contains("하/XSV") || strCandi.contains("이/VCP") ||
				strCandi.contains("아니/VCN") || strCandi.contains("되/XSV") || strCandi.contains("되/XSA") ||
				type((strCandi)).equals("NNB") || strCandi.contains("화/XSN") || strCandi.contains("들/XSN") ||
				strCandi.contains("XSV") || strCandi.contains("XSN") || strCandi.contains("XSA"))
			// strCandi.contains("/NNB") || strCandi.contains("NNBC"))
			return true;
		/*
		 * if (strCandi.contains("/VV") || strCandi.contains("/VA") ||
		 * strCandi.contains("/VX") || strCandi.contains("/XSA") ||
		 * strCandi.contains("/VCP") || strCandi.contains("/VCN") ||
		 * strCandi.contains("/XSN") || strCandi.contains("/XSA") ||
		 * strCandi.contains("/MM") || strCandi.contains("/XPN") ||
		 * strCandi.contains("/XSV")) //strCandi.contains("/NNB"))
		 * //strCandi.contains("/NNB") || strCandi.contains("NNBC")) return
		 * true;
		 */
		return false;
	}

	private boolean is_MM_NNP_morpheme(String strCandi)
	{
		if (strCandi.contains("MM"))
			return true;
		return false;
	}

	// 이가/JKS, 와과/JKC, 의/JKG, 은는/JX, 을를/JKO, 로으로/JKB, 에/JKB
	private boolean is_J_morpheme(String strCandi)
	{
		/*
		 * if (strCandi.contains("이가/JKS") || strCandi.contains("와과/JKC") ||
		 * strCandi.contains("의/JKG") || strCandi.contains("은는/JX") ||
		 * strCandi.contains("을를/JKO") || strCandi.contains("로으로/JKB") ||
		 * strCandi.contains("에/JKB") || strCandi.contains("와과/JC") ||
		 * strCandi.contains("와과/JKB") || strCandi.contains("에서/JKB") ||
		 * strCandi.contains("자는/ETM") || strCandi.contains("JX") ||
		 * strCandi.contains("EP") || strCandi.contains("EF") ||
		 * strCandi.contains("ETM") || strCandi.contains("보다/JKB") ||
		 * strCandi.contains("이가/JKC") || strCandi.contains("나이나/JC") ||
		 * strCandi.contains("EC") || strCandi.contains("ECS")) return true;
		 */
		if (strCandi.contains("JKS") || strCandi.contains("JKC") || strCandi.contains("JKG") || strCandi.contains("JX") ||
				strCandi.contains("JKO") || strCandi.contains("JKV") || strCandi.contains("JKQ") || strCandi.contains("JC") ||
				strCandi.contains("EP") || strCandi.contains("EF") || strCandi.contains("EC") || strCandi.contains("ECS") ||
				strCandi.contains("ETN") || strCandi.contains("ETM") || strCandi.contains("JKB"))
			return true;
		return false;
	}

	private boolean is_EC_morpheme(String strCandi)
	{
		if (strCandi.contains("어아/EC") || strCandi.contains("었았/EP") || strCandi.contains("어야아야/ECS") ||
				strCandi.contains("리라고/EC"))
			return true;
		return false;
	}

	private boolean is_empty_candidate(List<String> listAddCandi)
	{
		if (listAddCandi.size() > 0)
			return false;
		return true;
	}

	private boolean make_candidate_prev_pattern4(String strCandi, List<String> listAddCandi)
	{
		if (is_EC_morpheme(strCandi)) {
			// System.out.println("pattern4 start...");
			String[] arrX = strCandi.split(" ");
			int j2 = 0;
			/*
			 * for(j2 = arrX.length-1; j2 >= 0; j2--){ if
			 * (is_EC_morpheme(arrX[j2])) break; else continue; }
			 */
			for (j2 = 0; j2 < arrX.length; j2++) {
				if (is_EC_morpheme(arrX[j2]))
					break;
				continue;
			}
			String strAdd = "";
			String strAddMP = "";
			for (int j3 = 0; j3 < j2; j3++) {
				if (!is_symbolMP(arrX[j3])) {
					strAdd = strAdd + undo(arrX[j3]);
					strAddMP = strAddMP + arrX[j3] + " ";
				}
			}
			if (is_all_NNG_NNP(strAddMP))
				return true;
			if (strAdd.trim().length() > 1) {
				// if (is_all_english(strAdd))
				// strAdd = strAdd+"/SL";
				// else
				// strAdd = strAdd+"/NNG";
				if (my_search_PATTERN(undo(strAdd))) {
					//System.out.println("pattern 4 already exist : " + undo(strAdd));
					return true;
				}
				else {
					//System.out.println("pattern 4 already not exist : " + undo(strAdd));
				}
			}
		}
		return false;
	}
	
	private boolean make_candidate_prev_pattern2(String strCandi, List<String> listAddCandi)
	{
		if (is_J_morpheme(type(last_morpheme(strCandi)))) {
			// if (is_J_morpheme((strCandi))) {
			// System.out.println("pattern2 start..");
			String[] arrX = strCandi.split(" ");
			int j2 = 0;
			/*
			 * for (j2 = 0; j2 < arrX.length; j2++) { if
			 * (is_J_morpheme(arrX[j2])) break;
			 * 
			 * continue; }
			 */
			for (j2 = arrX.length - 1; j2 >= 0; j2--) {
				if (is_J_morpheme(arrX[j2]))
					continue;
				else
					break;
			}
			String strAddMP = "";
			String strAdd = "";
			int length = 0;
			for (int j3 = 0; j3 <= j2; j3++) {
				if (!is_symbolMP(arrX[j3])) {
					// System.out.println("pattern2 arrx: " + arrX[j3] );
					strAdd = strAdd + undo(arrX[j3]);
					strAddMP = strAddMP + arrX[j3] + " ";
					length++;
				}
			}
			if (length > 1 && is_all_NNG_NNP(strAddMP)) {
				// if (is_all_NNG_NNP(strAddMP)) {
				// System.out.println("pattern2 is all NNG");
				return true;
				// return false;
			}
			if (strAdd.trim().length() > 1) {
				if (is_all_english(strAdd))
					strAdd = strAdd + "/SL";
				else
					strAdd = strAdd + "/NNG";
				listAddCandi.add(strAdd.trim());
				// System.out.println("pattern2 res(add) : " + strAdd);
				return true;
			}
			else if (strAdd.trim().length() == 1) {
				// System.out.println("pattern2 res(not add) : " + strAdd);
				return true;
				// return false;
			}
		}
		return false;
	}

	private boolean is_symbolMP(String str)
	{
		if (str.contains("/SF") || str.contains("/SE") || str.contains("/SS") || str.contains("/SP") ||
				str.contains("/SO") || str.contains("/SW") || str.contains("/SY") || str.contains("/SC") || str.contains("/SN"))
			return true;
		return false;
	}

	private boolean is_all_NNG_NNP(String str)
	{
		String[] chk_nng = str.split(" ");
		for (int icnt = 0; icnt < chk_nng.length; icnt++) {
			// if ((chk_nng[icnt].contains("/NNG") ||
			// chk_nng[icnt].contains("/NNB") ||
			// chk_nng[icnt].contains("/NNP")))
			// continue;
			// else
			// return false;
			if (!chk_nng[icnt].contains("/NNG") && !chk_nng[icnt].contains("/NNP"))
				return false;
		}
		return true;
	}

	private boolean is_all_UNKNOWN(String str)
	{
		String[] chk_nng = str.split(" ");
		for (int icnt = 0; icnt < chk_nng.length; icnt++) {
			if (!chk_nng[icnt].contains("UNKNOWN"))
				return false;
		}
		return true;
	}

	private boolean is_all_english(String str)
	{
		String str_no_symbol = remove_symbol(str);
		for (int icnt = 0; icnt < str_no_symbol.length(); icnt++) {
			char c = str_no_symbol.charAt(icnt);
			if ((c >= 0X61 && c <= 0x7A) || (c >= 0x41 && c <= 0x5A))
				continue;
			return false;
		}
		return true;
	}

	private boolean make_candidate_prev_pattern(String strCandi, List<String> listAddCandi)
	{
		if (is_verb_morpheme(strCandi) || is_MM_NNP_morpheme(type(last_morpheme(strCandi)))) {
			String[] arrCandi = strCandi.split(" ");
			if (is_MM_NNP_morpheme(type(last_morpheme(strCandi)))) {
				String strAdd = "";
				String strAddMP = "";
				for (int j3 = 0; j3 < arrCandi.length - 1; j3++) {
					if (!is_symbolMP(arrCandi[j3])) {
						strAdd = strAdd + undo(arrCandi[j3]);
						strAddMP = strAddMP + arrCandi[j3] + " ";
					}
				}
				// return false;
				if (is_all_NNG_NNP(strAddMP)) {
					// System.out.println("pattern1 is all NNG");
					return true;
					// return false;
				}
				if (strAdd.trim().length() > 1) {
					if (is_all_english(strAdd))
						strAdd = strAdd + "/SL";
					else
						strAdd = strAdd + "/NNG";
					listAddCandi.add(strAdd);
					// System.out.println("pattern1 res : " + strAdd);
					return true;
				}
			}
			else {
				for (int j2 = 0; j2 < arrCandi.length; j2++) {
					if (is_verb_morpheme(arrCandi[j2])) {
						String strAdd = "";
						String strAddMP = "";
						// if (j2 == 0)
						// return true;
						// return false;
						int len = 0;
						for (int j3 = 0; j3 < j2; j3++) {
							if (!is_symbolMP(arrCandi[j3])) {
								strAdd = strAdd + undo(arrCandi[j3]);
								strAddMP = strAddMP + arrCandi[j3] + " ";
								len++;
							}
						}
						if (len > 1 && is_all_NNG_NNP(strAddMP)) {
							// if (is_all_NNG_NNP(strAddMP)) {
							// System.out.println("pattern1 is all NNG");
							// return false;
							return true;
						}
						if (strAdd.trim().length() > 1) {
							if (is_all_english(strAdd))
								strAdd = strAdd + "/SL";
							else
								strAdd = strAdd + "/NNG";
							listAddCandi.add(strAdd.trim());
							// System.out.println("pattern1 res(add) : " +
							// strAdd);
							return true;
						}
						else if (strAdd.trim().length() == 1) {
							// System.out.println("pattern1 res(not add) : " +
							// strAdd);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String match_nextchar(List<String> listOriginCorpus, List<String> listAddCandi, String original_words)
	{
		List<String> comblist = new ArrayList<String>();
		List<String> comblist2 = new ArrayList<String>();
		long start = System.currentTimeMillis();
		for (int icnt = 0; icnt < listOriginCorpus.size(); icnt++) {
			// String [] arrCorpus =
			// remove_symbol(listOriginCorpus.get(icnt)).split(" ");
			String[] arrCorpus = remove_spaceAll(listOriginCorpus.get(icnt)).split(" ");
			for (int kcnt = 0; kcnt < arrCorpus.length; kcnt++) {
				for (int jcnt = 0; jcnt < listAddCandi.size(); jcnt++) {
					if ((arrCorpus[kcnt].contains(undo(listAddCandi.get(jcnt)))) && !(arrCorpus[kcnt].equals(original_words)))
						comblist.add(remove_symbol(arrCorpus[kcnt].trim()));
					/// comblist.add(remove_spaceAll(arrCorpus[kcnt].trim()));
					else if ((arrCorpus[kcnt].equals(undo(listAddCandi.get(jcnt))))) {
						comblist2.add(remove_symbol(arrCorpus[kcnt].trim()));
					}
				}
			}
		}
		if (!comblist.isEmpty()) {
			remove_equal_candi(comblist);
			//System.out.println("comblist 1 : " + comblist);
			for (int jcnt = 0; jcnt < listAddCandi.size(); jcnt++) {
				String prevcomb = "";
				for (int icnt = 0; icnt < comblist.size(); icnt++) {
					if (comblist.get(icnt).length() > undo(listAddCandi.get(jcnt)).length()) {
						String T = comblist.get(icnt);
						String C = undo(listAddCandi.get(jcnt));
						if (T.indexOf(C) < 0)
							continue;
						int TindexDetail = T.indexOf(C) + C.length();
						if (TindexDetail > T.length() - 1)
							continue;
						String Tdetail = "";
						Tdetail = T.substring(TindexDetail, TindexDetail + 1);
						if (prevcomb.equals(""))
							prevcomb = Tdetail;
						else if (!Tdetail.isEmpty() && !prevcomb.equals(Tdetail)) {
							//System.out.println("nextchar : " + prevcomb + " -> " + Tdetail + " / " + T + " / " + listAddCandi.get(jcnt));
							long end = System.currentTimeMillis();
							float all_time = (float) ((end - start) / 1000.0);
							//System.out.println("match pattern : " + all_time);
							return listAddCandi.get(jcnt);
						}
					}
				}
			}
		}
		if (!comblist2.isEmpty()) {
			remove_equal_candi(comblist2);
			//System.out.println("comblist 2 : " + comblist2);
			String equal_str = comblist2.get(0);
			for (int jcnt = 0; jcnt < listAddCandi.size(); jcnt++) {
				if (undo(listAddCandi.get(jcnt)).equals(equal_str)) {
					//System.out.println("allsame : " + listAddCandi.get(jcnt));
					long end = System.currentTimeMillis();
					float all_time = (float) ((end - start) / 1000.0);
					//System.out.println("match pattern : " + all_time);
					return listAddCandi.get(jcnt);
				}
			}
		}
		//System.out.println("cannot found match : " + listAddCandi);
		long end = System.currentTimeMillis();
		float all_time = (float) ((end - start) / 1000.0);
		//System.out.println("match pattern : " + all_time);
		return null;
	}

	// private boolean is_updated_prev_pattern(List<String> listOriginCorpus,
	// String[] arrCorpus, List<String> listAddCandi, String original_words)
	// throws IOException, InterruptedException {
	private boolean is_updated_prev_pattern(List<String> listOriginCorpus, List<String> listAddCandi, String original_words)
			throws IOException, InterruptedException
	{
		String update_candi;
		if ((update_candi = match_nextchar(listOriginCorpus, listAddCandi, original_words)) != null) {
			if (update_candi.contains("/NNG")) {
				//System.out.println("New NNG Update : " + undo(update_candi) + "(" + original_words + ")");
				list_NewWords.add(update_candi);
				newWordsCnt++;
				// mecab_dic.put(undo(update_candi), 1);
				list_pattern_update(undo(update_candi));
			}
			else if (update_candi.contains("/SL")) {
				//System.out.println("New SL Update : " + undo(update_candi) + "(" + original_words + ")");
				list_NewWords.add(update_candi);
				newWordsCnt++;
				// mecab_dic.put(undo(update_candi), 1);
				list_pattern_update(undo(update_candi));
			}
			/*
			 * if (newWordsCnt >= DIC_UPDATE_COUNT && is_regist_mode) {
			 * modif_update(); }
			 */
			list_registered_words.add(original_words + " / " + undo(update_candi));
			return true;
		}
		return false;
	}
			
	public String undo(String str)
	{
		String tmp = "";
		String[] arr = str.split(" ");
		for (int i = 0; i < arr.length; i++) {
			String[] arr2 = arr[i].split("/");
			if (arr2[0].equals("")) {
				tmp = tmp + "/";
			}
			else {
				tmp = tmp + arr2[0];
			}
		}
		return tmp;
	}

	public void set_EF_EC(String path1, String path2) throws IOException
	{
		EF = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(path1));
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				EF.add(line);
			}
		}
		br.close();
		EC = new ArrayList<String>();
		BufferedReader br2 = new BufferedReader(new FileReader(path2));
		while ((line = br2.readLine()) != null) {
			if (!line.trim().equals("")) {
				EC.add(line);
			}
		}
		br2.close();
	}
}
