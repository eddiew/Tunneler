package com.eddie.tunneler;

import com.eddie.tunneler.GameView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Spinner;

public class MenuActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public void startGame(View v){
		//int smode = ((Spinner)findViewById(R.id.control_select)).getSelectedItemPosition();
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("stop",false);
		//intent.putExtra("mode",smode);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		updateScore();
	}
	
	public void updateScore(){
		
	}
	
    @Override
    protected void onNewIntent(Intent intent)
    {
    	super.onNewIntent(intent);
    	long score = intent.getExtras().getLong("score");
    	//eventually change this to a dialog fragment with high scores
//    	String scoreMsg = "Your Score: ";
//    	scoreMsg += Long.toString(score);
//    	Toast.makeText(this,scoreMsg,Toast.LENGTH_LONG).show();
	}
}
