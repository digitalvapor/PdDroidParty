package cx.mccormick.pddroidparty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.MotionEvent;
import android.util.Log;

public class Slider extends Widget {
	private static final String TAG = "Slider";
	private PdDroidPatchView parent;
	private int screenwidth;
	private int screenheight;
	
	float min, max, val;
	int log, init;
	String send, recv, labl;
	
	RectF dRect;
	Paint paint = new Paint();
	boolean orientation_horizontal = true;
	
	public Slider(PdDroidPatchView app, String[] atomline, boolean horizontal) {
		parent = app;
		orientation_horizontal = horizontal;
		
		screenwidth = parent.getWidth();
		screenheight = parent.getHeight();
		
		x = Float.parseFloat(atomline[2]) / parent.patchwidth * screenwidth;
		y = Float.parseFloat(atomline[3]) / parent.patchheight * screenheight;
		w = Float.parseFloat(atomline[5]) / parent.patchwidth * screenwidth;
		h = Float.parseFloat(atomline[6]) / parent.patchheight * screenheight;
		
		min = Float.parseFloat(atomline[7]);
		max = Float.parseFloat(atomline[8]);
		log = Integer.parseInt(atomline[9]);
		init = Integer.parseInt(atomline[10]);
		send = atomline[11];
		recv = atomline[12];
		labl = atomline[13];
		val = (Float.parseFloat(atomline[21]) / 100) / w;
		
		// listen out for floats from Pd
		parent.app.registerReceiver(recv, this);
		
		// graphics setup
		
		dRect = new RectF(Math.round(x), Math.round(y), Math.round(x + w), Math.round(y + h));
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(dRect, 3, 3, paint);
		if (orientation_horizontal) {
			canvas.drawLine(Math.round(x + (val / (max - min)) * w), Math.round(y + 2), Math.round(x + (val / (max - min)) * w), Math.round(y + h - 2), paint);
		} else {
			canvas.drawLine(Math.round(x + 2), Math.round(y + (val / (max - min)) * h), Math.round(x + w - 2), Math.round(y + (val / (max - min)) * h), paint);
		}
	}
	
	public void touch(MotionEvent event) {
		float ex = event.getX();
		float ey = event.getY();
		if (inside(ex, ey)) {
			if (orientation_horizontal) {
				val = (((ex - x) / w) * (max - min) + min);
			} else {
				val = (((ey - y) / h) * (max - min) + min);
			}
			//Log.e(TAG, "touch:" + val);
			if (event.getAction() == event.ACTION_DOWN || event.getAction() == event.ACTION_MOVE) {
				parent.app.send(send, "" + val);
			} else if (event.getAction() == event.ACTION_UP) {
			}
		}
	}
	
	public void receiveFloat(float v) {
		val = Math.min(max, Math.max(v, min));
	}
}

