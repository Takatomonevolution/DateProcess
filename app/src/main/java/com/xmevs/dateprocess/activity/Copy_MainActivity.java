package com.xmevs.dateprocess.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmevs.dateprocess.R;
import com.xmevs.dateprocess.help.DateProcessHelp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class Copy_MainActivity extends AppCompatActivity {

    private TextView tvTotalNumber = null;
    private TextView tvThroughNumber = null;
    private TextView tvWeek;
    private Button btSet = null;
    private Button btUpdateStartDate = null;
    private Button btUpdateEndDate = null;
    private LinearLayout llStartDate = null;
    private LinearLayout llEndDate = null;
    private TextView etStartY, etStartM, etStartD, etEndY, etEndM, etEndD;
    private ProgressBar pbProcess = null;
    private Handler myHandler;
    private int pvalue;
    private float processValue;
    private ProcessBarThread pbt = new ProcessBarThread();
//    private TotalNumberThread totalT = new TotalNumberThread();
//    private ThroughNumberThread throughT = new ThroughNumberThread();
//    final SharedPreferences sp = getSharedPreferences("SPShare", );
//    private final SharedPreferences.Editor editor = sp.edit();

    public static final String SED = "STARTENDDATE";

    private boolean isSet = false;
    private DateProcessHelp dph = new DateProcessHelp();

    private long totalNumber, throughNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalNumber = (TextView) findViewById(R.id.tvTotalNumber);
        tvThroughNumber = (TextView) findViewById(R.id.tvThroughNumber);
        btSet = (Button) findViewById(R.id.btSet);
        llStartDate = (LinearLayout) findViewById(R.id.llStartDate);
        llEndDate = (LinearLayout) findViewById(R.id.llEndDate);
        etStartY = (TextView) findViewById(R.id.etStartY);
        etStartM = (TextView) findViewById(R.id.etStartM);
        etStartD = (TextView) findViewById(R.id.etStartD);
        etEndY = (TextView) findViewById(R.id.etEndY);
        etEndM = (TextView) findViewById(R.id.etEndM);
        etEndD = (TextView) findViewById(R.id.etEndD);
        pbProcess = (ProgressBar) findViewById(R.id.pbProcess);
        tvWeek = (TextView) findViewById(R.id.tvWeek);
        btUpdateStartDate = (Button) findViewById(R.id.btUpdateStartDate);
        btUpdateEndDate = (Button) findViewById(R.id.btUpdateEndDate);

//        setVisible(false);

        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSet) {
                    getDate();
                    setVisible(true);
                    btSet.setText("隐藏");
                    isSet = true;
                } else if(isSet) {
//                    String strTemp = etStartY.getText().toString() + "," +
//                            etStartM.getText().toString() + "," +
//                            etStartD.getText().toString() + "-" +
//                            etEndY.getText().toString() + "," +
//                            etEndM.getText().toString() + "," +
//                            etEndD.getText().toString();
//                    write(strTemp);
//                    dph.setStartEndDate(strTemp);
//                    btSet_Click();
                    setVisible(false);
                    btSet.setText("显示");
                    isSet = false;
                } else {
                    btSet.setText("显示");
                    isSet = false;
                }
//                Toast.makeText(MainActivity.this, dph.getStartEndDate().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btUpdateStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Copy_MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etStartY.setText(year + "");
                        etStartM.setText(month + 1 + "");
                        etStartD.setText(dayOfMonth + "");
                        String strTemp = etStartY.getText().toString() + "," +
                                etStartM.getText().toString() + "," +
                                etStartD.getText().toString() + "-" +
                                etEndY.getText().toString() + "," +
                                etEndM.getText().toString() + "," +
                                etEndD.getText().toString();
                        write(strTemp);
                        dph.setStartEndDate(strTemp);
                        btSet_Click();
                    }
                }, dph.getStartY(), dph.getStartM()-1, dph.getStartD()).show();
            }
        });

        btUpdateEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Copy_MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etEndY.setText(year + "");
                        etEndM.setText(month + 1 + "");
                        etEndD.setText(dayOfMonth + "");
                        String strTemp = etStartY.getText().toString() + "," +
                                etStartM.getText().toString() + "," +
                                etStartD.getText().toString() + "-" +
                                etEndY.getText().toString() + "," +
                                etEndM.getText().toString() + "," +
                                etEndD.getText().toString();
                        write(strTemp);
                        dph.setStartEndDate(strTemp);
                        btSet_Click();

                    }
                }, dph.getEndY(), dph.getEndM()-1, dph.getEndD()).show();
            }
        });

        read();
        btSet_Click();
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                pbProcess.setProgress(msg.arg1);
            }
        };
//        Toast.makeText(MainActivity.this, dph.getStartEndDate().toString(), Toast.LENGTH_SHORT).show();
    }

    private void btSet_Click() {
        totalNumber = dph.totalNumber();
        throughNumber = dph.throughNumber();
        tvTotalNumber.setText(throughNumber + "");
        setweek(throughNumber);
        new Thread(pbt).start();
        processValue = (float) (totalNumber - throughNumber) * 100 / totalNumber;
        tvThroughNumber.setText(String.format("%.2f", processValue));
    }

    public void setVisible(boolean visible) {
        if(!visible) {
            llStartDate.setVisibility(View.INVISIBLE);
            llEndDate.setVisibility(View.INVISIBLE);
        } else if (visible) {
            llStartDate.setVisibility(View.VISIBLE);
            llEndDate.setVisibility(View.VISIBLE);
        }
    }

    public void getDate() {
        etStartY.setText(dph.getStartY() + "");
        etStartM.setText(dph.getStartM() + "");
        etStartD.setText(dph.getStartD() + "");
        etEndY.setText(dph.getEndY() + "");
        etEndM.setText(dph.getEndM() + "");
        etEndD.setText(dph.getEndD() + "");
    }

    private void setweek(long throughNumber) {
        long throughWeek = throughNumber/7;
        long throughDay = throughNumber%7;
        if(throughDay==0) {
            tvWeek.setText("(" + throughWeek + "周整)");
        } else if(throughWeek<1) {
            tvWeek.setText("(" + throughDay + "天)");
        } else {
            tvWeek.setText("(" + throughWeek + "周又" +throughDay + "天)");
        }
    }

    private void read() {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = openFileInput(SED);
            isr = new InputStreamReader(fis, "UTF-8");
            char[] input = new char[fis.available()];
            isr.read(input);
            String readed = new String(input);
            if (readed.equals("")) {
                dph.setStartEndDate("2016,9,1-2017,1,13");
//                Toast.makeText(MainActivity.this, "reeeee:" + readed, Toast.LENGTH_SHORT).show();
            } else {
                dph.setStartEndDate(readed);
//                Toast.makeText(MainActivity.this, "readed:" + readed, Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            dph.setStartEndDate("2016,9,1-2017,1,13");
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
    }

    private void write(String str) {
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
    }

    class ProcessBarThread implements Runnable {

        @Override
        public void run() {
            pvalue = 0;
            while (true) {
                pvalue += 1;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
}
