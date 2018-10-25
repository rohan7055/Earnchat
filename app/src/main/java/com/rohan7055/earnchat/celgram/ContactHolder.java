package com.rohan7055.earnchat.celgram;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan7055.earnchat.R;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Home Laptop on 03-Nov-16.
 */

public class ContactHolder extends RecyclerView.ViewHolder
{
	private TextView sender,timestamp,day,msg_contact,save_contact,contact_name,contact_number;
	private EmojiTextView celgram_name;
	private CircleImageView sender_image;
	private ImageView msg_status;

	public ContactHolder(View view)
	{
		super(view);

		sender=(TextView)view.findViewById(R.id.sender_name);
		timestamp=(TextView)view.findViewById(R.id.timestamp);
		day=(TextView)view.findViewById(R.id.day);
		msg_contact=(TextView)view.findViewById(R.id.msg_contact);
		save_contact=(TextView)view.findViewById(R.id.save_contact);
		contact_name=(TextView)view.findViewById(R.id.contact_name);
		contact_number=(TextView)view.findViewById(R.id.contact_number);
		celgram_name=(EmojiTextView)view.findViewById(R.id.celgram_name);
		sender_image=(CircleImageView) view.findViewById(R.id.sender_image);
		msg_status=(ImageView)view.findViewById(R.id.msg_status);
	}


	public TextView getSender() {
		return sender;
	}

	public TextView getTimestamp() {
		return timestamp;
	}

	public TextView getDay() {
		return day;
	}

	public TextView getMsg_contact() {
		return msg_contact;
	}

	public TextView getSave_contact() {
		return save_contact;
	}

	public TextView getContact_name() {
		return contact_name;
	}

	public TextView getContact_number() {
		return contact_number;
	}

	public EmojiTextView getCelgram_name() {
		return celgram_name;
	}

	public CircleImageView getSender_image() {
		return sender_image;
	}

	public ImageView getMsg_status() {
		return msg_status;
	}
}
