package com.eddie.tunneler;

import android.view.SurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.MotionEvent;
import android.content.Context;
import android.widget.Toast;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

public class GameView extends SurfaceView implements Callback{
	Context ctxt;
	GameThread gThread;
	private float centerX, centerY;
	private final float ctrlDiam = 360;
	private int w, h;
	public static enum SteeringMode{
		FOLLOW_THUMB, MOVEABLE_JOYSTICK, STATIC_JOYSTICK
	}
	private int sMode = 0;
	
	public GameView(Context context){//, int smode){
		super(context);
		this.ctxt = context;
		//this.sMode = smode;
		getHolder().addCallback(this);
		gThread = new GameThread(getHolder(), ctxt);
		setFocusable(true);
		Point maxDims = new Point();//is this REALLY necessary?
		((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(maxDims);
		w = maxDims.x;
		h = maxDims.y;
		gThread.dostart();
//		String msg = "";
//		msg += Float.toString(w);
//		msg += ',';
//		msg += Float.toString(h);
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
		//i don't know why
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		gThread.running = true;
		gThread.start();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
		boolean retry = true;
		gThread.running = false;
		while(retry){
			try{
				gThread.join();
				retry = false;
			}
			catch(InterruptedException e){}
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent me){
		switch(sMode){
		case 1:
			switch(me.getAction()){
			case MotionEvent.ACTION_UP:
	//			gThread.vRelax = true;
				gThread.xc = w/2;
				gThread.yc = h/2;
				break;
			case MotionEvent.ACTION_DOWN:
				centerX = me.getX();
				centerY = me.getY();
			default:
				gThread.xc = w/2 + (me.getX()-centerX)*4;
				gThread.yc = h/2 + (me.getY()-centerY)*4;
				break;
			}
			break;
		default:
		case 0:
			switch(me.getAction()){
			case MotionEvent.ACTION_UP:
				gThread.xc = w/2;
				gThread.yc = h/2;
				break;
			default:
				gThread.xc = me.getX();
				gThread.yc = me.getY();
				break;
			}
			break;
		}
		return true;
	}
}
