package com.ai.gestalt;

import com.clunix.NLP.sentence_analyzer.StrStr;

public class misc {
	private StrStr associatedForm(String label) {
		StrStr ret = null;
		if (label.startsWith("*(")) {
			int level = 1;
			String x = "";
			String xx = null;
			String t = label.substring(2);
			for (int i = 0; i < t.length(); i++) {
				if (t.charAt(i) == ')')
					level--;
				if (level == 0) {
					xx = t.substring(i + 2);
					break;
				}
				x += t.charAt(i);
			}
			if (x.equals(xx))
				return new StrStr(Student.ba(x), x);
		} else if (label.endsWith(")*")) {
			int level = 1;
			String x = "";
			String xx = null;
			String t = label.substring(0, label.length() - 2);
			for (int i = t.length() - 1; i >= 0; i--) {
				if (t.charAt(i) == '(')
					level--;
				if (level == 0) {
					xx = t.substring(0, i - 1);
					break;
				}
				x = t.charAt(i) + x;
			}
			if (x.equals(xx))
				return new StrStr(x, Student.fa(x));
		}
		return null;
	}
	

}
