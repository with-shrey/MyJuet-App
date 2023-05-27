package app.myjuet.com.myjuet.data;

import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jsoup.select.Elements;

import java.util.Locale;
import java.util.Map;

@Entity
public class ExamMarks {
    String id;
    @NonNull
    @PrimaryKey
    String subjectCode;
    String subjectName;
    Integer Test1;
    Integer Test2;
    Integer Test3;
    String T1;
    String T2;
    String T3;
    String P1;
    Integer P1_TOTAL;
    String P2;
    Integer P2_TOTAL;

    @Ignore
    public ExamMarks(Elements columns, SparseArray<Exam> map, Map<Exam, Integer> totalMarks) {
        this.Test1 = 15;
        this.Test2 = 25;
        this.Test3 = 35;
        for (int i = 0; i < columns.size(); i++) {
            if (i == 0) {
                this.id = columns.get(i).text();
            } else if (i == 1) {
                String[] strings = TextUtils.split(columns.get(i).text(), "- ");
                this.subjectCode = strings[strings.length - 1];
                this.subjectName = columns.get(i).text();
            } else if (map.get(i) == Exam.TEST1) {
                this.Test1 = totalMarks.get(Exam.TEST1);
                this.T1 = columns.get(i).text();
            }else if (map.get(i) == Exam.T1){
                this.Test1 = totalMarks.get(Exam.T1);
                this.T1 = columns.get(i).text();
            }else if (map.get(i) == Exam.TEST2){
                this.Test2 = totalMarks.get(Exam.TEST2);
                this.T2 = columns.get(i).text();
            }else if (map.get(i) == Exam.T2){
                this.Test2 = totalMarks.get(Exam.T2);
                this.T2 = columns.get(i).text();
            }else if (map.get(i) == Exam.TEST3) {
                this.Test3 = totalMarks.get(Exam.TEST3);
                this.T3 = columns.get(i).text();
            } else if (map.get(i) == Exam.T3) {
                this.Test3 = totalMarks.get(Exam.T3);
                this.T3 = columns.get(i).text();
            } else if (map.get(i) == Exam.P1) {
                this.P1 = columns.get(i).text();
                this.P1_TOTAL = totalMarks.get(Exam.P1);
            } else if (map.get(i) == Exam.P2) {
                this.P2 = columns.get(i).text();
                this.P2_TOTAL = totalMarks.get(Exam.P2);
            }
        }
    }

    public Integer getTest3() {
        return this.Test3 == null ? 35 : this.Test3;
    }

    public void setTest3(Integer test3) {
        Test3 = test3;
    }

    public Integer getP1_TOTAL() {
        return P1_TOTAL;
    }

    public void setP1_TOTAL(Integer p1_TOTAL) {
        P1_TOTAL = p1_TOTAL;
    }

    public Integer getP2_TOTAL() {
        return P2_TOTAL;
    }

    public ExamMarks() {
    }

    public void setP2_TOTAL(Integer p2_TOTAL) {
        P2_TOTAL = p2_TOTAL;
    }

    public Integer getTest1() {
        return this.Test1 == null ? 15 : this.Test1;
    }

    public void setTest1(Integer test1) {
        Test1 = test1;
    }

    public Integer getTest2() {
        return this.Test2 == null ? 25 : this.Test2;
    }

    public void setTest2(Integer test2) {
        Test2 = test2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(@NonNull String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public String getT2() {
        return T2;
    }

    public void setT2(String t2) {
        T2 = t2;
    }

    public String getT3() {
        return T3;
    }

    public void setT3(String t3) {
        T3 = t3;
    }

    public String getP1() {
        return P1;
    }

    public void setP1(String p1) {
        P1 = p1;
    }

    public String getP2() {
        return P2;
    }

    public void setP2(String p2) {
        P2 = p2;
    }

    @Override
    public String toString() {
        return "ExamMarks{" +
                "id='" + id + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", Test1=" + Test1 +
                ", Test2=" + Test2 +
                ", Test3=" + Test3 +
                ", T1='" + T1 + '\'' +
                ", T2='" + T2 + '\'' +
                ", T3='" + T3 + '\'' +
                ", P1='" + P1 + '\'' +
                ", P1_TOTAL=" + P1_TOTAL +
                ", P2='" + P2 + '\'' +
                ", P2_TOTAL=" + P2_TOTAL +
                '}';
    }

    public String getTotalMarks() {
        double sum=0;
        int total=0;
        if (!(T1 == null || T1.trim().equals(""))){
            total += this.getTest1();
            if (!T1.contains("Absent") && !T1.contains("Detained"))
                sum+=Double.parseDouble(T1);
        }if (!(T2 == null || T2.trim().equals(""))){
            total += this.getTest2();
            if (!T2.contains("Absent") && !T2.contains("Detained"))
                sum+=Double.parseDouble(T2);
        }if (!(T3 == null || T3.trim().equals(""))){
            total += this.getTest3();
            if (!T3.contains("Absent") && !T3.contains("Detained"))
                sum+=Double.parseDouble(T3);
        }if (!(P1 == null || P1.trim().equals(""))){
            total += this.getP1_TOTAL();
            if (!P1.contains("Absent") && !P1.contains("Detained"))
                sum+=Double.parseDouble(P1);
        }if (!(P2 == null || P2.trim().equals(""))){
            total += this.getP2_TOTAL();
            if (!P2.contains("Absent") && !P2.contains("Detained"))
                sum+=Double.parseDouble(P2);
        }
        return String.format(Locale.ENGLISH,"%.1f",sum) + "/"+total;
    }

    public String getMarksString() {
        String output="";
        if (!(T1 == null || T1.trim().equals(""))){
            output += "T1 : " + T1 + "/" + this.getTest1();
        }if (!(T2 == null || T2.trim().equals(""))){
            output += "\nT2 : " + T2 + "/" + this.getTest2();
        }if (!(T3 == null || T3.trim().equals(""))){
            output += "\nT3 : " + T3 + "/" + this.getTest3();
        }if (!(P1 == null || P1.trim().equals(""))){
            output += "P1 : " + P1 + "/" + this.getP1_TOTAL();
        }if (!(P2 == null || P2.trim().equals(""))){
            output += "\nP2 : " + P2 + "/" + this.getP2_TOTAL();
        }
        return output;
    }
}
