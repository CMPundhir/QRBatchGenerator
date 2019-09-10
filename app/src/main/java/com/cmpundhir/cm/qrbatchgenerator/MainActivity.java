package com.cmpundhir.cm.qrbatchgenerator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;
    List<String> list = new ArrayList<>();
    String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null ) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "failed to resolve permission for "+permission, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //startActivty();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.CAMERA)) {
                    Toast.makeText(getApplicationContext() , "Permissions are required for this app" , Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        if(!hasPermissions(MainActivity.this,PERMISSIONS)){
            showCameraPermissionDialog();
        }

        findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScanQRcodeActivity.class);
                startActivityForResult(intent,100);
            }
        });
        findViewById(R.id.doneBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.size()==0){
                    Toast.makeText(MainActivity.this, "Please scan qrcode first", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for(String s : list){
                    builder.append(s+",");
                }
                for(int i=0;i<list.size();i++){
                    builder.append(list.get(i));
                    if(i!=list.size()-1){
                        builder.append(",");
                    }
                }
                builder.replace(builder.length(),builder.length()+1,"");
                Intent intent = new Intent(MainActivity.this,GenerateQRCodeActivity.class);
                intent.putExtra("data",builder.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){
            String  qr = data.getStringExtra("qrcode");
            if(list.contains(qr)){
                Toast.makeText(this, "This QRCode is already scnned", Toast.LENGTH_SHORT).show();
            }else{
                list.add(qr);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showCameraPermissionDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("QRBatchGenerator Needs Camera Access and Write to External Storage")
                .setCancelable(true)
                .setMessage("QRBatchGenerator needs camera access permissions for scanning qrcode and saving genrated qrcodes");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,REQUEST_CODE_ASK_PERMISSIONS);
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recycler_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.sno.setText(position+1+"");
            holder.title.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView sno,title;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sno = itemView.findViewById(R.id.sno);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

}
