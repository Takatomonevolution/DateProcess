package com.xmevs.dateprocess.activity;

/**
 *  TIME : 2017-1-21 1:51
 *  Version：2.11
 */

import android.app.DatePickerDialog;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.xmevs.dateprocess.R;
import com.xmevs.dateprocess.datebase.DBManager;
import com.xmevs.dateprocess.entity.Info;
import com.xmevs.dateprocess.help.TimeslotHelp;
import com.xmevs.dateprocess.view.RotatingRect;

public class MainActivity extends AppCompatActivity {
    private Button btUpdateStartDate = null;
    private Button btUpdateEndDate = null;
    private TextView tvStartY, tvStartM, tvStartD, tvEndY, tvEndM, tvEndD, tvSlotname;
    private RotatingRect rrRect = null;

    private int pvalue;
    private float processValue;

//    private DateProcessHelp dph = new DateProcessHelp();
    private TimeslotHelp th;

    private long totalNumber, throughNumber;

    private Handler myHandler;
    private ProcessBarThread pbt = new ProcessBarThread();

    private DBManager manager;
    private int TIMESLOTID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        OnClickListener();
        readDb();
        computingTime();
    }

    private void OnClickListener() {
        btUpdateStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // DatePickerDialog类 参数month+1 , DatePickerDialog第3个参数-1即可
                        tvStartY.setText(year + "");
                        //这里要 +1  \ 另外这里是onDateSet() 获取来的 month 会-1 所以要+1
                        tvStartM.setText((month + 1) + "");
                        tvStartD.setText(dayOfMonth + "");
                        tvSlotname.setText(th.getSlotname());
                        String slotname = tvSlotname.getText().toString();
                        String strTemp = getTime();
                        writeDb(strTemp);
//                        Toast.makeText(MainActivity.this, strTemp, Toast.LENGTH_SHORT).show();
//                        dph.setStartEndDate(strTemp);
                        th.setDate(strTemp, slotname);
                        computingTime();
                    }
                    //这里要  -1
//                }, dph.getStartY(), dph.getStartM()-1, dph.getStartD()).show();
                }, th.getYearS(), th.getMonthS()-1, th.getDayS()).show();
            }
        });

        btUpdateEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvEndY.setText(year + "");
                        tvEndM.setText(( month + 1 ) + "");
                        tvEndD.setText(dayOfMonth + "");
                        tvSlotname.setText(th.getSlotname());
                        String slotname = tvSlotname.getText().toString();
                        String strTemp = getTime();
                        writeDb(strTemp);
//                        dph.setStartEndDate(strTemp);
                        th.setDate(strTemp, slotname);
                        computingTime();
                    }
//                }, dph.getEndY(), dph.getEndM()-1, dph.getEndD()).show();
                }, th.getYearE(), th.getMonthE()-1, th.getDayE()).show();
            }
        });
    }

    public String getTime() {
        String temp = tvStartY.getText().toString() + "," +
                tvStartM.getText().toString() + "," +
                tvStartD.getText().toString() + "-" +
                tvEndY.getText().toString() + "," +
                tvEndM.getText().toString() + "," +
                tvEndD.getText().toString();
        return  temp;
    }

    private void update() {
        tvStartY.setText(th.getYearS() + "");
        // 存储的时候 就是正确的值 所以不用+1
        tvStartM.setText(th.getMonthS() + "");
        tvStartD.setText(th.getDayS() + "");
        tvEndY.setText(th.getYearE() + "");
        tvEndM.setText(th.getMonthE() + "");
        tvEndD.setText(th.getDayE() + "");
        tvSlotname.setText(th.getSlotname());
    }

    private void initView() {
        rrRect = (RotatingRect) findViewById(R.id.rrRect);
        tvStartY = (TextView) findViewById(R.id.etStartY);
        tvStartM = (TextView) findViewById(R.id.etStartM);
        tvStartD = (TextView) findViewById(R.id.etStartD);
        tvEndY = (TextView) findViewById(R.id.etEndY);
        tvEndM = (TextView) findViewById(R.id.etEndM);
        tvEndD = (TextView) findViewById(R.id.etEndD);
        tvSlotname = (TextView) findViewById(R.id.tvSlotname);
        btUpdateStartDate = (Button) findViewById(R.id.btUpdateStartDate);
        btUpdateEndDate = (Button) findViewById(R.id.btUpdateEndDate);
        manager = new DBManager(MainActivity.this);
        th = new TimeslotHelp();
    }

    private void computingTime() {
//        totalNumber = dph.totalNumber();
//        throughNumber = dph.throughNumber();
        totalNumber = th.totalDay();
        throughNumber = th.throughDay();
        processValue = (float) (totalNumber - throughNumber) * 100 / totalNumber;
        new Thread(pbt).start();
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                rrRect.setValue(processValue, msg.arg1, throughNumber);
            }
        };
    }

    private void readDb() {
        Info info = manager.query(TIMESLOTID);
        String strTimeslot = "2016,9,1-2017,1,13";
        String slotname = "大二上学期";
        //如果数据库没有数据
        if (info.getTimeslot().equals("-1") || info.getTimeslot()== "-1") {
            //  手动添加一条
            Info info2 = new Info(-1, strTimeslot, slotname);
            tvSlotname.setText(slotname);
//            dph.setStartEndDate(strTimeslot);
            th.setDate(strTimeslot, slotname);
            manager.add(info2);
        } else {
            // 读取数据 读取信息
//            dph.setStartEndDate(info.getTimeslot());
            th.setDate(info.getTimeslot(), info.getSlotname());
            TIMESLOTID = info.getId();
            update();
        }
    }

    private void writeDb(String str) {
        String slotname =  "大二上学期23333";
        Info into = new Info(-1, getTime(), slotname);
//        dph.setStartEndDate(str);
        th.setDate(str, slotname);
        manager.update(TIMESLOTID, into);
    }

//    void defaultTime() {
//        dph.setStartEndDate("2016,9,1-2017,1,13");
//        update();
//    }

    class ProcessBarThread implements Runnable {

        @Override
        public void run() {
            pvalue = 0;
            while (true) {
                pvalue += 1;
                SystemClock.sleep(25);//Android里的休眠
                Message mes = new Message();
                if(pvalue < (int)processValue) {
                    mes.arg1 = pvalue;
                    mes.what = 0x111;
                    myHandler.sendMessage(mes);
                } else {
                    mes.arg1 = (int)processValue;
                    mes.what = 0x222;
                    myHandler.sendMessage(mes);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.close();
    }
}
