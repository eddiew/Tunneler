package com.eddie.tunneler;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.widget.Toast;
import android.content.Intent;

import com.eddie.tunneler.barrier.*;

import java.lang.Math;
import android.graphics.Paint;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.Random;
import android.os.Debug;

public class GameThread extends Thread {
	private SurfaceHolder gHolder = null;
	boolean running = false;
	private long gLastSec = 0,gLastTime = 0;
	private int width, height, mindim, maxdim, fps, ifps;
	private float speed, rspeed, x = 0f, y = 0f;
	public float xc,yc;
	private float vx = 0f, vy = 0f;
	private final float vmax = 0.5f;
	private final float r = 0.5f;
	private final int nBarriers = 4;
	private ArrayDeque<Barrier> barriers = new ArrayDeque<Barrier>();
	private long score = 0;
//	public boolean vRelax;
	Context context;
	Random rand;
//	private float playerAngle, triAngle, distFromCenterLine;
//	private Paint debugTextPaint;
	private Paint scorePaint, tubePaint;
	private final float startingSpeed = 1.0f;
	private final float lineStartDist = 0.2f;
	private float[][] endpoints;
	private float[] circledists;
	private final int nTubeCircles = 8;
	
	private Barrier getNewBarrier(float d){
		switch(rand.nextInt(2)){
		case 0:
			return new StaticBarrier(d);
		case 1:
			return new SpinningBarrier(d);
		default:
			return new SpinningBarrier(d);
		}
	}
	
	public GameThread(SurfaceHolder sHolder, Context context){
		this.context = context;
		this.gHolder = sHolder;
		ifps = fps = 0;
		rand = new Random();
//		debugTextPaint = new Paint();
//		debugTextPaint.setColor(0xffffffff);
		scorePaint = new Paint();
		scorePaint.setColor(0xffffff00);
		scorePaint.setTextSize(24);
		tubePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tubePaint.setColor(0xffffffff);
		tubePaint.setStyle(Paint.Style.STROKE);
		endpoints = new float[8][2];
		for(int n = 0; n < 8; n++){
			endpoints[n][0] = (float)Math.cos(Math.PI*n/4f+Math.PI/8f)/2f;
			endpoints[n][1] = (float)Math.sin(Math.PI*n/4f+Math.PI/8f)/2f;
		}
		circledists = new float[nTubeCircles];
		for(int n = 0; n < nTubeCircles; n++){
			circledists[n] = 16*(n+0.5f)/nTubeCircles;
		}
	}
	public void dostart(){
		synchronized(gHolder){
			gLastSec = gLastTime = System.currentTimeMillis()+100;//100 b/c the system is slow?
		}
		speed = startingSpeed;
		rspeed = (float)Math.PI/4f;
		for(int n = 1; n <= nBarriers; n++){
			barriers.add(getNewBarrier(4*n));
		}
//		vRelax = false;
	}
	@Override
	public void run(){
		//Debug.startMethodTracing("tunneler");
		while(running){
			Canvas c = null;
			try{
				c = gHolder.lockCanvas();
				if(width == 0){
					width = c.getWidth();
					height = c.getHeight();
					if(width > height){
						mindim = height;
						maxdim = width;
					}
					else{
						mindim = width;
						maxdim = height;
					}
					xc = width/2;
					yc = height/2;
				}
				synchronized(gHolder){
					long curTime = System.currentTimeMillis();
					float tw = (float)(curTime-gLastTime)/1000f;
					gLastTime = curTime;
					update(tw);
					draw(c);
					//debugStuff(c);
					ifps++;
					if(curTime > (gLastSec+1000)){
						gLastSec = curTime;
						fps = ifps;
						ifps = 0;
					}
				}
			}
			finally{
				if(c != null){
					gHolder.unlockCanvasAndPost(c);
				}
			}
		}
		//Debug.stopMethodTracing();
	}
	
