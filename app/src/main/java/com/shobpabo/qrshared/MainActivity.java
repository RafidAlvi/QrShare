package com.shobpabo.qrshared;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
String TAG = "GenerateQrCode";
EditText qrEd,nameED,phnED,adED;
ImageView qrimg,gene,shareb;
Button create,namebt,phnbt,adrsbt,info,scan,saveB,deleteB;
Bitmap bitmap;
QRGEncoder qrgEncoder;
String inputValue;
EditText edsave;






    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!=null && result.getContents()!=null){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Scan Result")
                    .setMessage(result.getContents())
                    .setIcon(R.drawable.qr)
                    .setCancelable(false)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData data = ClipData.newPlainText("result",result.getContents());
                            edsave.setText("\n \n"+result.getContents()+"\n \n ________________________\n \n"+ edsave.getText().toString()+"\n \n");
                            Toast.makeText(MainActivity.this,
                                    "Saved: \n" + result.getContents(), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                            editor.putString("saved",edsave.getText().toString());
                            editor.apply();

                            manager.setPrimaryClip(data);

                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            }).create().show();
        }
        super.onActivityResult(requestCode,resultCode,data);

    }







    @Override
    protected void onStart() {
        super.onStart();
        qrimg.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fade));

        SharedPreferences prefs = getSharedPreferences("name", MODE_PRIVATE);
        String name = prefs.getString("name", "");//"No name defined" is the default value.
        nameED.setText(name);

        SharedPreferences prefs1 = getSharedPreferences("phone", MODE_PRIVATE);
        String phon = prefs1.getString("phn", "");//"No name defined" is the default value.
        phnED.setText(phon);

        SharedPreferences prefs2 = getSharedPreferences("address", MODE_PRIVATE);
        String adrss = prefs2.getString("adrs", "");//"No name defined" is the default value.
        adED.setText(adrss);

        SharedPreferences prefsSave = getSharedPreferences("save", MODE_PRIVATE);
        String saved = prefsSave.getString("saved", "");//"No name defined" is the default value.
        edsave.setText(saved);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        qrimg = findViewById(R.id.qrcode);
        qrimg.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fade));
        qrEd = findViewById(R.id.editTextName);


        edsave = findViewById(R.id.edSave);


        phnED = findViewById(R.id.edPhone);
        nameED = findViewById(R.id.edName);
        adED = findViewById(R.id.edAddress);

        phnbt = findViewById(R.id.btPhone);
        adrsbt = findViewById(R.id.btAddress);
        namebt = findViewById(R.id.btName);
        info = findViewById(R.id.allinfobt);
        gene = findViewById(R.id.gen);
        saveB = findViewById(R.id.btSave);
        deleteB = findViewById(R.id.btClear);
        scan = findViewById(R.id.scanButton);
        shareb = findViewById(R.id.shareBT);


        shareb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ApplicationInfo api = getApplicationContext().getApplicationInfo();
                String apkPath = api.sourceDir;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");

                intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(apkPath)));
                startActivity(Intent.createChooser(intent,"Share QrShare app using.. "));


            }
        });


        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putString("saved",edsave.getText().toString());
                editor.apply();
                Toast.makeText(MainActivity.this,
                        "Saved", Toast.LENGTH_SHORT).show();
            }
        });



        deleteB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteB.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotatealbum));

                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putString("saved"," ");
                editor.apply();
                edsave.setText(" ");

                Toast.makeText(MainActivity.this,
                        "Deleted", Toast.LENGTH_SHORT).show();


                return false;
            }
        });







        deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteB.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate));
                Toast.makeText(MainActivity.this,
                        "Long press to Delete", Toast.LENGTH_SHORT).show();
            }
        });





        gene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = qrEd.getText().toString().trim();
                if (inputValue.length()>0){
                    hideKeyboard(v);

                    WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width= point.x;
                    int height= point.y;
                    int smallerdimention= width<height ? width:height;
                    smallerdimention = smallerdimention*4/4;
                    qrgEncoder=new QRGEncoder(inputValue,null, QRGContents.Type.TEXT,smallerdimention);
                    try{
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrimg.setImageBitmap(bitmap);
                        Toast.makeText(MainActivity.this,
                                "QR contains: \n" + inputValue, Toast.LENGTH_SHORT).show();
                    }
                    catch (WriterException e){
                        Log.v(TAG,e.toString());
                    }




                }
                else {
                    qrEd.setError("Required");
                }

            }
        });


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameED.length()>0) {
                    String  inputValue1 = nameED.getText().toString().trim();
                    if (phnED.length()>0) {
                       String inputValue2 = phnED.getText().toString().trim();
                        if (adED.length()>0) {
                            String inputValue3 = adED.getText().toString().trim();

                            inputValue = "Name: "+ inputValue1 +"\n "+"Phone: "+ inputValue2 +"\n "+"Address: "+ inputValue3;
                            if (inputValue.length()>0) {
                                qrMaker();
                                hideKeyboard(v);
                                Toast.makeText(MainActivity.this,
                                        "QR contains: \n" + inputValue, Toast.LENGTH_SHORT).show();
                            } else {
                                nameED.setError("Required");
                            }

                        }else {
                            adED.setError("Required");
                        }

                    }else {
                        phnED.setError("Required");
                    }

                }
                else {
                    nameED.setError("Required");
                }



            }
        });






        namebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = nameED.getText().toString().trim();
                if (inputValue.length()>0) {
                    qrMaker();
                    hideKeyboard(v);
                    Toast.makeText(MainActivity.this,
                            "QR contains: \n" + inputValue, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                    editor.putString("name", inputValue);
                    editor.apply();
                } else {
                    nameED.setError("Required");
                }
            }
        });

        adrsbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = adED.getText().toString().trim();
                if (inputValue.length()>0) {
                    qrMaker();
                    hideKeyboard(v);
                    Toast.makeText(MainActivity.this,
                            "QR contains: \n" + inputValue, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("address", MODE_PRIVATE).edit();
                    editor.putString("adrs", inputValue);
                    editor.apply();
                } else {
                    adED.setError("Required");
                }
            }
        });

        phnbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = phnED.getText().toString().trim();
                if (inputValue.length()>0) {
                    qrMaker();
                    hideKeyboard(v);
                    Toast.makeText(MainActivity.this,
                            "QR contains: \n" + inputValue, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("phone", MODE_PRIVATE).edit();
                    editor.putString("phn", inputValue);
                    editor.apply();
                } else {
                    phnED.setError("Required");
                }
            }
        });





        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("scanning");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();

            }
        });





    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void qrMaker(){

        WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width= point.x;
        int height= point.y;
        int smallerdimention= width<height ? width:height;
        smallerdimention = smallerdimention*4/4;
        qrgEncoder=new QRGEncoder(inputValue,null, QRGContents.Type.TEXT,smallerdimention);
        try{
            bitmap = qrgEncoder.encodeAsBitmap();
            qrimg.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            Log.v(TAG,e.toString());
        }

    }




}
