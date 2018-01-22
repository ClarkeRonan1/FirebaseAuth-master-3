package info.androidhive.firebase;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ImageProcessing extends AppCompatActivity
{
    ImageView imageView;
    DatabaseReference databaseReference;

    Drawable drawable;
    List<Task> allTask;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.print("Part 1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        allTask = new ArrayList<Task>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Button addTaskButton = (Button)findViewById(R.id.add_task_button);
        //addTaskButton.setOnClickListener(new View.OnClickListener() {
           // @Override
          //  public void onClick(View view) {

            //}
       // });
        //imageView = (ImageView) findViewById(R.id.imageView);


        drawable = ContextCompat.getDrawable(this,R.drawable.eye1);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        Bitmap newBitmap = convertImage(bitmap);
        imageView.setImageBitmap(newBitmap);

    }

    public static Bitmap convertImage(Bitmap original)
    {
        System.out.print("Part 2");
        Bitmap finalImage = Bitmap.createBitmap(original.getWidth(),original.getHeight(),original.getConfig());

        int A,R,G,B;
        int colorPixel;
        int width = original.getWidth();
        int height = original.getHeight();

        for(int x = 0; x < height; x++)
        {
            for(int y = 0; y < width; y++)
            {
                colorPixel = original.getPixel(x,y);
                A = Color.alpha(colorPixel);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R = (R+G+B) /3;
                G = R;
                B = R;

                finalImage.setPixel(x,y, Color.argb(A,R,G,B));
                System.out.print(R);

            }
        }
        return finalImage;
    }
}
