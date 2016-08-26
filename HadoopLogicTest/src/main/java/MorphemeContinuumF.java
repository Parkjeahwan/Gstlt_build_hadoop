import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by parkjh on 16. 4. 28.
 */
public class MorphemeContinuumF {
    public static void main (String[] arg) throws IOException {
        String filepath1 = "/shr/data/corpus_1000_article.txt";
        BufferedReader br = new BufferedReader(new FileReader(filepath1));
        List<String> list = new ArrayList<String>();

        String str;
        int linenum = 0;
        while ((str = br.readLine()) != null && linenum < 1999) {
            linenum++;
            if (linenum < 1999) continue;

            System.out.println(str);
            String m[] = str.split(" ");
            int u = 1;
            /*for (int k0 = 1; k0 <= u; k0++) {
                //HashSet <String> checkm1 = new HashSet <String> ();
                for (int i = 0; i < m.length - k0 + 1; i++) {
                    //HashSet <String> checkm2 = new HashSet <String> ();
                    String m1 = "";
                    for (int ii = i; ii < i + k0; ii++) m1 += m1.equals("") ? m[ii] : " " + m[ii];

                    //if (checkm1.contains(m1)) continue;
                    //else checkm1.add(m1);

                    for (int k1 = 1; k1 <= u; k1++) {
                        String m2 = "";
                        for (int j = i + k0; i < m.length - k0 - k1 + 1 && j < i + k0 + k1; j++) m2 += m2.equals("") ? m[j] : " " + m[j];
                        if(m2.equals("")) continue;

                        //if (m1.equals(m2) || checkm2.contains(m2)) continue;
                        //else checkm2.add(m2);

                        String keys = m1 + " ##SP " + m2;
                        list.add(keys);
                    }
                }
            }*/

            for (int k0=1;k0<=u;k0++) {
                HashSet<String> checkm1 = new HashSet <String> ();
                for (int i=0;i<m.length-k0+1;i++) {
                    HashSet <String> checkm2 = new HashSet <String> ();
                    String m1 = "";
                    for (int ii=i;ii<i+k0;ii++) m1 += m1.equals("")? m[ii] : " "+m[ii];

                    if (checkm1.contains(m1)) continue;
                    else checkm1.add(m1);

                    for (int k1=1;k1<=u;k1++) {

                        for (int j=i+k0;j<m.length-k1+1;j++) {
                            String m2 = "";
                            for (int jj=j;jj<j+k1;jj++) m2 += m2.equals("")? m[jj]:" "+m[jj];

                            if (m1.equals(m2) || checkm2.contains(m2)) continue;
                            else checkm2.add(m2);

                            String keys = m1 + " ##SP " + m2;
                            list.add(keys);
                        }
                    }
                }
            }

        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + " " + list.get(i));
        }
    }
}
