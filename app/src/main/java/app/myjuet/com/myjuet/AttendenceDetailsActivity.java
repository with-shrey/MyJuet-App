package app.myjuet.com.myjuet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.databinding.CalculatorBinding;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;


public class AttendenceDetailsActivity extends AppCompatActivity {
    AppDatabase mAppDatabase;
    ArrayList<AttendenceDetails> listdata;
    AttendenceData mAttendenceData;
    @SuppressWarnings("UnusedAssignment")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesUtil.getPreferences(this,"dark",false))
            setTheme(R.style.DarkTheme);
        mAppDatabase = AppDatabase.newInstance(this);
        setContentView(R.layout.activity_attendence_details);
        AdView mAdView;
        String i = getIntent().getStringExtra("id");
        TextView classesno = findViewById(R.id.noofclasses);
        TextView Present = findViewById(R.id.present);
        TextView Absent = findViewById(R.id.absent);
        TextView leaving = findViewById(R.id.leavingdetails);
        TextView nextattend = findViewById(R.id.nextdetails);
        TextView lt = findViewById(R.id.lecandtut);
        TextView l = findViewById(R.id.lec);
        TextView tut = findViewById(R.id.tut);
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        Button calculate =  findViewById(R.id.calculate);
        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(5);
        mAppDatabase.AttendenceDao().getAttendenceById(i).observe(this,attendenceData -> {
            mAttendenceData = attendenceData;
             SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
             double Attendence = Integer.parseInt(prefs.getString(getString(R.string.key_preferred_attendence), "90"));
             double t = Attendence / 100;
             int pa = Integer.parseInt(mAttendenceData.getmCountPresent()) + Integer.parseInt(mAttendenceData.getmCountAbsent());

             int p = Integer.parseInt(mAttendenceData.getmCountPresent());
             int res;
             String ClassText;
             double classes;
             if (!mAttendenceData.getmLecTut().contains("--")) {
                 ClassText = getResultText(p,pa,Attendence);
             } else
                 ClassText = "No Classes Updated Yet!!";

             classesno.setText(ClassText);
            Present.setText(mAttendenceData.getmCountPresent());
            Absent.setText(mAttendenceData.getmCountAbsent());
            leaving.setText(mAttendenceData.getmOnLeaving());
            nextattend.setText(mAttendenceData.getmOnNext());
            lt.setText(mAttendenceData.getmLecTut());
            l.setText(mAttendenceData.getmLec());
            tut.setText(mAttendenceData.getmTut());
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(mAttendenceData.getmName());
         });
        listdata = new ArrayList<>();
        RecyclerView list = findViewById(R.id.listdetails);
        ScrollView scrollView = findViewById(R.id.scroller);
        list.setNestedScrollingEnabled(false);
        DetailsAdapter adapter = new DetailsAdapter(this, listdata);
        list.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(mLayoutManager);
        scrollView.smoothScrollTo(0, 0);
        mAppDatabase.AttendenceDetailsDao().AttendenceDetails(i).observe(this,attendenceDetails -> {
            listdata.clear();
            if (attendenceDetails != null && attendenceDetails.size()>0) {
                listdata.addAll(attendenceDetails);
            }else{
                listdata.clear();
                listdata.add(new AttendenceDetails("N/A","--","--"));
            }
            assert attendenceDetails != null;
            adapter.notifyDataSetChanged();
        });
        mAdView = findViewById(R.id.adViewDetails);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        calculate.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
            int Attendence = Integer.parseInt(prefs.getString(getString(R.string.key_preferred_attendence), "90"));
            CalculatorHolder holder = new CalculatorHolder(mAttendenceData.getCountPresent(),
                    mAttendenceData.getCountAbsent(),
                    Attendence
                    );
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DarkTheme);
            CalculatorBinding binding = DataBindingUtil.inflate(getLayoutInflater(),R.layout.calculator,null,false);
            builder.setView(binding.getRoot());
            builder.setTitle("Attendence Calculator");
            binding.setHolder(holder);
            binding.setLifecycleOwner(AttendenceDetailsActivity.this);
            builder.show();

        });
    }

    int totalPercent(int p,int a){
        return (int)Math.floor((p*100.0)/(p+a));
    }

    public String getResultText(int p,int pa,double Attendence){
        String ClassText ="";
        double classes,t = Attendence/100.0;
        int res;
        if (Integer.parseInt(mAttendenceData.getmLecTut()) < Attendence) {
                classes = Math.ceil(((t * pa) - p) / (1 - t));
                res = (int) classes;
                ClassText = "You Need To Attend " + String.valueOf(res) + " Classes To Reach Threshold " + String.valueOf(Attendence) + " %";
            } else if (Integer.parseInt(mAttendenceData.getmLecTut()) == Attendence) {
                ClassText = "Don't Leave Class";
            } else {
                classes = Math.floor((p - (t * pa)) / (t)) - 1;
                res = (int) classes;
                if (res <= 0) {
                    res = 0;
                    ClassText = "Don't Leave Class";

                } else {
                    ClassText = "You Can Leave " + classes + " Classes And Reach Threshold " + String.valueOf(Attendence) + " %\n I suggest NOT to leave a class!!\n";
                }

            }
            return ClassText;
        }

        public class CalculatorHolder {
            public MutableLiveData<Integer> willAttend,willLeave,percentage;
            public int present, absent;
            public MutableLiveData<String> preference, leaveText;
            public CalculatorHolder(int p, int a, int preferences) {
                present = p;
                absent = a;
                preference = new MutableLiveData<>();
                willAttend = new MutableLiveData<>();
                willLeave = new MutableLiveData<>();
                percentage = new MutableLiveData<>();
                leaveText = new MutableLiveData<>();

                willLeave.setValue(0);
                willAttend.setValue(0);
                preference.setValue(""+preferences);
                percentage.setValue(0);
                calculate();

                willAttend.observe(AttendenceDetailsActivity.this, integer -> {
                    calculate();
                });
                willLeave.observe(AttendenceDetailsActivity.this, integer -> {
                    calculate();
                });
                preference.observe(AttendenceDetailsActivity.this, string -> {
                    try {
                        Integer.parseInt(string);
                        calculate();
                    }catch (Exception e){

                    }
                });
            }

            public MutableLiveData<Integer> getMutWillAttend() {
                return willAttend;
            }

            public MutableLiveData<Integer> getMutWillLeave() {
                return willLeave;
            }

            void calculate(){
                int pa = present+willAttend.getValue() + absent + willLeave.getValue();
                String text = getResultText(present+willAttend.getValue(),pa,Double.parseDouble(preference.getValue()));
                leaveText.setValue(text);

                percentage.setValue(totalPercent(present+willAttend.getValue(),absent + willLeave.getValue()));
            }

            public void incrementAttend(View view){
                willAttend.setValue(willAttend.getValue() + 1);
            }
            public void decrementAttend(View view){
                willAttend.setValue(willAttend.getValue() - 1);
            }
            public void incrementLeave(View view){
                willLeave.setValue(willLeave.getValue() + 1);

            }
            public void decrementLeave(View view){
                willLeave.setValue(willLeave.getValue() - 1);
            }
        }




}
