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
import com.xmevs.dateprocess.help.DateProcessHelp;
import com.xmevs.dateprocess.view.RotatingRect;

public class MainActivity extends AppCompatActivity {
    private Button btUpdateStartDate = null;
    private Button btUpdateEndDate = null;
    private TextView etStartY, etStartM, etStartD, etEndY, etEndM, etEndD;
    private RotatingRect rrRect = null;

    private int pvalue;
    private float processValue;

    private DateProcessHelp dph = new DateProcessHelp();

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
                        etStartY.setText(year + "");
                        //这里要 +1  \ 另外这里是onDateSet() 获取来的 month 会-1 所以要+1
                        etStartM.setText((month + 1) + "");
                        etStartD.setText(dayOfMonth + "");
                        String strTemp = getTime();
                        writeDb(strTemp);
//                        Toast.makeText(MainActivity.this, strTemp, Toast.LENGTH_SHORT).show();
                        dph.setStartEndDate(strTemp);
                        computingTime();
                    }
                    //这里要  -1
                }, dph.getStartY(), dph.getStartM()-1, dph.getStartD()).show();
            }
        });

        btUpdateEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etEndY.setText(year + "");
                        etEndM.setText(( month + 1 ) + "");
                        etEndD.setText(dayOfMonth + "");
                        String strTemp = getTime();
                        writeDb(strTemp);
                        dph.setStartEndDate(strTemp);
                        computingTime();
                    }
                }, dph.getEndY(), dph.getEndM()-1, dph.getEndD()).show();
            }
        });
    }

    public String getTime() {
        String temp = etStartY.getText().toString() + "," +
                etStartM.getText().toString() + "," +
                etStartD.getText().toString() + "-" +
                etEndY.getText().toString() + "," +
                etEndM.getText().toString() + "," +
                etEndD.getText().toString();
        return  temp;
    }

    private void update() {
        etStartY.setText(dph.getStartY() + "");
        // 存储的时候 就是正确的值 所以不用+1
        etStartM.setText(dph.getStartM() + "");
        etStartD.setText(dph.getStartD() + "");
        etEndY.setText(dph.getEndY() + "");
        etEndM.setText(dph.getEndM() + "");
        etEndD.setText(dph.getEndD() + "");
    }

    private void initView() {
        rrRect = (RotatingRect) findViewById(R.id.rrRect);
        etStartY = (TextView) findViewById(R.id.etStartY);
        etStartM = (TextView) findViewById(R.id.etStartM);
        etStartD = (TextView) findViewById(R.id.etStartD);
        etEndY = (TextView) findViewById(R.id.etEndY);
        etEndM = (TextView) findViewById(R.id.etEndM);
        etEndD = (TextView) findViewById(R.id.etEndD);
        btUpdateStartDate = (Button) findViewById(R.id.btUpdateStartDate);
        btUpdateEndDate = (Button) findViewById(R.id.btUpdateEndDate);
        manager = new DBManager(MainActivity.this);
    }

    private void computingTime() {
        totalNumber = dph.totalNumber();
        throughNumber = dph.throughNumber();
        processValue = (float) (totalNumber - throughNumber) * 100 / totalNumber;
        new Thread(pbt).start();
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                rrRect.setValue(processValue, msg.arg1, throughNumber);
            }
        };
    }

/*    private void read() {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = openFileInput(SED);
            isr = new InputStreamReader(fis, "UTF-8");
            char[] input = new char[fis.available()];
            isr.read(input);
            String readed = new String(input);
            if (readed.equals("")) {
                defaultTime();
//                Toast.makeText(MainActivity.this, "unread:" + readed, Toast.LENGTH_SHORT).show();
            } else {
                dph.setStartEndDate(readed);
                update();
//                Toast.makeText(MainActivity.this, "readed:" + readed, Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            defaultTime();
            Log.e("Exception", "FileNotFoundException");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null){
                    isr.close();
                    isr = null;
                }
                if (fis != null){
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void readDb() {
        Info info = manager.query(TIMESLOTID);
        String strTimeslot = "2016,9,1-2017,1,13";
        //如果数据库没有数据
        if (info.getTimeslot().equals("-1") || info.getTimeslot()== "-1") {
            //  手动添加一条
            Info info2 = new Info(-1, strTimeslot);
            dph.setStartEndDate(strTimeslot);
            manager.add(info2);
        } else {
            // 读取数据 读取信息
            dph.setStartEndDate(info.getTimeslot());
            TIMESLOTID = info.getId();
            update();
        }
    }

    /*private void write(String str) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = openFileOutput(SED, MODE_PRIVATE);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(str);
            osw.flush();
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                osw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void writeDb(String str) {
        Info into = new Info(-1, getTime());
        dph.setStartEndDate(getTime());
        manager.update(TIMESLOTID, into);
    }



    void defaultTime() {
        dph.setStartEndDate("2016,9,1-2017,1,13");
        update();
    }

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
