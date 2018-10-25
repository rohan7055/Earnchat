package com.rohan7055.earnchat.celgram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.rohan7055.earnchat.R;

/**
 * Created by Home Laptop on 03-Nov-16.
 */

public class Contact_View extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view);
		Intent intent=getIntent();
		final String Name=intent.getStringExtra("NAME");
		final String Number=intent.getStringExtra("NUMBER");

		TextView title = (TextView)findViewById(R.id.heading_txt);
		title.setText("Send contact");

		(( TextView)findViewById(R.id.name)).setText(Name);
		((TextView)findViewById(R.id.number)).setText(Number);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent data=new Intent();
				data.putExtra("ACTION","CANCEL");
				data.putExtra("NAME",Name);
				data.putExtra("NUMBER",Number);
				setResult(RESULT_OK,data);
				finish();
			}
		});
		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent data=new Intent();
				data.putExtra("ACTION","SEND");
				data.putExtra("NAME",Name);
				data.putExtra("NUMBER",Number);
				setResult(RESULT_OK,data);
				finish();
			}
		});
	}
}
