package com.example.braintumordetection;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Menu;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.graphics.Color;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.braintumordetection.ml.Model;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
public class MainActivity extends AppCompatActivity {
    private Button select, predict;
    private ImageView imageView;
    private TextView output,accu,accout;
    private Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select=findViewById(R.id.Select);
        predict=findViewById(R.id.Predict);
        imageView=findViewById(R.id.imageView);
        output=findViewById(R.id.textView);
        accu=findViewById(R.id.textView2);
        accout=findViewById(R.id.textView3);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                accu.setVisibility(View.INVISIBLE);
                accout.setVisibility(View.INVISIBLE);
                startActivityForResult(intent, 100);
            }
        });
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(img!=null) {
                    img=Bitmap.createScaledBitmap(img, 64, 64, true);
                    try {
                        Model model = Model.newInstance(getApplicationContext());
                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                        tensorImage.load(img);
                        ByteBuffer byteBuffer = tensorImage.getBuffer();
                        inputFeature0.loadBuffer(byteBuffer);
                        // Runs model inference and gets result.
                        Model.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                       if(outputFeature0.getFloatArray()[0]==1.0){
                            output.setText("Yes Brain Tumor");
                        }
                        else{
                            output.setText("No Brain Tumor");
                        }
          //       output.setText(outputFeature0.getFloatArray()[0]+ "\n"+ outputFeature0.getFloatArray()[0]);
                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        // TODO Handle the exception
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Please Select Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.deve:
                Intent i = new Intent(MainActivity.this,Developerdetails.class);
                startActivity(i);
               // Toast.makeText(this,"Developer Details",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.about:
                Intent in = new Intent(MainActivity.this,settings.class);
                startActivity(in);
          //      Toast.makeText(this,"User Guide",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            imageView.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                img=MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}










