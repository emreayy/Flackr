package eay.flackr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class Detail extends AppCompatActivity {

    Button button;
    ImageView image ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        init();
        req();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Detail.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void init(){
        button=findViewById(R.id.button);
        image=findViewById(R.id.image);
    }
    private void req(){
        Bundle extras = getIntent().getExtras();
        String link = extras.getString("send_string");
        Picasso.get().load(link).into(image);
    }

}