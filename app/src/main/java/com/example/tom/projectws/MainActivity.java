package com.example.tom.projectws;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private TextView textview,textview_date,textViewUserEmail;
    private ImageView imageView;
    private int year,month, day;
    private Button buttonLogout,btnShowToken,btnDisplayDate;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            textview = (TextView) findViewById(R.id.textView);
            if(textview.getText().toString()==null)
            {
                String message ="* "+intent.getStringExtra("msg")+"\n";
                textview.setText(message);
                return;
            }

            String message =textview.getText().toString()
                    +"* "+intent.getStringExtra("msg")+"\n";
            textview.setText(message);
            saveNotification();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Logout and user info
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        buttonLogout=(Button) findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(this);
        textViewUserEmail=(TextView)findViewById(R.id.textViewUserEmail);

        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        //set welcome for each user
        textViewUserEmail.setText("Welcome:"+user.getEmail());
        //display date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR); // 获取当前年份
        month = c.get(Calendar.MONTH) + 1;// 获取当前月份
        day = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        textview_date=(TextView) findViewById(R.id.textView_date);
        textview_date.setText("Today is:"+year+"/"+month+"/"+day+"\n"+"These are your notification below:");
        //profile photo
        Intent intent=getIntent();
        if (activityReceiver != null) {
            IntentFilter intentFilter = new  IntentFilter("ACTION_STRING_ACTIVITY");
            registerReceiver(activityReceiver, intentFilter);
        }

        btnShowToken=(Button) findViewById(R.id.button_show_token);
        btnShowToken.setOnClickListener(this);


        //display today's notification
        final String date=year+""+month+""+day;
        displayNotification(date);

        //select date from dialog
        btnDisplayDate=(Button)findViewById(R.id.button_displayDate);
        btnDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog=new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month-1,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener=new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String date=year+""+(month+1)+""+dayOfMonth++;
                String message="You select the date("+year+"/"+(month+1)+"/"+(dayOfMonth-1)+")\n These notification are displayed below:";
                textview_date.setText(message);
                //display notification on selected date
               displayNotification(date);
            }
        };
    }
    //display notification1
    private void displayNotification(final String date){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        textview = (TextView) findViewById(R.id.textView);
        Query query=databaseReference.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(date).getValue()!=null) {
                    textview.setText(dataSnapshot.child(date).getValue().toString());
                }
                else
                {
                    textview.setText("There are no notification in your selected day");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //save notification sent from firebase
    private void saveNotification(){
        final String Date=year+""+month+""+day;
        final String Notification=textview.getText().toString();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(user.getUid());
        if( databaseReference.child(Date)==null) {
            databaseReference.push().child(Date);
            databaseReference.child(Date).setValue(Notification);
        }
        else {
            //add notification to specific date
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    databaseReference.child(Date).setValue(Notification);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    @Override
    public void onClick(View v) {
        if (v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v == btnShowToken){
            String token= FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Token:"+token);
            Toast.makeText(MainActivity.this,token, Toast.LENGTH_SHORT).show();
        }

    }
}
