package com.eddie.tunneler;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Intent;

public class GameActivity extends Activity {
	GameView gView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean stop = getIntent().getExtras().getBoolean("stop");
		if(!stop){
		//fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		gView = new GameView(this);//,getIntent().getExtras().getInt("mode"));
		setContentView(gView);
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();

	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
    @Override
    protected void onNewIntent(Intent intent)
    {
    	super.onNewIntent(intent);
    	boolean stop = intent.getExtras().getBoolean("stop");
    	if(stop)
    	{
        	String scoreMsg = "Your Score: ";
        	scoreMsg += Long.toString(intent.getExtras().getLong("score"));
        	Toast.makeText(this,scoreMsg,Toast.LENGTH_LONG).show();
    		Intent showScore = new Intent(this, MenuActivity.class);
			showScore.putExtra("score", intent.getExtras().getLong("score"));
			this.startActivity(showScore);
    		finish();//GameActivity.this.finish();
    	}
	}
}
