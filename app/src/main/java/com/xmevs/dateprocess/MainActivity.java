package com.xmevs.dateprocess;

import android.app.DatePickerDialog;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {
    private Button btUpdateStartDate = null;
    private Button btUpdateEndDate = null;
    private TextView etStartY, etStartM, etStartD, etEndY, etEndM, etEndD;
    private RotatingRect rrRect = null;

    private int pvalue;
    private float processValue;

    public static final String SED = "STARTENDDATE";

    private boolean isSet = false;
    private DateProcessHelp dph = new DateProcessHelp();

    private long totalNumber, throughNumber;

    private Handler myHandler;
    private ProcessBarThread pbt = new ProcessBarThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rrRect = (RotatingRect) findViewById(R.id.rrRect);
        etStartY = (TextView) findViewById(R.id.etStartY);
        etStartM = (TextView) findViewById(R.id.etStartM);
        etStartD = (TextView) findViewById(R.id.etStartD);
        etEndY = (TextView) findViewById(R.id.etEndY);
        etEndM = (TextView) findViewById(R.id.etEndM);
        etEndD = (TextView) findViewById(R.id.etEndD);
        btUpdateStartDate = (Button) findViewById(R.id.btUpdateStartDate);
        btUpdateEndDate = (Button) findViewById(R.id.btUpdateEndDate);

        btUpdateStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etStartY.setText(year + "");
                        etStartM.setText(month + 1 + "");
                        etStartD.setText(dayOfMonth + "");
                        String strTemp = etStartY.getText().toString() + ","
                                +
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
                new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
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
    }

    private void btSet_Click() {
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
}
