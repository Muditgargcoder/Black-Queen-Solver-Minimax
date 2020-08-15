
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * CardGame
 */
public class cg2 {

    int players;
    int[][] mem;
    int[][][] playerCard; // [player][type][number]
    int myID;
    Scanner s;
    int cardPerPlayer;
    int tricksPlayed = 0;
    // Index of type start from 0
    // [] Hukum 0
    // [] Paan 1
    // [] Eat 2
    // [] Chiddi 3
    // Index of card start from 0
    // Index of playerId from 0

    // -1 - Gone
    // 0 - Not having
    // 1 - Probably having
    // 2 - Having

    public cg2(int players, int myID) {
        s = new Scanner(System.in);
        this.players = players;
        this.cardPerPlayer = 52 / players;
        this.playerCard = new int[players][4][cardPerPlayer + 2]; // [no of players][Card types (4)][cardsPerPlayer + 2]
        this.myID = myID;
        for (int i = 0; i < playerCard.length; i++) {
            for (int j = 0; j < playerCard[0].length; j++) {
                for (int j2 = 2; j2 < playerCard[0][0].length; j2++) {
                    playerCard[i][j][j2] = 1;
                }
            }
        }

    }

    public void has(int player, int type, int no, int[][][] mem) { // Used for me, i am having this card, and others
                                                                   // don't;
        for (int i = 0; i < players; i++) {
            if (i == player) {
                mem[i][type][no] = 2;
                continue;
            }
            mem[i][type][no] = 0;
        }
    }

    public int[] get() { // get input card at Starting
        String t = s.nextLine().trim();
        String[] a = t.split(" ");
        int[] input = new int[2];
        switch (a[0]) {
            case "h":
                input[0] = 0;
                break;
            case "p":
                input[0] = 1;
                break;
            case "e":
                input[0] = 2;
                break;
            case "c":
                input[0] = 3;
                break;
            default:
                System.out.println("Error in syntax, Put Again");
                return get();
        }
        try {
            input[1] = Integer.parseInt(a[1]);
        } catch (Exception e) {
            System.out.println("Error in syntax, Put Again");
            return get();
            // TODO: handle exception
        }

        return input;
    }

    public boolean SafeCount(int type, int[][][] mem) {
        int c = 0;
        for (int j = 0; j < 15; j++) {
            if (mem[myID][type][j] == -1) {
                c += 1;
            }
        }

        if (c >= 4) {
            return true;
        }
        return false;
    }

    public boolean[] remove(int[] inp, int[][][] mem, int curplayer, int startCardType) {

        boolean[] t = new boolean[4];
        for (int i = 0; i < players; i++) {
            if (mem[i][inp[0]][inp[1]] == 1) {
                t[i] = true;
            }
            mem[i][inp[0]][inp[1]] = -1;
        }

        if (inp[0] != startCardType) {
            for (int i = 0; i < 15; i++) {
                mem[curplayer][startCardType][i] = -1;
            }
        }

        return t;
    }

