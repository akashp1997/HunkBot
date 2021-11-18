import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class HunkBotDup {
    FastReader reader = new FastReader();
    int boardRows, boardCols;
    int board[][];

    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader()
        {
            br = new BufferedReader(
                    new InputStreamReader(System.in));
        }

        String next()
        {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        String nextLine()
        {
            String str = "";
            try {
                str = br.readLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    public static void main(String args[]){
         HunkBotDup mrBot = new HunkBotDup();
         mrBot.runProgram();
    }

    private void runProgram() {
        while(true) {

        }
    }

    private void boardInitialize(int r, int c) {
        board = new int[r][c];
        boardCols = c;
        boardRows = r;
        System.out.println('0');
    }

}
