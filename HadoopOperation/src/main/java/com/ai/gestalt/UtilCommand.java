package com.ai.gestalt;

import com.clunix.NLP.sentence_analyzer.Commander;

public class UtilCommand {
	public static boolean command(String[] arg, Student st){
		if (arg[0].equals("-S")) {
			mainf.printSUCC(arg[1], st);
			return true;
		}
		else if (arg[0].equals("-P")) {
			mainf.printPREV(arg[1], st);
			return true;
		}
		else if (arg[0].equals("-r")) {
			 mainf.printsuccR(arg[1], arg[2], st);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String choosefn(String input){
		Commander cmnd = new Commander();
		if (input.equals("1")) {
			System.out.println("read shorts.txt.10");
			return "shorts.txt.10";
		} else if (input.equals("2")) {
			System.out.println("read whole_mecab_pr.txt");
			return "whole_mecab_pr.txt";
		} else if (input.equals("3")) {
			System.out.println("read article_mecab_pr.txt");
			return "article_mecab_pr.txt";
		} else if (input.equals("4")) {
			System.out.println("read article20K.txt");
			return "article20K.txt";
		} else if (input.equals("5")) {
			System.out.println("read " + cmnd.fn);
			return cmnd.fn;
		} else if (input.equals("6")) {
			System.out.println("read 4debug.data");
			return "4debug.data";
		} else if (input.equals("7")) {
			System.out.println("read test2.txt");
			return "test2.txt";
		} else {
			System.out.println("wrong option!");
			System.exit(1);
			return null;
		}
	}
}
