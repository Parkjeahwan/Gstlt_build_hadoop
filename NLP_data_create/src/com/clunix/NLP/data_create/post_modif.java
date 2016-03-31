package com.clunix.NLP.data_create;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import com.clunix.NLP.FirstJNI;

public class post_modif
{ // 후처리기 윤충배
	private static FirstJNI fjni = new FirstJNI();
	final private static String[] postProcStr = { "하/XSV 은는/ETM", "하/XSA 은는/ETM", "하/VV 은는/ETM", "하/VX 은는/ETM", "하/XSV 을를/ETM",
			"하/XSA 을를/ETM", "하/VV 을를/ETM", "하/VX 을를/ETM" };
	final private static String[] jcTransStr = { "동시/NNG", "동일/NNG", "유사/NNG", "흡사/NNG", "일치/NNG", "불일치/NNG", "동치/NNG", "같/VA",
			"다르/VA", "비슷하/VA", "비교/NNG" };
	final private static String[] predicate = { "VV", "VA", "VX", "VCP", "VCN", "MM", "XPN" };
	final private static String[] substantive = { "NNG", "NNP", "NNB", "NNBC", "NR", "NP", "XR", "XSN", "SN", "SL", "SH" };
	final private static String[] postpos = { "JKS", "JKC", "JKG", "JKO", "JKB", "JKV", "JKQ", "JC", "JX" };
	final private static String[] relativesPred = { "JKS", "JKC", "JKG", "JKO", "JKB", "JKV", "JKQ", "JC", "JX", "EP", "EF", "EC",
			"ECS", "ETN", "ETM", "XSN", "XSV", "XSA", "VCN", "VCP", "VX" };
	final private static String[] poststr = { "을를/JKO", "이가/JKS", "은는/JX", "의/JKG", "에/JKB", "로으로/JKB", "도/JX" };
	final private static String[] unitNoun = { "쌍/NNG", "짝/NNG", "해/NNG", "가닥/NNG" };
	final private static String[] subst = { "NNG", "NNP", "NNB", "NNBC", "NR", "NP" };
	final private static String[] ending = { "EP", "EF", "EC", "ECS", "ETN", "ETM" };
	final private static String[] pred2 = { "VV", "VA", "VX", "VCN", "VCP", "XSV", "XSA" };
	final private static TreeMap<String, Integer> transMap = new TreeMap<String, Integer>();
	
	public static void importVLNoun(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;

		while ((line = br.readLine()) != null) {
			if (!transMap.containsKey(line)) {
				transMap.put(line, 1);
			}
		}          
		br.close();    	
	}

	private static void merge_word(String[] words, int i)
	{
		String[] infos1 = words[i].split("/");
		String[] infos2 = words[i + 1].split("/");
		infos1[0] = infos1[0] + infos2[0];
		words[i] = infos1[0] + "/" + infos1[1];
		words[i + 1] = "";
		for (int j = i + 1; j < words.length - 1; j++) {
			words[j] = words[j + 1];
			words[j + 1] = "";
		}
	}

	private static String changeTag(String word, String tag)
	{
		String[] infos = word.split("/");
		if (infos.length != 2)
			return word;
		return infos[0] + "/" + tag;
	}

	private static String transSpaceStr(String[] words)
	{
		String res = null;
		for (int i = 0; i < words.length; i++) {
			if (words[i] == null)
				continue;
			if (res == null)
				res = words[i];
			else
				res = res + " ###/SPACE " + words[i];
		}
		return res;
	}

	private static String combineWords(String[] words)
	{
		String res = null;
		for (int i = 0; i < words.length - 1; i++) {
			String[] infos = words[i].split("/");
			if (infos.length != 2)
				return null;
			if (res == null)
				res = infos[0];
			else
				res = res + infos[0];
		}
		return res;
	}

	private static String MP_to_Str(String[] arr)
	{
		String res = null;
		for (int icnt = 0; icnt < arr.length; icnt++) {
			if (arr[icnt].equals(""))
				continue;
			if (res == null)
				res = arr[icnt];
			else
				res += " " + arr[icnt];
		}
		return res;
	}

	private static boolean checkMorp(String word, String morp)
	{
		String[] infos = word.split("/");
		if (infos.length != 2)
			return false;
		if (infos[1].equals(morp))
			return true;
		return false;
	}

	private static boolean checkSubst(String words)
	{
		String[] infos;
		infos = words.split("/");
		if (infos.length != 2)
			return false;
		for (int i = 0; i < subst.length; i++) {
			if (infos[1].equals(subst[i]))
				return true;
		}
		return false;
	}

	private static boolean checkS(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		if (infos[1].startsWith("S"))
			return true;
		return false;
	}

	private static boolean checkSN(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		if (infos[1].startsWith("SN"))
			return true;
		return false;
	}

	private static boolean checkJ(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		for (int i = 0; i < postpos.length; i++) {
			if (infos[1].equals(postpos[i]))
				return true;
		}
		return false;
	}

	private static boolean checkJStr(String word, String j)
	{
		String cont;
		for (int i = 0; i < poststr.length; i++) {
			if (poststr[i].equals(j))
				continue;
			cont = word + " " + poststr[i];
			if (time3(cont))
				return true;
		}
		return false;
	}

	private static boolean checkpredRel(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		for (int i = 0; i < relativesPred.length; i++) {
			if (infos[1].equals(relativesPred[i]))
				return true;
		}
		return false;
	}

	private static boolean checkE(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		for (int i = 0; i < ending.length; i++) {
			if (infos[1].equals(ending[i]))
				return true;
		}
		return false;
	}

