package com.myproject.asimion.bluechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    ImageView blueChatLogo ;
    ImageView nextButton;
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set main activity toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle1 = (TextView) toolbar.findViewById(R.id.toolbar_title_first);
        TextView mTitle2 = (TextView) toolbar.findViewById(R.id.toolbar_title_second);
        // add new fonts <google fonts>
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Pacifico-Regular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/Monoton-Regular.ttf");
        Typeface tf3 = Typeface.createFromAsset(getAssets(), "fonts/FredokaOne-Regular.ttf");
        mTitle1.setTypeface(tf1);
        mTitle2.setTypeface(tf2);

        setUpWidget(tf3);//setup layout components
        nextToApp(this);//go to second activity NEXT
        nextToAppLogo(this);//go to second activity using Logo

    }

    public void setUpWidget(Typeface tf){
        blueChatLogo = (ImageView)findViewById(R.id.logo);
        nextButton = (ImageView) findViewById(R.id.go);
        next = (TextView)findViewById(R.id.next);
        next.setTypeface(tf);
    }


    public void nextToApp(final Context context){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    public void nextToAppLogo(final Context context){
        blueChatLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

}

