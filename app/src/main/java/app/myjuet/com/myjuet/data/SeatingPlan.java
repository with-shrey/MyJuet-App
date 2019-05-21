package app.myjuet.com.myjuet.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Entity
public class SeatingPlan {
    @NonNull
    @PrimaryKey
    String subjectCode;
    String subjectName;
    String date;
    String time;
    String centerName;
    String roomName;
    String row;
    String column;
    String seatNo;

    public SeatingPlan() {
    }
    @Ignore
    public SeatingPlan(Elements dateColumns, Elements seatColumns) {
       String sub = dateColumns.get(0).text();
       String[] dateArray = dateColumns.get(1).text()
               .replace("Date : ","")
               .split(" At ");
       sub=sub.replace("Paper ID : ","");
       String[] subjectArray = sub.split("\\(");
       this.subjectCode = subjectArray[subjectArray.length-1].replace(")","");
       this.subjectName = sub;
       this.date=dateArray[0];
       this.time=dateArray[1];
       this.centerName = seatColumns.get(0).text();
       this.roomName = seatColumns.get(1).text();
       this.row = seatColumns.get(2).text();
       this.column = seatColumns.get(3).text();
       this.seatNo = seatColumns.get(4).text();
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

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    @Override
    public String toString() {
        return "SeatingPlan{" +
                "subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", centerName='" + centerName + '\'' +
                ", roomName='" + roomName + '\'' +
                ", row='" + row + '\'' +
                ", column='" + column + '\'' +
                ", seatNo='" + seatNo + '\'' +
                '}';
    }
}
