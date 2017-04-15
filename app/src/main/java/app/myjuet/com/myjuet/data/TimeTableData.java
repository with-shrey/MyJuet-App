package app.myjuet.com.myjuet.data;

/**
 * Created by Shrey on 12-Apr-17.
 */

public class TimeTableData implements java.io.Serializable {
    private int[] PosNine = new int[2];
    private int[] PosTen = new int[2];
    private int[] PosEleven = new int[2];
    private int[] PosTwelve = new int[2];
    private int[] PosTwo = new int[2];
    private int[] PosThree = new int[2];
    private int[] PosFour = new int[2];
    private int[] PosFive = new int[2];

    private String LocNine;
    private String LocTen;
    private String LocEleven;
    private String LocTwelve;
    private String LocTwo;
    private String LocThree;
    private String LocFour;
    private String LocFive;


    public TimeTableData() {
        PosNine = new int[]{0, 0};
        PosTen = new int[]{0, 0};
        PosEleven = new int[]{0, 0};
        PosTwelve = new int[]{0, 0};
        PosTwo = new int[]{0, 0};
        PosThree = new int[]{0, 0};
        PosFour = new int[]{0, 0};
        PosFive = new int[]{0, 0};
        LocNine = "";
        LocTen = "";
        LocEleven = "";
        LocTwelve = "";
        LocTwo = "";
        LocThree = "";
        LocFour = "";
        LocFive = "";
    }

    public int getPosEleven() {
        return PosEleven[0];
    }

    public void setPosEleven(int posEleven) {
        PosEleven[0] = posEleven;
    }

    public int getPosFive() {
        return PosFive[0];
    }

    public void setPosFive(int posFive) {
        PosFive[0] = posFive;
    }

    public int getPosFour() {
        return PosFour[0];
    }

    public void setPosFour(int posFour) {
        PosFour[0] = posFour;
    }

    public int getPosNine() {
        return PosNine[0];
    }

    public void setPosNine(int posNine) {
        PosNine[0] = posNine;
    }

    public int getPosTen() {
        return PosTen[0];
    }

    public void setPosTen(int posTen) {
        PosTen[0] = posTen;
    }

    public int getPosThree() {
        return PosThree[0];
    }


    public void setPosThree(int posThree) {
        PosThree[0] = posThree;
    }

    public int getPosTwelve() {
        return PosTwelve[0];
    }

    public void setPosTwelve(int posTwelve) {
        PosTwelve[0] = posTwelve;
    }

    public int getPosTwo() {
        return PosTwo[0];
    }

    public void setPosTwo(int posTwo) {
        PosTwo[0] = posTwo;
    }

    public int getTypeFive() {
        return PosFive[1];
    }

    public void setTypeFive(int posFive) {
        PosFive[1] = posFive;
    }

    public int getTypeFour() {
        return PosFour[1];
    }

    public void setTypeFour(int posFour) {
        PosFour[1] = posFour;
    }

    public int getTypeNine() {
        return PosNine[1];
    }

    public void setTypeNine(int posNine) {
        PosNine[1] = posNine;
    }

    public int getTypeTen() {
        return PosTen[1];
    }

    public void setTypeTen(int posTen) {
        PosTen[1] = posTen;
    }

    public int getTypeThree() {
        return PosThree[1];
    }

    public void setTypeThree(int posThree) {
        PosThree[1] = posThree;
    }

    public int getTypeTwelve() {
        return PosTwelve[1];
    }

    public void setTypeTwelve(int posTwelve) {
        PosTwelve[1] = posTwelve;
    }

    public int getTypeTwo() {
        return PosTwo[1];
    }

    public void setTypeTwo(int posTwo) {
        PosTwo[1] = posTwo;
    }

    public String getLocEleven() {
        return LocEleven;
    }

    public void setLocEleven(String locEleven) {
        LocEleven = locEleven;
    }

    public int getTypeEleven() {
        return PosEleven[1];
    }

    public void setTypeEleven(int posEleven) {
        PosEleven[1] = posEleven;
    }

    public String getLocFive() {
        return LocFive;
    }

    public void setLocFive(String locFive) {
        LocFive = locFive;
    }

    public String getLocFour() {
        return LocFour;
    }

    public void setLocFour(String locFour) {
        LocFour = locFour;
    }

    public String getLocNine() {
        return LocNine;
    }

    public void setLocNine(String locNine) {
        LocNine = locNine;
    }

    public String getLocTen() {
        return LocTen;
    }

    public void setLocTen(String locTen) {
        LocTen = locTen;
    }

    public String getLocThree() {
        return LocThree;
    }

    public void setLocThree(String locThree) {
        LocThree = locThree;
    }

    public String getLocTwelve() {
        return LocTwelve;
    }

    public void setLocTwelve(String locTwelve) {
        LocTwelve = locTwelve;
    }

    public String getLocTwo() {
        return LocTwo;
    }

    public void setLocTwo(String locTwo) {
        LocTwo = locTwo;
    }
}
