import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjh on 16. 4. 28.
 */
public class MorphemeCountF {
    public static void main (String[] arg) throws IOException {
        String filepath1 = "/shr/data/corpus_1000_article.txt";
        BufferedReader br = new BufferedReader(new FileReader(filepath1));
        List<String> list = new ArrayList<String>();

        String str;
        int linenum = 0;
        while ((str = br.readLine()) != null && linenum < 2000) {
            linenum++;
            if (linenum < 1999) continue;

            System.out.println(str);
            String m[] = str.split(" ");
            int u=3;
            for (int k0=1;k0<=u;k0++) {
                for (int i=0;i<m.length-k0+1;i++) {
                    String m1 = "";
                    for (int ii=i;ii<i+k0;ii++) m1 += m1.equals("")? m[ii] : " "+m[ii];
                    String keys = m1;
                    list.add(keys);
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + " " + list.get(i));
        }
    }
}