	private static boolean checkJEV(String word)
	{
		String[] infos;
		infos = word.split("/");
		if (infos.length != 2)
			return false;
		for (int i = 0; i < ending.length; i++) {
			if (infos[1].equals(ending[i]))
				return true;
		}
		for (int i = 0; i < postpos.length; i++) {
			if (infos[1].equals(postpos[i]))
				return true;
		}
		for (int i = 0; i < pred2.length; i++) {
			if (infos[1].equals(pred2[i]))
				return true;
		}
		if (infos[1].startsWith("S"))
			return true;
		return false;
	}

	private static boolean checkHanMM(String[] words, int idx)
	{
		boolean find = true;
		String[] infos2 = words[idx].split("/");
		if (infos2[1].equals("NNB")) {
			find = false;
		}
		String cont = null;
		for (int j = idx; j < words.length; j++) {
			if (words[j].equals("###/SPACE"))
				continue;
			if (words[j].contains("NNG")) {
				if (cont == null)
					cont = words[j];
				else
					cont += " " + words[j];
			}
			else
				break;
		}
		if (cont != null) {
			if (time1("어떤/MM" + cont))
				find = false;
		}
		infos2 = words[idx].split("/");
		if (infos2[1].equals("NNBC"))
			find = false;
		for (int j = 0; j < unitNoun.length; j++) {
			if (words[idx].equals(unitNoun[j])) {
				find = false;
				break;
			}
		}
		return find;
	}

