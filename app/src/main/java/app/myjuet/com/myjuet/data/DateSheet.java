package app.myjuet.com.myjuet.data;

import android.text.TextUtils;

import org.jsoup.select.Elements;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DateSheet{
    String id;
    @NonNull
    @PrimaryKey
    String subjectCode;
    String subjectName;
    String date;
    String time;

    public DateSheet() {
    }

    @Ignore
    public DateSheet(String id, @NonNull String subjectCode, String subjectName, String date, String time) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.date = date;
        this.time = time;
    }
    @Ignore
    public DateSheet(Elements columns) {
        for (int i = 0; i < columns.size(); i++) {
            switch (i){
                case 0:
                    this.id =columns.get(i).text();
                    break;
                case 1:
                    this.date = columns.get(i).text();
                    break;
                case 2:
                    this.time = columns.get(i).text();
                    break;
                case 3:
                    String[] strings= TextUtils.split(columns.get(i).text(), "\\(");
                    this.subjectCode = strings[strings.length -1];
                    this.subjectName = columns.get(i).text();
                    break;
            }
        }
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DateSheet{" +
                "id='" + id + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}