    public void formatMyDeck() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < cardPerPlayer + 2; j++) {
                if (playerCard[myID][i][j] == 1) {
                    playerCard[myID][i][j] = 0;
                }
            }
        }
    }

    public boolean compTwo(int type1, int type2, int no1, int no2) { // true if 1 gets cards
        if (type1 != type2) {
            return true;
        } else if (no1 > no2) {
            return true;
        } else {
            return false;
        }
    }

    public int[] onePlay(int[][] ar, int first) { // returning [player who gets, No. of red Paan, Queen or not]
        int winTemp = first;
        for (int i = 0; i < players - 1; i++) {
            if (compTwo(ar[winTemp][0], ar[(first + i + 1) % players][0], ar[winTemp][1],
                    ar[(first + i + 1) % players][1])) {
            } else {
                winTemp = (first + i + 1) % players;
            }
        }
        int redPaan = 0;
        int Q = 0;
        for (int i = 0; i < players; i++) {
            if (ar[i][0] == 1) {
                redPaan++;
            } else if (ar[i][0] == 0 && ar[i][1] == 12) {
                Q = 1;
            }
        }

        return new int[] { winTemp, redPaan, Q };
    }

    int[][] defaultStartCards = new int[][] { { 0, 9 }, { 0, 7 }, { 0, 12 }, { 0, 4 }, { 1, 5 }, { 1, 6 }, { 1, 14 },
            { 1, 11 }, { 2, 13 }, { 2, 10 }, { 2, 9 }, { 3, 7 }, { 3, 4 } };

    public void start() {

        System.out.println("Enter your cards: ");

        int[] inp = new int[2];

        if (s.nextLine().trim().equals("ds")) {
            for (int i = 0; i < 13; i++) {
                has(myID, defaultStartCards[i][0], defaultStartCards[i][1], playerCard);
            }
        } else {
            for (int i = 0; i < cardPerPlayer; i++) {
                System.out.println("Entry " + (i + 1) + ": ");
                inp = get();
                has(myID, inp[0], inp[1], playerCard);

            }
        }

        formatMyDeck(); // My Deck has only my cards now
        // printMem(playerCard, 1);
        // Start Playing
        int curplayer = 0;
        int[][] onePla; // OnePlay Input
        int[] points = new int[players]; // Total Points at last
        int[] k; // onePlay output
        int startCardType = -1;
        for (int i = 0; i < cardPerPlayer; i++) { // There will be 13 onePlays
            onePla = new int[players][2];
            tricksPlayed = i;
            for (int j = 0; j < players; j++) {
                if (curplayer == myID) {
                    System.out.println("Your turn");
                    ArrayList<int[]> l = new ArrayList<>();

                    int[] r = afterOnePlay(myID, new int[players][2], playerCard.clone(), l, -1, myID, i);

                    // System.out.println("test");
                    for (int b = 0; b < l.size(); b++) {
                        System.out.println(Arrays.toString(l.get(b)));
                    }

                    // printList(l);

                    System.out.println("Enter your move: ");
                    inp = get();
                    onePla[curplayer][0] = inp[0];
                    onePla[curplayer][1] = inp[1];
                    if (j == 0) {
                        startCardType = inp[0];
                    }
                    remove(inp, playerCard, curplayer, startCardType);
                } else {
                    System.out.println("Player " + curplayer + " ?");
                    inp = get();
                    onePla[curplayer][0] = inp[0];
                    onePla[curplayer][1] = inp[1];
                    if (j == 0) {
                        startCardType = inp[0];
                    }
                    remove(inp, playerCard, curplayer, startCardType);
                }
                curplayer = (curplayer + 1) % players;
                // System.out.println(startCardType);
            }
            k = onePlay(onePla, curplayer); // HERE, curplayer is already the startplayer as the loop has ended.
            points[k[0]] = k[1] + k[2] * 12; // Points Added
            curplayer = k[0];
            System.out.println("Player " + k[0] + " got " + points[k[0]] + " points");
            System.out.println("New Turn: ");
            // showAllCardsOfPlayers();
        }

        System.out.println("Game Finished, Showing Points: ");

        for (int i = 0; i < players; i++) {
            System.out.println("Points of " + i + " " + points[i]);
        }
        tricksPlayed = 0;

    }

    public int[] afterOnePlay(int curplayer, int[][] inp, int[][][] mem, ArrayList<int[]> l, int startCardType,
            int startplayer, int count) {
        // l will store every card played by me and its points
        int[][] np = inp.clone();
        int[][][] m = mem.clone();
        int[] max = new int[3];
        max[2] = -1;
        int[] t = new int[3];
        boolean hassameCard = checkSameCard(startCardType, curplayer, m);
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < cardPerPlayer + 2; j++) {
                if ((m[curplayer][i][j] == 1 || m[curplayer][i][j] == 2)
                        || ((hassameCard == true && i == startCardType) || (hassameCard == false))) {
                    np[curplayer][0] = i;
                    np[curplayer][1] = j;
                    if (startplayer == curplayer) {
                        startCardType = i;
                    }
                    boolean[] b = remove(new int[] { i, j }, m, curplayer, startCardType);
                    if (curplayer == startplayer) {
                        t = afterOnePlay((curplayer + 1) % players, np, m, l, i, startplayer, count);
                        max[2] = t[2];
                        max[0] = i;
                        max[1] = j;
                        l.add(max.clone());
                    } else if ((curplayer + 1) % players != startplayer) {
                        t = afterOnePlay((curplayer + 1) % players, np, m, l, startCardType, startplayer, count);
                        if (t[2] > max[2]) {
                            max[2] = t[2];
                            max[0] = i;
                            max[1] = j;
                        }

                    } else {

                        int[] k = onePlay(np, startplayer);
                        int maxx = 0;
                        if (((count > 6 && count != tricksPlayed + 2) || (count != tricksPlayed)) && count != 12) {
                            ArrayList<int[]> al = new ArrayList<>();
                            int[] after = afterOnePlay(k[0], inp, m, al, -1, k[0], count + 1);
                            for (int n = 0; n < al.size(); n++) {
                                if (maxx < al.get(n)[2]) {
                                    maxx = al.get(n)[2];
                                }
                            }
                        }

                        if (k[0] == myID) { // count points if it effects me
                            if ((k[1] + k[2] * 12) + maxx > max[2]) {
                                max[2] = k[1] + k[2] * 12 + maxx;
                                max[0] = i;
                                max[1] = j;
                            }
                        }

                    }
                    if (curplayer == myID) {
                        has(curplayer, i, j, m);
                    } else {
                        undoremove(b, i, j, m);
                    }
                }
            }
        }

        return max;
    }
    // [] Hukum 0
    // [] Paan 1
    // [] Eat 2
    // [] Chiddi 3

    public int[] minMax(int curplayer, int[][][] mem, int count, int point) { // Starting the Oneplay for curplayer and
                                                                              // predicting
        // returning bestCard [type,no,Points] with respect to the curplayer

        int[] bestCard = new int[3];
        int[] t = bestCard.clone();
        int[][] onePlayInp = new int[4][2];
        int m[][][];
        if (curplayer == myID) { // Choose minimum points of the (Maximum Points with every Card i can play)
            bestCard[2] = 10000;
            for (int i = 0; i < 4; i++) {
                for (int j = 2; j < 15; j++) {
                    if (mem[curplayer][i][j] == 1 || mem[curplayer][i][j] == 2) {
                        m = mem.clone();
                        onePlayInp[curplayer][0] = i;
                        onePlayInp[curplayer][1] = j;
                        remove(new int[] { i, j }, m, curplayer, i);
                        t = maxPlay(curplayer, curplayer, count, onePlayInp, m, point); // Card, type
                        if (bestCard[2] > t[2]) {
                            bestCard = t;
                        }
                    }

                }
            }
        } else { // Choose maximum points (these points are relative to me) of the (minimum
                 // Points with every card other can play)
            bestCard[2] = -10000;
            for (int i = 0; i < 4; i++) {
                for (int j = 2; j < 15; j++) {
                    if (mem[curplayer][i][j] == 1 || mem[curplayer][i][j] == 2) {
                        m = mem.clone();
                        onePlayInp[curplayer][0] = i;
                        onePlayInp[curplayer][1] = j;
                        remove(new int[] { i, j }, m, curplayer, i);
                        t = minPlay(curplayer, curplayer, count, onePlayInp, m); // Card, type
                        if (bestCard[2] < t[2]) {
                            bestCard = t;
                        }
                    }

                }
            }
        }
        bestCard[2] += point;
        return bestCard;
    }

    public int[] maxPlay(int startplayer, int curplayer, int count, int[][] OnePlayInp, int[][][] mem, int point) {
        int[][] onePlayInp = new int[4][2];
        int m[][][];
        int[] t = new int[3];
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < 15; j++) {
                if (mem[curplayer][i][j] == 1) {
                    m = mem.clone();
                    if (((curplayer + 1) % players) == myID) {
                        int[] ar = onePlay(OnePlayInp, startplayer);
                        int[] k = new int[] { 0, 0, 0 };
                        if (count != 12) {
                            k = minMax(ar[0], m, count + 1, point + ar[1] + ar[2] * 12);
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////// LEFT HERE ///////////
                    }
                }
            }
        }
    }

    public int[] minPlay(int startplayer, int curplayer, int count, int[][] OnePlayInp, int[][][] mem) {

    }

    public void undoremove(boolean[] hasBefore, int type, int num, int[][][] mem) { // those who might have cards before
        for (int i = 0; i < players; i++) {
            if (hasBefore[i] == true) {
                mem[i][type][num] = 1;
            }
        }
    }

    public boolean checkSameCard(int type, int player, int[][][] mem) {
        if (type == -1) {
            return false;
        }
        for (int j = 2; j < cardPerPlayer + 2; j++) {
            if (mem[player][type][j] == 1 || mem[player][type][j] == 2) {
                return true;
            }
        }
        return false;
    }

    public void printMem(int[][][] ar, int playerId) {
        System.out.println("Cards of Player " + playerId);
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < 15; j++) {
                if (ar[playerId][i][j] == 1 || ar[playerId][i][j] == 2) {
                    String typeee = (i == 0 ? "H" : i == 1 ? "P" : i == 2 ? "E" : "C");
                    System.out.println(typeee + " " + j);
                }
            }
        }
        System.out.println();
    }

    public void showAllCardsOfPlayers() {
        printMem(playerCard, 0);
        printMem(playerCard, 1);
        printMem(playerCard, 2);
        printMem(playerCard, 3);
    }

    public void printList(ArrayList<int[]> l) {

        ArrayList<int[]> p = (ArrayList<int[]>) l.clone();
        ArrayList<int[]> t = new ArrayList<>();
        int j = 0;
        while (!p.isEmpty()) {
            int min[] = new int[3];
            min[2] = 100;
            int minind = 0;
            for (int k = 0; k < p.size(); k++) {
                if (p.get(k)[2] == min[2] && p.get(k)[0] == min[0] && p.get(k)[1] > min[1]) {
                    min = p.get(k);
                    minind = k;
                } else if (p.get(k)[2] < min[2]) {
                    min = p.get(k);
                    minind = k;
                }
            }
            t.add(min);
            p.remove(minind);
        }

        printListHelper(t);

    }

    public void printListHelper(ArrayList<int[]> t) {
        String type = "";
        int[] m = new int[3];
        for (int i = 0; i < t.size(); i++) {
            m = t.get(i);
            type = (m[0] == 0 ? "H" : m[0] == 1 ? "P" : m[0] == 2 ? "E" : "C");
            System.out.println(type + " " + m[1] + " " + m[2]);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Scanner s = new Scanner(System.in);
        cg2 c = new cg2(4, 0);
        c.start();

        // h 9
        // h 7
        // h 12
        // h 4
        // p 5
        // p 6
        // p 14
        // p 11
        // e 13
        // e 10
        // e 9
        // c 7
        // c 4

        // System.out.println(Arrays.toString(c.onePlay(new int[][] { { 1, 12 }, { 0, 2
        // }, { 1, 10 }, { 1, 9 } }, 0)));

        // [] Hukum 0
        // [] Paan 1
        // [] Eat 2
        // [] Chiddi 3
    }

    // public int[] solver(int startPlayerId, int curplayer, int[][] oneP, int[][][]
    // memory, int count) { // version 2
    // // int - [type, number, points]

    // int[][] one = oneP.clone(); // OnePlay Input
    // int[] k; // onePlay points
    // int j = curplayer;
    // int[][][] m = memory.clone();
    // int[] minCard = new int[3];
    // int[] t;
    // ArrayList<int[]> store = new ArrayList<>();
    // minCard[2] = 100000;
    // t = new int[3];
    // for (int i = 0; i < 4; i++) { // 4 types of cards
    // for (int l = 2; l < cardPerPlayer + 2; l++) {
    // if (m[j][i][l] == 1 || m[j][i][l] == 2) {
    // one[j][0] = i;
    // one[j][1] = l;
    // remove(new int[] { i, l }, m);
    // if ((j + 1) % players != startPlayerId) {
    // t = solver(startPlayerId, (j + 1) % players, one, m, count);
    // one[(j + 1) % players][0] = t[0];
    // one[(j + 1) % players][1] = t[1];
    // }
    // // } else if ((j + 2) % players != startPlayerId) {
    // // t = solver(startPlayerId, (j + 2) % players, one, m, count);
    // // one[(j + 1) % players][0] = t[0];
    // // one[(j + 1) % players][1] = t[1];
    // // } else if ((j + 3) % players != startPlayerId) {
    // // t = solver(startPlayerId, (j + 3) % players, one, m, count);
    // // one[(j + 1) % players][0] = t[0];
    // // one[(j + 1) % players][1] = t[1];
    // // }

    // k = onePlay(one, startPlayerId);
    // int curpoint = 0;
    // if (count != cardPerPlayer - 1) { // cardPerPlayer - 1 = 12
    // t = solver(k[0], k[0], new int[4][2], m, count++);
    // curpoint = t[2];
    // }

    // if (k[0] == myID) {
    // curpoint += k[1] + k[2] * 12;
    // }
    // store.add(new int[] { i, l, curpoint });
    // if (curpoint < minCard[2]) {
    // minCard[2] = curpoint;
    // minCard[0] = i;
    // minCard[1] = l;
    // }
    // }
    // }
    // }

    // return minCard;
    // }

    // public int[] suggest(int playerid, int count) {

    // int point[] = new int[3];
    // int min = 100000;
    // int type = 0;
    // int num = 0;
    // for (int i = 0; i < 4; i++) {
    // for (int j = 0; j < 15; j++) {
    // if (playerCard[playerid][i][j] == 2 || playerid == 1) {
    // point = solver(playerid, playerid, i, j, new int[4][2], playerCard.clone(),
    // count);
    // if (point[2] < min) {
    // min = point[2];
    // type = i;
    // num = j;
    // }
    // }
    // }
    // }

    // return new int[] { type, num, min };
    // }

    // public int[] solver(int startPlayerId, int curplayer, int p, int q, int[][]
    // oneP, int[][][] memory, int count) {
    // // int - [type, number, points]

    // int[][] one = oneP.clone(); // OnePlay Input
    // int[] k; // onePlay points
    // int j = curplayer;
    // int[][][] m = memory.clone();
    // int pcount = 0;

    // while (pcount < 4 && (curplayer != startPlayerId || j == curplayer)) {
    // if (j == curplayer) {
    // one[curplayer][0] = p;
    // one[curplayer][1] = q;
    // remove(oneP[curplayer], m);
    // } else {
    // int[] minCard = new int[3];
    // minCard[2] = 100000;
    // int[] t = new int[3];
    // for (int i = 0; i < 4; i++) {
    // for (int l = 0; l < 15; l++) {
    // if (m[j][i][l] == 1 || m[j][i][l] == 2) {
    // t = solver(startPlayerId, j, i, l, one, m, count);
    // if (t[2] < minCard[2]) {
    // minCard = t;
    // }
    // }
    // }
    // }
    // one[j][0] = minCard[0];
    // one[j][1] = minCard[1];
    // }
    // j = (j + 1) % players;
    // pcount++;
    // }
    // k = onePlay(one, startPlayerId);

    // if (count == 13) {
    // if (k[2] == myID) {
    // return new int[] { p, q, k[1] + k[2] * 12 };
    // } else {
    // return new int[] { p, q, 0 };
    // }

    // }

    // int[] n = suggest(k[0], count);
    // // solver(k[0], k[0], p, q, new int[4][2], m);

    // if (k[0] == myID) {
    // return new int[] { n[0], n[1], k[1] + k[2] * 12 + n[2] };
    // }
    // return n;
    // }

    // public int recSolver(int id, int count, int p, int q, int[][][] memory, int
    // startPlayerId, int[][] oneP,
    // int Overallpoints) {
    // int curplayer = id;
    // int[][] one = oneP.clone(); // OnePlay Input
    // int[] points = new int[players];
    // int[] k; // onePlay points
    // int[] inp = new int[2]; // input

    // int j = id;
    // int[][][] m = memory.clone();
    // int pcount = 0;
    // while (pcount < 4 && (curplayer == startPlayerId && j != id)) {
    // if (j == id) {
    // oneP[curplayer][0] = p;
    // oneP[curplayer][1] = q;
    // remove(inp, m);
    // } else {
    // for (int i = 0; i < 4; i++) {
    // for (int l = 0; l < 15; l++) {
    // if (m[curplayer][i][l] == 1 || m[curplayer][i][l] == 2) {
    // recSolver(curplayer, count, i, l, m, startPlayerId, oneP);
    // }
    // }
    // }

    // }
    // curplayer = (curplayer + 1) % players;
    // j++;
    // pcount++;
    // }
    // k = onePlay(oneP, startPlayerId);
    // if (k[0] == myID) {
    // Overallpoints += k[1] + k[2] * 12;
    // }

    // // recSolver(k[0], count++, p, q, memory, startPlayerId, oneP) curplayer =
    // k[0];
    // return 0;

    // }

}