package com.eddie.tunneler.barrier;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.PointF;
import java.util.ArrayList;

public class SpinningBarrier extends Barrier {
	private ArrayList<PointF> sidePoints;
	public SpinningBarrier(){
		super();
		bType = gen.nextInt(3);
		rot = (float) gen.nextInt(360);
		rrot = gen.nextBoolean();
		sidePoints = new ArrayList<PointF>();
		switch(bType){
		case 0://bar
			bShape.addArc(new RectF(-0.5f,-0.5f,0.5f,0.5f), 0, 45);
			bShape.lineTo(-0.5f,0);
			bShape.arcTo(new RectF(-0.5f,-0.5f,0.5f,0.5f), 180, 45);
			bShape.lineTo(0.5f,0);
			bColor = 0xffff0000;
			bFillColor = 0xff660000;
			sidePoints.add(new PointF(0.35355339059f, 0.35355339059f));
			sidePoints.add(new PointF(-0.5f,0));
			sidePoints.add(new PointF(-0.35355339059f, -0.35355339059f));
			sidePoints.add(new PointF(0.5f,0));
			break;
		case 1://bar removed
			bShape.addArc(new RectF(-0.5f,-0.5f,0.5f,0.5f), 0, 135);
			bShape.lineTo(0.5f,0);
			bShape.addArc(new RectF(-0.5f,-0.5f,0.5f,0.5f), 180, 135);
			bShape.lineTo(-0.5f,0);
			bColor = 0xffff8000;
			bFillColor = 0xff663300;
			sidePoints.add(new PointF(0.35355339059f, -0.35355339059f));
			sidePoints.add(new PointF(-0.5f,0));
			sidePoints.add(new PointF(-0.35355339059f, 0.35355339059f));
			sidePoints.add(new PointF(0.5f,0));
			break;
		case 2://90 deg wedge removed
			bShape.addArc(new RectF(-0.5f,-0.5f,0.5f,0.5f), 0, 270);
			bShape.lineTo(0,0);
			bShape.lineTo(0.5f,0);
			bColor = 0xffffff00;
			bFillColor = 0xff666600;
			sidePoints.add(new PointF(0.5f,0));
			sidePoints.add(new PointF(0,0));
			sidePoints.add(new PointF(0,0));
			sidePoints.add(new PointF(0,-0.5f));
			break;
		}
		bFillPaint.setColor(bFillColor);
		bPaint.setColor(bColor);
	}
	public SpinningBarrier(float dist){
		this();
		this.dist = dist;
	}
	
	@Override
	public void draw(Canvas c, float x, float y, int cw, int ch, int mindim, int maxdim){//x and y range from -0.5 to +0.5
		//compute center
		float xposb = cw/2f - maxdim*x/(2f*(dist+bThickness));
		float yposb = ch/2f - maxdim*y/(2f*(dist+bThickness));
		float bscale = maxdim/(2f*(dist+bThickness));
		float nrot = rot;
		if(rrot)nrot *= -1;
		Path sidePath1 = new Path(), sidePath2 = new Path();
		ArrayList<PointF> bpts = new ArrayList<PointF>();
		for(PointF p : sidePoints){
			bpts.add(new PointF(p.x, p.y));
		}
		//scaling
		for(PointF p : bpts){
			p.x *= bscale;
			p.y *= bscale;
		}
		//rotation
		float si = (float)Math.sin(Math.toRadians(nrot));
		float co = (float)Math.cos(Math.toRadians(nrot));
		for(PointF p : bpts){
			float nx= p.x*co - p.y*si;
			float ny = p.x*si + p.y*co;
			p.x = nx;
			p.y = ny;
		}
		//translation
		for(PointF p : bpts){
			p.x += xposb;
			p.y += yposb;
		}
		if(dist > 0){
			float xpos = cw/2f - maxdim*x/(2f*dist);
			float ypos = ch/2f - maxdim*y/(2f*dist);
			float fscale = maxdim/(2f*dist);
			Path fPath = new Path(bShape);
			Matrix fMatrix = new Matrix();
			ArrayList<PointF> fpts = new ArrayList<PointF>();
			for(PointF p : sidePoints){
				fpts.add(new PointF(p.x, p.y));
			}
			//scaling
			for(PointF p : fpts){
				p.x *= fscale;
				p.y *= fscale;
			}
			//rotation
			for(PointF p : fpts){
				float nx= p.x*co - p.y*si;
				float ny = p.x*si + p.y*co;
				p.x = nx;
				p.y = ny;
			}
			//translation
			for(PointF p : fpts){
				p.x += xpos;
				p.y += ypos;
			}
			sidePath1.moveTo(fpts.get(0).x, fpts.get(0).y);
			sidePath1.lineTo(fpts.get(1).x, fpts.get(1).y);
			sidePath1.lineTo(bpts.get(1).x, bpts.get(1).y);
			sidePath1.lineTo(bpts.get(0).x, bpts.get(0).y);
			sidePath1.close();
			sidePath2.moveTo(fpts.get(2).x, fpts.get(2).y);
			sidePath2.lineTo(fpts.get(3).x, fpts.get(3).y);
			sidePath2.lineTo(bpts.get(3).x, bpts.get(3).y);
			sidePath2.lineTo(bpts.get(2).x, bpts.get(2).y);
			sidePath2.close();
			c.drawPath(sidePath1, bFillPaint);
			c.drawPath(sidePath1, bPaint);
			c.drawPath(sidePath2, bFillPaint);
			c.drawPath(sidePath2, bPaint);
			//Front Face
			fMatrix.setScale(fscale, fscale);
			fPath.transform(fMatrix);
			fMatrix.setRotate(nrot);
			fPath.transform(fMatrix);
			fMatrix.setTranslate(xpos,ypos);
			fPath.transform(fMatrix);
			//fill type
			fPath.setFillType(Path.FillType.EVEN_ODD);
			//draw front face
			c.drawPath(fPath,bFillPaint);
			c.drawPath(fPath,bPaint);
		}
		else{
			//draw stubs for partially-visible side paths
			
		}
	}
	
	@Override
	public boolean hit (float x, float y){//-0.5 to 0.5, y is flipped
		float playerAngle;
		if(x == 0){
			if(y == 0) playerAngle = 0;
			else playerAngle = (y>0?3:1)*(float)Math.PI/2f;
		}
		else{
			playerAngle = (float)Math.atan(-y/x);
			if(x < 0) playerAngle += Math.PI;
			else if(y > 0) playerAngle += 2*Math.PI;
		}
		float nrot = rot*(float)Math.PI/180f;
		if(rrot) nrot = 2*(float)Math.PI-nrot;
		switch(bType){
		case 0://flow into case 1
		case 1:
			switch(bType){
			case 0:
				nrot += Math.PI/8;//22.5f;
				break;
			case 1:
				nrot -= Math.PI/8;
				break;
			}
			float triAngle = (nrot+playerAngle)%(float)Math.PI;
			float rdist = (float)Math.sqrt(x*x+y*y);
			float distFromCenterLine = Math.abs(rdist*(float)Math.sin(triAngle));
			if(bType == 0) return distFromCenterLine <= 0.191342f;
			return distFromCenterLine >= 0.191342f;
		case 2:
			float triAngle2 = (nrot+playerAngle)%(2*(float)Math.PI);
			//assert(triAngle2 >= 0);
			return triAngle2 >= Math.PI/2f;//something's not right here
		}
		return false;
	}
}