	private void update(float secs){
		Iterator<Barrier> it = barriers.iterator();
		while(it.hasNext()){
			Barrier cB = it.next();
			cB.dist -= speed*secs;
			cB.rot += rspeed*secs;
			cB.rot %= 360;
		}
		for(int n= 0; n < nTubeCircles; n++){
			circledists[n] -= speed*secs;
		}
		if(circledists[0] < 0){
			for(int n = 0; n < nTubeCircles; n++){
				circledists[n] += 16f/nTubeCircles;
			}
		}
		if(barriers.getFirst().dist <= 0){
			if(barriers.getFirst().hit(x,y)){
				//game over
				running = false;
				Intent stopGame = new Intent(context, GameActivity.class);
				stopGame.putExtra("stop", true);
				stopGame.putExtra("score",score);
				context.startActivity(stopGame);
			}
			else if(barriers.getFirst().passed()){
				barriers.removeFirst();
				barriers.addLast(getNewBarrier(15.8f));
				score += 1;
				//Toast.makeText(context,"passed barrier",Toast.LENGTH_SHORT).show();
			}
		}
		vx = speed*(xc-width/2f)/mindim;//xc is from 0 to width, vx is from -0.5 to +0.5
		vy = speed*(yc-height/2f)/mindim;
		float v = (float)Math.sqrt(vx*vx+vy*vy);
		if(v > vmax*speed){
			vx *= speed*vmax/v;
			vy *= speed*vmax/v;
		}
		x += vx*secs;
		y += vy*secs;
		float d = (float)Math.sqrt(x*x+y*y);
		if(d > r){
			x *= r/d;
			y *= r/d;
		}
		speed += secs/30f;
		rspeed = speed*30f;
	}
	private void draw(Canvas c){
		//clear screen
		c.drawRGB(0,0,0);
		//draw tube mesh, the lines should fade with distance so they don't converge stupidly
		//tube mesh lines
		for(int n = 0; n < 8; n++){
			float xpos = width/2f + maxdim*(endpoints[n][0]-x)/(2f*lineStartDist);
			float ypos = height/2f + maxdim*(endpoints[n][1]-y)/(2f*lineStartDist);
			c.drawLine(width/2f, height/2f, xpos, ypos, tubePaint);
		}
		//tube mesh circles
		//for(int n = 0; n < nTubeCircles; n++){
		for(float cd : circledists){
			float xposc = width/2f - maxdim*x/(2f*cd);
			float yposc = height/2f - maxdim*y/(2f*cd);
			c.drawCircle(xposc, yposc, (float)0.5*maxdim/(2f*cd), tubePaint);
		}
		//draw barriers
		Iterator<Barrier> it = barriers.descendingIterator();
		while(it.hasNext()){
			//Barrier cB = it.next();
			it.next().draw(c,x,y,width,height,mindim,maxdim);
		}
		//draw score
		String scoreString = "Score: ";
		scoreString += Long.toString(score);
		c.drawText(scoreString,100,100,scorePaint);
		//draw fps
		String fpsString = "FPS: ";
		fpsString += Integer.toString(fps);
		c.drawText(fpsString, width-100,height-100, scorePaint);
	}
//	private void debugStuff(Canvas c){
//		if(x == 0){
//			if(y == 0) playerAngle = 0;
//			else playerAngle = (y>0?3:1)*(float)Math.PI/2f;
//		}
//		else{
//			playerAngle = (float)Math.atan(-y/x);
//			if(x < 0) playerAngle += Math.PI;
//			else if(y > 0) playerAngle += 2*Math.PI;
//		}
//		//playerAngle *= 180f/(float)Math.PI;
//		float nrot = barriers.getFirst().rot*(float)Math.PI/180f;
//		if(barriers.getFirst().rrot) nrot = 2*(float)Math.PI-nrot;
//		switch(barriers.getFirst().bType){
//		case 0:
//			nrot += Math.PI/8;//22.5f;
//			break;
//		case 1:
//			nrot -= Math.PI/8;
//			break;
//		}
//		triAngle = (nrot+playerAngle)%(float)Math.PI;
//		float rdist = (float)Math.sqrt(x*x+y*y);
//		distFromCenterLine = Math.abs(rdist*(float)Math.sin(triAngle));
//		//the above calculates player angle CCW from +x axis
//		//playerAngle *= -1;
//		//Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
//		String debugText = "PlayerAngle: ";
//		debugText += Float.toString(playerAngle);
//		String debugText2 = "triAngle: ";
//		debugText2 += Float.toString(triAngle);
//		String debugText3 = "distFromCenter: ";
//		debugText3 += Float.toString(distFromCenterLine);
//		c.drawText(debugText,100,100,debugTextPaint);
//		c.drawText(debugText2,100,150,debugTextPaint);
//		c.drawText(debugText3,100,200,debugTextPaint);
//	}
	public long getScore(){
		return score;
	}
}
