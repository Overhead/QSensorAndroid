package classes;

import com.example.qsensorapp.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Class that defines how each line in the list on "My Movies" page looks like
 * @author Tjarb
 *
 */
public class MovieItemView extends TextView {
	
	private Paint marginPaint;
	private Paint linePaint;
	private	int paperColor;
	private float margin;
	
	public MovieItemView (Context context, AttributeSet ats, int ds) {
		super(context,ats,ds);
		init();
	}
	
	public MovieItemView (Context context) {
		super(context);
		init();
	}
	
	public MovieItemView (Context context, AttributeSet attrs) {
		super(context,attrs);
		init();
	}
	
	private void init() {
		
		//Get reference to our resource table
		Resources myResources = getResources();
				
		//Create the paint brushes we will use in the Ondraw method
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.notepad_lines));
		
		//Get paper background and margin width
		paperColor = myResources.getColor(R.color.notepad_paper);
		margin = myResources.getDimension(R.dimen.notepad_margin);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(paperColor);
		
		canvas.drawLine(0,0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0,getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
		
		//Draw margin
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		
		//Move the text across from the margin
		canvas.save();
		canvas.translate(margin, 0);
		
		//use the textview to render the text
		super.onDraw(canvas);
		canvas.restore();	
	}	

}
