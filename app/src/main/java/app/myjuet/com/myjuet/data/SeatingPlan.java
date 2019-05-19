package app.myjuet.com.myjuet.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jsoup.select.Elements;

@Entity
public class SeatingPlan {
    @PrimaryKey(autoGenerate = true)
    int id;
    @Ignore
    public SeatingPlan(Elements columns) {
    }

    public SeatingPlan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
