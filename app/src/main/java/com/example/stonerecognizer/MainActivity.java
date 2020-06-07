package com.example.stonerecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView i;
    Button b;
    TextView t;
    public static final int Pic_image=121;
    ImageLabeler labeler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("modelfiles/manifest.json")
                        // or .setAbsoluteFilePath(absolute file path to manifest file)
                        .build();
        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.0f)  // Evaluate your model in the Firebase console
                        // to determine an appropriate value.
                        .build();
        labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

        i=(ImageView)findViewById(R.id.image);
        b=(Button)findViewById(R.id.choose);
        t=(TextView)findViewById(R.id.text);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.setText("");
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),Pic_image);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Pic_image) {
            i.setImageURI(data.getData());
            t.setText("");
            InputImage image;
            try {
                image = InputImage.fromFilePath(getApplicationContext(), data.getData());
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                // Task completed successfully
                                // ...
                                for (ImageLabel label : labels) {
                                    String text = label.getText();

                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();
                                    t.append(text+" "+confidence+" "+index+"\n");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
