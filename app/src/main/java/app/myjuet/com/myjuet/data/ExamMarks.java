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
    String T1;
    String T2;
    String T3;
    String P1;
    String P2;

    public ExamMarks() {
    }

    @Ignore
    public ExamMarks(Elements columns, SparseArray<Exam> map) {
        for (int i = 0; i < columns.size(); i++) {
            if (i == 0){
                this.id =columns.get(i).text();
            }else if( i == 1){
                String[] strings= TextUtils.split(columns.get(i).text(), "- ");
                this.subjectCode = strings[strings.length -1];
                this.subjectName = columns.get(i).text();
            }else if (map.get(i) == Exam.TEST1){
                this.Test1 = 15;
                this.T1 = columns.get(i).text();
            }else if (map.get(i) == Exam.T1){
                this.Test1 = 15;
                this.T1 = columns.get(i).text();
            }else if (map.get(i) == Exam.TEST2){
                this.Test2 = 25;
                this.T2 = columns.get(i).text();
            }else if (map.get(i) == Exam.T2){
                this.Test2 = 25;
                this.T2 = columns.get(i).text();
            }else if (map.get(i) == Exam.TEST3){
                this.T3 = columns.get(i).text();
            }else if (map.get(i) == Exam.P1){
                this.P1 = columns.get(i).text();
            }else if (map.get(i) == Exam.P2){
                this.P2 = columns.get(i).text();
            }
        }
    }

    public Integer getTest1() {
        return Test1;
    }

    public void setTest1(Integer test1) {
        Test1 = test1;
    }

    public Integer getTest2() {
        return Test2;
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
                ", T1='" + T1 + '\'' +
                ", T2='" + T2 + '\'' +
                ", T3='" + T3 + '\'' +
                ", P1='" + P1 + '\'' +
                ", P2='" + P2 + '\'' +
                '}';
    }

    public String getTotalMarks() {
        double sum=0;
        int total=0;
        if (!(T1 == null || T1.trim().equals(""))){
            total+= this.Test1;
            if (!T1.contains("Absent") && !T1.contains("Detained"))
                sum+=Double.parseDouble(T1);
        }if (!(T2 == null || T2.trim().equals(""))){
            total+= this.Test2;
            if (!T2.contains("Absent") && !T2.contains("Detained"))
                sum+=Double.parseDouble(T2);
        }if (!(T3 == null || T3.trim().equals(""))){
            total+=35;
            if (!T3.contains("Absent") && !T3.contains("Detained"))
                sum+=Double.parseDouble(T3);
        }if (!(P1 == null || P1.trim().equals(""))){
            total+=15;
            if (!P1.contains("Absent") && !P1.contains("Detained"))
                sum+=Double.parseDouble(P1);
        }if (!(P2 == null || P2.trim().equals(""))){
            total+=15;
            if (!P2.contains("Absent") && !P2.contains("Detained"))
                sum+=Double.parseDouble(P2);
        }
        return String.format(Locale.ENGLISH,"%.1f",sum) + "/"+total;
    }

    public String getMarksString() {
        String output="";
        if (!(T1 == null || T1.trim().equals(""))){
            output+="T1 : "+T1+"/" + this.Test1;
        }if (!(T2 == null || T2.trim().equals(""))){
            output+="\nT2 : "+T2+"/"+ + this.Test2;
        }if (!(T3 == null || T3.trim().equals(""))){
            output+="\nT3 : "+T3+"/35";
        }if (!(P1 == null || P1.trim().equals(""))){
            output+="P1 : "+P1+"/15";
        }if (!(P2 == null || P2.trim().equals(""))){
            output+="\nP2 : "+P2+"/15";
        }
        return output;
    }
}
