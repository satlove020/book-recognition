package com.recognize.book.itface;

import com.recognize.book.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

/**
 * Dialog for rating.
 */
public class RatingDialog extends Dialog {

	
	private String ratingScore;
	private ReadyListener readyListener;
	
	/**
	 * Constructor
	 * @param context
	 * @param readyListener
	 */
	public RatingDialog(Context context, ReadyListener readyListener) {
        super(context);
        this.readyListener = readyListener;
    }

	
	public interface ReadyListener {
        public void ready(String name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_dialog);
        
        /* Create rating bar*/
        RatingBar ratingBar = (RatingBar)findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){

		    public void onRatingChanged(RatingBar ratingBar, float rating,
		      boolean fromUser) {
		    	ratingScore =String.valueOf(rating);
		    }
		});
        
        /* Create button */
        Button buttonAccept = (Button) findViewById(R.id.accept);
        Button buttonCancel = (Button) findViewById(R.id.cancel);
        buttonAccept.setOnClickListener(new endListener());
        buttonCancel.setOnClickListener(new endListener());
      
    }
    
    /**
     * end listener
     *
     */
    private class endListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
        	readyListener.ready(ratingScore);
            RatingDialog.this.dismiss();
        }
    }

}