	public static String revise_parsing(String line) throws IOException, InterruptedException
	{
		String[] words = line.split(" ");
		String s = line;
		if (words.length == 0)
			return s;
		if (words[words.length - 1].equals("다/EC")) {
			words[words.length - 1] = "다/EF";
			s = MP_to_Str(words);
		}
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals("###/SPACE"))
				continue;
			/*
			 * 을를/JKO + 체언 + 하/XSA 패턴 혹은, (에/JKB || 에서/JKB || 에게/JKB || 로으로/JKB)
			 * + 체언 + XSA 패턴 등장시 해당 패턴이 3회 이상 등장하면 XSA를 XSV로 변환하고 변혼 테이블과 동작성 명사
			 * 리스트에 가나다순 등록 NNG + XSA 패턴이 변환 테이블에 있다면 XSA를 XSV로 변경
			 */
			if (checkMorp(words[i], "NNG")) {
				if (i + 1 < words.length) {
					String cont = null;
					if (words[i + 1].equals("###/SPACE")) {
						if (i + 2 < words.length && checkMorp(words[i + 2], "XSA")) {
							cont = words[i] + " " + words[i + 2];
							if (transMap.containsKey(cont)) {
								words[i + 2] = changeTag(words[i + 2], "XSV");
								s = MP_to_Str(words);
							}
						}
					}
					else {
						if (checkMorp(words[i + 1], "XSA")) {
							cont = words[i] + " " + words[i + 1];
							if (transMap.containsKey(cont)) {
								words[i + 1] = changeTag(words[i + 1], "XSV");
								s = MP_to_Str(words);
							}
						}
					}
				}
			}
			/*
			 * 어아도록/ECS 하/XSV -> 어아도록/ECS 하/VV
			 */
			if (words[i].equals("어아도록/ECS")) {
				if (i + 2 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("하/XSV")) {
							words[i + 2] = "하/VV";
							s = MP_to_Str(words);
						}
					}
					else {
						if (words[i + 1].equals("하/XSV")) {
							words[i + 1] = "하/VV";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 숫자 패턴 "/SN 기/ETN" -> "/SN 기/NNBC"
			 */
			if (checkSN(words[i])) {
				if (i + 1 < words.length && words[i + 1].equals("기/ETN")) {
					words[i + 1] = "기/NNBC";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 보통/MAJ -> 보통/MAG
			 */
			if (words[i].equals("보통/MAJ")) {
				words[i] = "보통/MAG";
				s = MP_to_Str(words);
			}
			/*
			 * 국사학/NNG 와과/JC -> 국사학과/NNG 20151105
			 */
			if (words[i].equals("국사학/NNG")) {
				if (i + 1 < words.length && words[i + 1].equals("와과/JC")) {
					words[i] = "국사학과/NNG";
					words[i + 1] = "";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 어떻/MM -> 어떻/VA 20151023
			 */
			if (words[i].equals("어떻/MM")) {
				words[i] = "어떻/VA";
				s = MP_to_Str(words);
			}
			/*
			 * 게/ECS 보/VX -> 게/ECS 보/VV 20151023
			 */
			if (words[i].equals("게/ECS")) {
				if (i + 1 < words.length && words[i + 1].equals("보/VX")) {
					words[i + 1] = "보/VV";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 완벽/NNG 하/XSV -> 완벽/NNG 하/XSA 20160112
			 */
			if (words[i].equals("완벽/NNG")) {
				if (i + 1 < words.length && words[i + 1].equals("하/XSV")) {
					words[i + 1] = "하/XSA";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 이/MM 같/VA -> 이/NP 같/VA 20151023
			 */
			if (words[i].equals("이/MM")) {
				if (i + 1 < words.length && words[i + 1].equals("같/VA")) {
					words[i] = "이/NP";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 은는다고/EC 말/NNG 하/XSV -> 은는다고/ECS 말/NNG 하/XSV 20151105
			 */
			if (words[i].equals("은는다고/EC")) {
				if (i + 1 < words.length && words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length && words[i + 2].equals("말/NNG")) {
						if (i + 3 < words.length && words[i + 3].equals("하/XSV")) {
							words[i] = "은는다고/ECS";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 막/MAJ -> 막/MAG
			 */
			if (words[i].equals("막/MAJ")) {
				words[i] = "막/MAG";
				s = MP_to_Str(words);
			}
			/*
			 * "XX다고/ECS 하/VX" 패턴이 출연 할 경우 "다/EF 고/JKQ 하/VV"로 변경
			 */
			if (words[i].contains("다고/ECS")) {
				if (i + 1 < words.length && words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length && words[i + 2].equals("하/VX")) {
						String sp_word[] = words[i].split("다");
						words[i] = sp_word[0] + "다/EF 고/JKQ";
						words[i + 2] = "하/VV";
						s = MP_to_Str(words);
					}
				} else if (i + 1 < words.length && words[i + 1].equals("하/VX")) {
					String sp_word[] = words[i].split("다");
					words[i] = sp_word[0] + "다/EF 고/JKQ";
					words[i + 1] = "하/VV";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 일반 명사/NNG 되/VX -> NNG 되/VV 20151103
			 */
			if (checkMorp(words[i], "NNG")) {
				if (i + 1 < words.length && words[i + 1].equals("되/VX")) {
					words[i + 1] = "되/VV";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 일반 명사/NNG 링/JKO -> 일반 명사 + 링/NNG 으로 변경하고 스페이스가 있다면 (###/SPACE) 일반
			 * 명사/NNG 링/NNG로 변환 20151103
			 */
			if (checkMorp(words[i], "NNG")) {
				if (i + 1 < words.length && words[i + 1].equals("링/JKO")) {
					merge_word(words, i);
					s = MP_to_Str(words);
				}
				else if (i + 1 < words.length && words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length && words[i + 2].equals("링/JKO")) {
						words[i + 2] = "링/NNG";
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 연결 어미/EC 하나/NNG 또는 /ECS 하나/NNG -> 연결 어미/EC + 하/VV 나/EF 으로 변경하고
			 * 스페이스가 있는경우도 (###/SPACE) 동일하게 변경 20151118
			 */
			if (checkMorp(words[i], "EC") || checkMorp(words[i], "ECS")) {
				if (i + 1 < words.length && words[i + 1].equals("하나/NNG")) {
					words[i + 1] = "하/VV 나/EF";
					s = MP_to_Str(words);
				}
				else if (i + 1 < words.length && words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length && words[i + 2].equals("하나/NNG")) {
						words[i + 2] = "하/VV 나/EF";
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 기호/SY 하/XSV -> 기호/SY 하/VV로 변경하고 스페이스가 있는경우도 (###/SPACE) 동일하게 변경
			 * 20151118
			 */
			if (checkMorp(words[i], "SY")) {
				if (i + 1 < words.length && words[i + 1].equals("하/XSV")) {
					words[i + 1] = "하/VV";
					s = MP_to_Str(words);
				}
				else if (i + 1 < words.length && words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length && words[i + 2].equals("하/XSV")) {
						words[i + 2] = "하/VV";
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 은는가/EC ./SF -> 은는가/EF ./SF 20151023
			 */
			if (words[i].equals("은는가/EC")) {
				if (i + 1 < words.length && words[i + 1].equals("./SF")) {
					words[i] = "은는가/EF";
					s = MP_to_Str(words);
				}
			}
			/*
			 * 없이/MAG -> 없/VA 이/JKB 20151023
			 */
			if (words[i].equals("없이/MAG")) {
				words[i] = "없/VA 이/JKB";
				s = MP_to_Str(words);
			}
			/*
			 * 있/VV 을를/ETM -> 있/VA 을를/ETM 추후 반영 예정
			 */
			/*
			 * if (words[i].equals("있/VV")) { if (i+1 < words.length) { boolean
			 * find = false; if (words[i+1].equals("###/SPACE")) { if
			 * (words[i+2].equals("을를/ETM")) { find = true; } } else { if
			 * (words[i+1].equals("을를/ETM")) { find = true; } }
			 * 
			 * if (find) { words[i] = "있/VA"; s = MP_to_Str(words); } } }
			 */
			/*
			 * 은는다고/EC 하/VV -> 은는다고/ECS 하/VV 다라고/EC 하/VV -> 다라고/ECS 하/VV
			 * 은는다라고/EC 하/VV -> 은는다라고/ECS 하/VV
			 */
			if (words[i].equals("은는다고/EC") || words[i].equals("다라고/EC") || words[i].equals("은는다라고/EC")) {
				if (i + 1 < words.length) {
					boolean find = false;
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("하/VV")) {
							find = true;
						}
					}
					else {
						if (words[i + 1].equals("하/VV")) {
							find = true;
						}
					}
					if (find) {
						if (words[i].equals("은는다고/EC")) {
							words[i] = "은는다고/ECS";
						}
						else if (words[i].equals("다라고/EC")) {
							words[i] = "다라고/ECS";
						}
						else if (words[i].equals("은는다라고/EC")) {
							words[i] = "은는다라고/ECS";
						}
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 위하/VV 어아/EC 싸우/VV -> 위하/VV 어아/ECS 싸우/VV
			 */
			if (words[i].equals("어아/EC")) {
				if (i > 0 && i + 1 < words.length) {
					boolean find = false;
					if (words[i - 1].equals("###/SPACE")) {
						if (i - 2 > 0 && words[i - 2].equals("위하/VV")) {
							if (words[i + 1].equals("###/SPACE")) {
								if (i + 2 < words.length && words[i + 2].equals("싸우/VV")) {
									find = true;
								}
							}
							else {
								if (words[i + 1].equals("싸우/VV")) {
									find = true;
								}
							}
						}
					}
					else {
						if (words[i - 1].equals("위하/VV")) {
							if (words[i + 1].equals("###/SPACE")) {
								if (i + 2 < words.length && words[i + 2].equals("싸우/VV")) {
									find = true;
								}
							}
							else {
								if (words[i + 1].equals("싸우/VV")) {
									find = true;
								}
							}
						}
					}
					if (find) {
						words[i] = "어아/ECS";
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 다/EC '/SY 은는/JX 또는 다/EC "/SY 은는/JX 패턴일 경우, 은는/JX -> 다라는/ETM 변경
			 */
			if (words[i].equals("다/EC")) {
				if (i + 1 < words.length) {
					boolean find = false;
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("'/SY") || words[i + 2].equals("\"/SY")
								|| words[i + 2].equals("’/SY")) {
							if (i + 3 < words.length) {
								if (words[i + 3].equals("###/SPACE")) {
									if (words[i + 4].equals("은는/JX")) {
										words[i + 4] = "다라는/ETM";
										find = true;
									}
								}
								else {
									if (words[i + 3].equals("은는/JX")) {
										words[i + 3] = "다라는/ETM";
										find = true;
									}
								}
							}
						}
					}
					else {
						if (words[i + 1].equals("'/SY") || words[i + 1].equals("\"/SY")
								|| words[i + 1].equals("’/SY")) {
							if (i + 2 < words.length) {
								if (words[i + 2].equals("###/SPACE")) {
									if (words[i + 3].equals("은는/JX")) {
										words[i + 3] = "다라는/ETM";
										find = true;
									}
								}
								else {
									if (words[i + 2].equals("은는/JX")) {
										words[i + 2] = "다라는/ETM";
										find = true;
									}
								}
							}
						}
					}
					if (find) {
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 다/EC '/SY 또는 다/EC "/SY 패턴일 경우, 다/EC -> 다/EF 로 변경
			 */
			if (words[i].equals("다/EC")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("'/SY") || words[i + 2].equals("\"/SY")
								|| words[i + 2].equals("’/SY")) {
							words[i] = "다/EF";
							s = MP_to_Str(words);
						}
					}
					else {
						if (words[i + 1].equals("'/SY") || words[i + 1].equals("\"/SY")
								|| words[i + 1].equals("’/SY")) {
							words[i] = "다/EF";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 어절 끝이 "다/EC ./SY" 패턴일 경우, 다/EC -> 다/EF ./SY -> ./SF 로 변경
			 */
			if (words[i].equals("다/EC")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("./SY")) {
						if (words[i + 2].equals("###/SPACE")) {
							words[i] = "다/EF";
							words[i + 1] = "./SF";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 수/NNB 치/VV 패턴이 출현할 경우, 수/NNB 치/VV =>  수치/NNG 로 변경
			 */
			if (words[i].equals("수/NNB")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("치/VV")) {
						merge_word(words, i);
						s = MP_to_Str(words);
					}
				}
			}
			/*
			* NNB, NNBC, SL, NNG, ETN, XSN, 등등... 'Sub' 에 포함되는 것들 다음에 "정도/NNG" 가 나오면 "정도/NNB"로 변경
			 */
			if (words[i].equals("정도/NNG")) {
				if (i - 1 >= 0) {
					String[] prevInfo = words[i - 1].split("/");
					for (int k = 0; k < substantive.length; k++) {
						if (prevInfo[1].equals(substantive[k])) {
							words[i] = "정도/NNB";
							s = MP_to_Str(words);
							break;
						}
					}			
				}
			}
			/*
			 * 더/MAG 하/VV 패턴이 출현할 경우, 더/MAG 하/VV => 더하/VV 로 변경
			 */
			if (words[i].equals("더/MAG")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("하/VV")) {
						words[i] = "더/VV";
						merge_word(words, i);
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * /SSC 고/EC 패턴이 출현할 경우, /SSC 고/EC => /SSC 고/ECS 로 변경
			 */
			if (words[i].contains("/SSC")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("고/EC")) {
						words[i + 1] = "고/ECS";
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 *  *학/NNG 와과/JC 패턴이 출현할 경우, *학/NNG 와과/JC => *학과/NNG로 변경
			 *  (*학/NNG 와과/JC 바로 뒤 3어절 이내에 에 ‘ ~학/NNG’ 라는 형태소가 나타나지 않거나 || (‘~학’이 오더라도) ~학/NNG 와과/JC 가 3어절 이내에 나타나면)
			 */
			if (words[i].contains("학/NNG")) {
				if (i + 2 < words.length) {
					if (words[i + 1].equals("와과/JC")) {
						int check_space = 0;
						boolean change_word = false;
						for (int j = i + 2; j < words.length; j++) {
							if (words[j].equals("###/SPACE")) {
								check_space++;
							} else if ( words[j].contains("학/NNG")) {
								if (j+1 < words.length && words[j+1].equals("와과/JC")) {
									change_word = true;
								} else {
									break;
								}
							}
							if (check_space > 3) {
								change_word = true;
								break;
							}
						}
						if (change_word) {
							words[i + 1] = "과/NNG";
							merge_word(words, i);
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 어야아야/EC 하/VV 패턴이 출현할 경우, 어야아야/EC 하/VV -> 어야아야/ECS 하/VX 로 변경
			 */
			if (words[i].equals("어야아야/EC")) {
				if (i + 2 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("하/VV")) {
							words[i] = "어야아야/ECS";
							words[i + 2] = "하/VX";
							s = MP_to_Str(words);
						}
					}
					else {
						if (words[i + 1].equals("하/VV")) {
							words[i] = "어야아야/ECS";
							words[i + 1] = "하/VX";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 어아도/EC 되/VV -> 어아도/ECS 되/VV
			 */
			if (words[i].equals("어아도/EC")) {
				if (i + 2 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (words[i + 2].equals("되/VV")) {
							words[i] = "어아도/ECS";
							s = MP_to_Str(words);
						}
					}
					else {
						if (words[i + 1].equals("되/VV")) {
							words[i] = "어아도/ECS";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * 은는지/EC -> 은는지/ECS
			 */
			if (words[i].equals("은는지/EC")) {
				words[i] = "은는지/ECS";
				s = MP_to_Str(words);
			}
			/*
			 * 와과/JC -> 와과/JKB 오분석 처리 와과/JC 다음에 나오는 단어가 아래의 목록 중 있을 경우 와과/JKB로
			 * 변경 "동시/NNG", "동일/NNG", "유사/NNG", "흡사/NNG", "일치/NNG", "불일치/NNG",
			 * "동치/NNG", "같/VA", "다르/VA", "비슷하/VA", "비교/NNG" 와과/JC -> 와과/JKB 오분석
			 * 처리
			 */
			if (words[i].equals("와과/JC")) {
				if (i + 1 < words.length) {
					String w = null;
					if (words[i + 1].equals("###/SPACE")) {
						if (i + 2 < words.length)
							w = words[i + 2];
					}
					else
						w = words[i + 1];
					if (w != null) {
						for (int j = 0; j < jcTransStr.length; j++) {
							if (w.equals(jcTransStr[j])) {
								words[i] = "와과/JKB";
								s = MP_to_Str(words);
								break;
							}
						}
					}
				}
			}
			/*
			 * 와과/JC + /MAG -> 와과/JKB 와과 앞 구절과 뒷 구절이 대구를 이루면 JC 아니면 JKB 대구 조건 :
			 * 와과 바로 앞 형태소가 뒤 5형태소 거리 이내에 존재
			 */
			if (words[i].equals("와과/JC") && i + 1 < words.length) {
				String w = null;
				int index = 0;
				if (words[i + 1].equals("###/SPACE")) {
					if (i + 2 < words.length) {
						w = words[i + 2];
						index = 3;
					}
				}
				else {
					w = words[i + 1];
					index = 2;
				}
				if (w != null) {
					if (w.contains("/MAG")) {
						String front = null;
						String back = null;
						boolean find = false;
						boolean bfind = false;
						int cnt = 1;
						if (i > 0) {
							if (words[i - 1].equals("###/SPACE")) {
								if (i > 1)
									front = words[i - 2];
							}
							else
								front = words[i - 1];
						}
						if (front != null) {
							for (int j = i + index; j < words.length; j++) {
								if (words[j].equals("###/SPACE"))
									continue;
								if (!find && front.equals(words[j])) {
									find = true;
								}
								if (!bfind) {
									String[] infos = words[j].split("/");
									if (infos.length != 2)
										break;
									if (infos[1].equals("XSV") || infos[1].equals("XSA")) {
										String prev = null;
										if (j - 1 > i + index) {
											if (words[j - 1].equals("###/SPACE") && j - 2 > i + index) {
												prev = words[j - 2];
											}
											else
												prev = words[j - 1];
										}
										if (prev != null) {
											String[] prevInfo = prev.split("/");
											for (int k = 0; k < substantive.length; k++) {
												if (prevInfo[1].equals(substantive[k])) {
													bfind = true;
													back = prev;
													break;
												}
											}
										}
									}
									if (!bfind) {
										for (int k = 0; k < predicate.length; k++) {
											if (infos[1].equals(predicate[k])) {
												bfind = true;
												back = words[j];
												break;
											}
										}
									}
								}
								if ((cnt++ > 4 || find) && bfind)
									break;
							}
						}
						if (!find) {
							if (time3("와과/JKB " + back) || time3("와과/JC " + back)) {
								words[i] = "와과/JKB";
								s = MP_to_Str(words);
							}
							else {
								words[i] = "와과/JC";
								s = MP_to_Str(words);
							}
						}
						else {
							words[i] = "와과/JC";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * S에서 ECS 바로 뒤에 한 /MM 이 후속 출현하고 && 한/MM 바로 뒤에 NNBC가 출현하지 않으면 한/MM 을
			 * 하/XSV 은는/ETM으로 교체
			 * 
			 * S에서 VX/VCP/VCN + 은는/ETM의 경우 한/MM을 한/NNG로 변경 예외 - 한/MM + ?/NNB -
			 * 한/MM을 어떤/MM 으로 변경 후 후속하는 NNG 연속체와 결합. 결합한 연속체가 말뭉치에 존재하지 않으면
			 * 한/NNG로 변경 - 한/MM + ?/NNBC, 한/MM + 단위성 명사(쌍, 짝, 해, 가닥)
			 */
			if (words[i].equals("한/MM")) {
				if (i > 0 && i + 1 < words.length) {
					if (words[i - 1].equals("###/SPACE")) {
						if (i > 1) {
							String[] infos = words[i - 2].split("/");
							if (infos[1].equals("ECS")) {
								if (words[i + 1].equals("###/SPACE")) {
									if (i + 2 < words.length) {
										String[] infos2 = words[i + 2].split("/");
										if (!infos2[1].equals("NNBC")) {
											words[i] = "하/XSV 은는/ETM";
											s = MP_to_Str(words);
										}
									}
								}
								else {
									String[] infos2 = words[i + 1].split("/");
									if (!infos2[1].equals("NNBC")) {
										words[i] = "하/XSV 은는/ETM";
										s = MP_to_Str(words);
									}
								}
							}
						}
					}
					else {
						String[] infos = words[i - 1].split("/");
						if (infos[1].equals("ECS")) {
							if (words[i + 1].equals("###/SPACE")) {
								if (i + 2 < words.length) {
									String[] infos2 = words[i + 2].split("/");
									if (!infos2[1].equals("NNBC")) {
										words[i] = "하/XSV 은는/ETM";
										s = MP_to_Str(words);
									}
								}
							}
							else {
								String[] infos2 = words[i + 1].split("/");
								if (!infos2[1].equals("NNBC")) {
									words[i] = "하/XSV 은는/ETM";
									s = MP_to_Str(words);
								}
							}
						}
					}
					if (words[i - 1].equals("###/SPACE")) {
						if (i > 1) {
							if (words[i - 2].equals("은는/ETM")) {
								if (i > 2) {
									if (words[i - 3].equals("###/SPACE")) {
										String[] infos = words[i - 4].split("/");
										if (infos[1].equals("VX") || infos[1].equals("VCP") || infos[1].equals("VCN")) {
											if (words[i + 1].equals("###/SPACE")) {
												if (i < words.length - 2) {
													if (checkHanMM(words, i + 2)) {
														words[i] = "한/NNG";
														s = MP_to_Str(words);
													}
												}
											}
											else {
												if (checkHanMM(words, i + 1)) {
													words[i] = "한/NNG";
													s = MP_to_Str(words);
												}
											}
										}
									}
									else {
										String[] infos = words[i - 3].split("/");
										if (infos[1].equals("VX") || infos[1].equals("VCP") || infos[1].equals("VCN")) {
											if (words[i + 1].equals("###/SPACE")) {
												if (i < words.length - 2) {
													if (checkHanMM(words, i + 2)) {
														words[i] = "한/NNG";
														s = MP_to_Str(words);
													}
												}
											}
											else {
												if (checkHanMM(words, i + 1)) {
													words[i] = "한/NNG";
													s = MP_to_Str(words);
												}
											}
										}
									}
								}
							}
						}
					}
					else {
						if (words[i - 1].equals("은는/ETM")) {
							if (i > 1) {
								if (words[i - 2].equals("###/SPACE")) {
									String[] infos = words[i - 3].split("/");
									if (infos[1].equals("VX") || infos[1].equals("VCP") || infos[1].equals("VCN")) {
										if (words[i + 1].equals("###/SPACE")) {
											if (i < words.length - 2) {
												if (checkHanMM(words, i + 2)) {
													words[i] = "한/NNG";
													s = MP_to_Str(words);
												}
											}
										}
										else {
											if (checkHanMM(words, i + 1)) {
												words[i] = "한/NNG";
												s = MP_to_Str(words);
											}
										}
									}
								}
								else {
									String[] infos = words[i - 2].split("/");
									if (infos[1].equals("VX") || infos[1].equals("VCP") || infos[1].equals("VCN")) {
										if (words[i + 1].equals("###/SPACE")) {
											if (i < words.length - 2) {
												if (checkHanMM(words, i + 2)) {
													words[i] = "한/NNG";
													s = MP_to_Str(words);
												}
											}
										}
										else {
											if (checkHanMM(words, i + 1)) {
												words[i] = "한/NNG";
												s = MP_to_Str(words);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			/*
			 * NNG-하/XSV-게/EC 패턴이 출현하면 && 을를/JKO-?/NNG-하/?-(은는/ETM or 을를/ETM) 총
			 * 8가지가 말뭉치 상에 3회 이상 출현한 패턴이 아닌경우 ?/NNG-하/XSV-게/EC를
			 * ?/NNG-하/XSA-게/JKB로 교체
			 */
			if (words[i].contains("NNG")) {
				if (i > 0 && i + 2 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (i + 3 < words.length) {
							if (words[i + 2].equals("하/XSV")) {
								if (words[i + 3].equals("###/SPACE")) {
									if (i + 4 < words.length) {
										if (words[i + 4].equals("게/EC")) {
											if (words[i - 1].equals("###/SPACE")) {
												if (i > 1) {
													if (words[i - 2].equals("을를/JKO")) {
														String cont;
														boolean find = false;
														for (int j = 0; j < postProcStr.length; j++) {
															cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
															if (time3(cont)) {
																find = true;
																break;
															}
														}
														if (!find) {
															words[i + 2] = "하/XSA";
															words[i + 4] = "게/JKB";
															s = MP_to_Str(words);
														}
													}
												}
											}
											else {
												if (words[i - 1].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (!find) {
														words[i + 2] = "하/XSA";
														words[i + 4] = "게/JKB";
														s = MP_to_Str(words);
													}
												}
											}
										}
									}
								}
								else {
									if (words[i + 3].equals("게/EC")) {
										if (words[i - 1].equals("###/SPACE")) {
											if (i > 1) {
												if (words[i - 2].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (!find) {
														words[i + 2] = "하/XSA";
														words[i + 3] = "게/JKB";
														s = MP_to_Str(words);
													}
												}
											}
										}
										else {
											if (words[i - 1].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (!find) {
													words[i + 2] = "하/XSA";
													words[i + 3] = "게/JKB";
													s = MP_to_Str(words);
												}
											}
										}
									}
								}
							}
						}
					}
					else {
						if (words[i + 1].equals("하/XSV")) {
							if (words[i + 2].equals("###/SPACE")) {
								if (i + 3 < words.length) {
									if (words[i + 3].equals("게/EC")) {
										if (words[i - 1].equals("###/SPACE")) {
											if (i > 1) {
												if (words[i - 2].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (!find) {
														words[i + 1] = "하/XSA";
														words[i + 3] = "게/JKB";
														s = MP_to_Str(words);
													}
												}
											}
										}
										else {
											if (words[i - 1].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (!find) {
													words[i + 1] = "하/XSA";
													words[i + 3] = "게/JKB";
													s = MP_to_Str(words);
												}
											}
										}
									}
								}
							}
							else {
								if (words[i + 2].equals("게/EC")) {
									if (words[i - 1].equals("###/SPACE")) {
										if (i > 1) {
											if (words[i - 2].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (!find) {
													words[i + 1] = "하/XSA";
													words[i + 2] = "게/JKB";
													s = MP_to_Str(words);
												}
											}
										}
									}
									else {
										if (words[i - 1].equals("을를/JKO")) {
											String cont;
											boolean find = false;
											for (int j = 0; j < postProcStr.length; j++) {
												cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
												if (time3(cont)) {
													find = true;
													break;
												}
											}
											if (!find) {
												words[i + 1] = "하/XSA";
												words[i + 2] = "게/JKB";
												s = MP_to_Str(words);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			/*
			 * NNG-하/XSA-게/JKB 패턴이 출현하면 && 을를/JKO-?/NNG-하/?-(은는/ETM or 을를/ETM) 총
			 * 8가지가 말뭉치 상에 3회 이상 출현한 패턴 일 경우 ?/NNG-하/XSA-게/JKB를
			 * ?/NNG-하/XSV-게/EC로 교체
			 */
			if (words[i].contains("NNG")) {
				if (i > 0 && i + 2 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (i + 3 < words.length) {
							if (words[i + 2].equals("하/XSA")) {
								if (words[i + 3].equals("###/SPACE")) {
									if (i + 4 < words.length) {
										if (words[i + 4].equals("게/JKB")) {
											if (words[i - 1].equals("###/SPACE")) {
												if (i > 1) {
													if (words[i - 2].equals("을를/JKO")) {
														String cont;
														boolean find = false;
														for (int j = 0; j < postProcStr.length; j++) {
															cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
															if (time3(cont)) {
																find = true;
																break;
															}
														}
														if (find) {
															words[i + 2] = "하/XSV";
															words[i + 4] = "게/EC";
															s = MP_to_Str(words);
														}
													}
												}
											}
											else {
												if (words[i - 1].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (find) {
														words[i + 2] = "하/XSV";
														words[i + 4] = "게/EC";
														s = MP_to_Str(words);
													}
												}
											}
										}
									}
								}
								else {
									if (words[i + 3].equals("게/JKB")) {
										if (words[i - 1].equals("###/SPACE")) {
											if (i > 1) {
												if (words[i - 2].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (find) {
														words[i + 2] = "하/XSV";
														words[i + 3] = "게/EC";
														s = MP_to_Str(words);
													}
												}
											}
										}
										else {
											if (words[i - 1].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (find) {
													words[i + 2] = "하/XSV";
													words[i + 3] = "게/EC";
													s = MP_to_Str(words);
												}
											}
										}
									}
								}
							}
						}
					}
					else {
						if (words[i + 1].equals("하/XSA")) {
							if (words[i + 2].equals("###/SPACE")) {
								if (i + 3 < words.length) {
									if (words[i + 3].equals("게/JKB")) {
										if (words[i - 1].equals("###/SPACE")) {
											if (i > 1) {
												if (words[i - 2].equals("을를/JKO")) {
													String cont;
													boolean find = false;
													for (int j = 0; j < postProcStr.length; j++) {
														cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
														if (time3(cont)) {
															find = true;
															break;
														}
													}
													if (find) {
														words[i + 1] = "하/XSV";
														words[i + 3] = "게/EC";
														s = MP_to_Str(words);
													}
												}
											}
										}
										else {
											if (words[i - 1].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (find) {
													words[i + 1] = "하/XSV";
													words[i + 3] = "게/EC";
													s = MP_to_Str(words);
												}
											}
										}
									}
								}
							}
							else {
								if (words[i + 2].equals("게/JKB")) {
									if (words[i - 1].equals("###/SPACE")) {
										if (i > 1) {
											if (words[i - 2].equals("을를/JKO")) {
												String cont;
												boolean find = false;
												for (int j = 0; j < postProcStr.length; j++) {
													cont = words[i - 2] + " " + words[i] + " " + postProcStr[j];
													if (time3(cont)) {
														find = true;
														break;
													}
												}
												if (find) {
													words[i + 1] = "하/XSV";
													words[i + 2] = "게/EC";
													s = MP_to_Str(words);
												}
											}
										}
									}
									else {
										if (words[i - 1].equals("을를/JKO")) {
											String cont;
											boolean find = false;
											for (int j = 0; j < postProcStr.length; j++) {
												cont = words[i - 1] + " " + words[i] + " " + postProcStr[j];
												if (time3(cont)) {
													find = true;
													break;
												}
											}
											if (find) {
												words[i + 1] = "하/XSV";
												words[i + 2] = "게/EC";
												s = MP_to_Str(words);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return s;
	}

	public static String revise_parsing2(String line)
	{
		String[] words = line.split(" ###/SPACE ");
		String res = line;
		for (int i = 0; i < words.length; i++) {
			String[] cont = words[i].split(" ");
			String w = null;
			if (cont.length < 3)
				continue;
			if (checkJ(cont[cont.length - 1])) {
				w = combineWords(cont);
			}
			if (w != null) {
				w = w + "/NNG";
				if (checkJStr(w, cont[cont.length - 1])) {
					words[i] = w + " " + cont[cont.length - 1];
					res = transSpaceStr(words);
				}
			}
		}
		return res;
	}

	public static String revise_parsing4(String line) throws IOException
	{
		String[] words = line.split(" ");
		String s = line;
		if (words.length == 0)
			return s;
		for (int i = 0; i < words.length; i++) {
			/*
			 * )/SSC를 만날 경우 후속 형태소가 관계사 연속체일 경우
			 */
			if (words[i].contains(")")) {
				if (i + 1 < words.length) {
					String cont = null;
					if (words[i + 1].equals("###/SPACE")) {
						if (words.length > i + 2 && checkpredRel(words[i + 2])) {
							cont = words[i + 2];
							words[i + 2] = "";
							for (int j = i + 3; j < words.length; j++) {
								if (words[j].equals("###/SPACE")) {
									words[i + 1] = "";
									break;
								}
								cont += " " + words[j];
								words[j] = "";
							}
						}
					}
					else {
						if (checkpredRel(words[i + 1])) {
							cont = words[i + 1];
							words[i + 1] = "";
							for (int j = i + 2; j < words.length; j++) {
								if (words[j].equals("###/SPACE"))
									break;
								cont += " " + words[j];
								words[j] = "";
							}
						}
					}
					if (cont != null) {
						for (int j = i - 1; j >= 0; j--) {
							// if (words[j].equals("###/SPACE (/SSO")) {
							if (words[j].contains("###/SPACE") && words[j].contains("(")) {
								words[j - 1] = words[j - 1] + " " + cont;
								s = MP_to_Str(words);
								break;
							}
							else if (words[j].contains("(")) {
								if (j != 0) {
									if (words[j - 1].equals("###/SPACE")) {
										words[j - 2] = words[j - 2] + " " + cont;
										s = MP_to_Str(words);
										break;
									}
									else {
										words[j - 1] = words[j - 1] + " " + cont;
										s = MP_to_Str(words);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return s;
	}

	public static String revise_parsing3(String line) throws IOException
	{
		String[] words = line.split(" ");
		String s = line;
		if (words.length == 0)
			return s;
		for (int i = 0; i < words.length; i++) {
			/*
			 * 『/SY, 』/SY 기호들을 『/SSO, 』/SSC 변경
			 */
			if (words[i].equals("『/SY")) {
				words[i] = "『/SSO";
				for(int j = i; j < words.length; j++) {
					if (words[j].equals("』/SY")) {
						words[j] = "』/SSC";
						break;
					}
				}
				s = MP_to_Str(words);
			}
			/*
			 * ./SF .으로 시작하는 SY -> ./SF 삭제
			 */
			if (words[i].equals("./SF")) {
				if (i + 1 < words.length) {
					if (words[i + 1].equals("###/SPACE")) {
						if (words.length > i + 2 && words[i + 2].startsWith(".") && words[i + 2].contains("/SY")) {
							words[i + 1] = "";
							words[i] = "";
							s = MP_to_Str(words);
						}
					}
					else {
						if (words[i + 1].startsWith(".") && words[i + 1].contains("/SY")) {
							words[i] = "";
							s = MP_to_Str(words);
						}
					}
				}
			}
			/*
			 * (/SSO를 만날 경우 앞에 space bar를 추가하여 어절 분리
			 */
			if (words[i].contains("(")) {
				if (i > 0) {
					if (!words[i - 1].equals("###/SPACE")) {
						words[i] = "###/SPACE " + words[i];
						s = MP_to_Str(words);
					}
				}
			}
			/*
			 * 조사 다음에 조사가 나오지 않으면 무조건 스페이스로 어절 분할 어미 다음에 어미나 조사, VX, VCN, VCP,
			 * XSV, XSA, VV, VA가 나오지 않으면 무조건 어절 분할
			 */
			if (checkJ(words[i])) {
				if (i + 1 < words.length) {
					boolean find = false;
					if (!words[i + 1].equals("###/SPACE")) {
						if (!checkJ(words[i + 1]) && !checkS(words[i + 1])) {
							find = true;
						}
					}
					if (find) {
						words[i] = words[i] + " ###/SPACE";
						s = MP_to_Str(words);
						words = s.split(" ");
					}
				}
			}
			if (checkE(words[i])) {
				if (i + 1 < words.length) {
					boolean find = false;
					if (!words[i + 1].equals("###/SPACE")) {
						if (!checkJEV(words[i + 1])) {
							find = true;
						}
					}
					if (find) {
						words[i] = words[i] + " ###/SPACE";
						s = MP_to_Str(words);
						words = s.split(" ");
					}
				}
			}
			/*
			 * 이라고/JKQ -> 이/VCP 라고/JKQ 체언+라고/JKQ -> 체언 + 이/VCP 라고/JKQ
			 */
			if (words[i].equals("이라고/JKQ")) {
				words[i] = "이/VCP 라고/JKQ";
				s = MP_to_Str(words);
				words = s.split(" ");
			}
			if (words[i].equals("라고/JKQ")) {
				if (i > 0) {
					if (words[i - 1].equals("###/SPACE")) {
						if (checkSubst(words[i - 2])) {
							words[i] = "이/VCP 라고/JKQ";
							s = MP_to_Str(words);
							words = s.split(" ");
						}
					}
					else {
						if (checkSubst(words[i - 1])) {
							words[i] = "이/VCP 라고/JKQ";
							s = MP_to_Str(words);
							words = s.split(" ");
						}
					}
				}
			}
		}
		return s;
	}

	private static boolean time1(String cont)
	{
		int foc = fjni.NLPSearch(cont);
		if (foc > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	private static boolean time3(String cont)
	{
		int foc = fjni.NLPSearch(cont);
		if (foc > 2) {
			return true;
		}
		else {
			return false;
		}
	}
}
