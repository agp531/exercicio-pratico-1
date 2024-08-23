package com.example.trabalhopratico1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etUrl;
    private Button btnDownload;
    private ProgressBar progressBar;
    private ImageView imgView;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUrl = findViewById(R.id.etUrl);
        btnDownload = findViewById(R.id.btnDownload);
        progressBar = findViewById(R.id.progressBar);
        imgView = findViewById(R.id.imgView);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etUrl.getText().toString().trim();
                if (!url.isEmpty()) {
                    downloadImage(url);
                }
            }
        });
    }

    private void downloadImage(String url) {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    InputStream in = new URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        // Exibir mensagem de erro para o usuário
                        Toast.makeText(MainActivity.this, "Erro ao baixar a imagem", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                Bitmap finalBitmap = bitmap;
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (finalBitmap != null) {
                        imgView.setImageBitmap(finalBitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "Imagem não encontrada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}