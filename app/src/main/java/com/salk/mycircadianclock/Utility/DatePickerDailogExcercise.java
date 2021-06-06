package com.salk.mycircadianclock.Utility;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.wheel.OnWheelChangedListener;
import com.salk.mycircadianclock.wheel.WheelView;
import com.salk.mycircadianclock.wheel.adapters.ArrayWheelAdapter;
import com.salk.mycircadianclock.wheel.adapters.NumericWheelAdapter;

import java.util.Calendar;
import java.util.Locale;

@SuppressLint("ResourceAsColor")
public class DatePickerDailogExcercise extends Dialog {

	private Context Mcontex;
	String language;

	public DatePickerDailogExcercise(Context context,
                                     final Calendar calendar, final DatePickerListner dtp) {

		super(context);
		Mcontex = context;
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.wheel_sleep_view, null);

		final Calendar cal = Calendar.getInstance();
		final TextView txt_msg = view.findViewById(R.id.titleslw);
		TextView btncancel =view.findViewById(R.id.cancelbuttonslpwh);
		TextView btnset = view.findViewById(R.id.donebuttonslpwh);
		btncancel.setTypeface(Typeface.DEFAULT_BOLD);
		btnset.setTypeface(Typeface.DEFAULT_BOLD);
		LinearLayout lytdate = view.findViewById(R.id.whcontainer);
		
		final WheelView month = new WheelView(Mcontex);
		final WheelView day = new WheelView(Mcontex);
		final WheelView year = new WheelView(Mcontex);

		language = Locale.getDefault().getDisplayLanguage();

		lytdate.addView(day, new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		lytdate.addView(month, new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		
		lytdate.addView(year, new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		requestWindowFeature(Window.FEATURE_NO_TITLE);


		setContentView(view);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);

		getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				
				
				if((month.getCurrentItem() >= cal.get(Calendar.MONTH)))
				{
					day.setCurrentItem(cal.get(Calendar.DATE) - 1);
					month.setCurrentItem(cal.get(Calendar.MONTH));
				}
				
				
				
			
				updateDays(month,day,year);
			}
		};
		day.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub

				if ((day.getCurrentItem() > cal.get(Calendar.DATE) - 1) && (month.getCurrentItem() == cal.get(Calendar.MONTH))) {
					day.setCurrentItem(cal.get(Calendar.DATE) - 1);
				}
				
				updateDays(month, day, year);
			}
		});
		year.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				int i=year.getCurrentItem();
				int j=cal.get(Calendar.YEAR);
				
				if((year.getCurrentItem() > cal.get(Calendar.YEAR)))
				{
					year.setCurrentItem(cal.get(Calendar.YEAR));
				}
				
				
				updateDays(month, day, year);
			}
		});

		
		// month
		int curMonth = cal.get(Calendar.MONTH);
		
		if(language.matches("français")){

			String months[] = new String[]  { "jan", "fév", "mar", "avr", "mai",
					"juin", "juil", "Août", "sept", "oct", "nove", "déc" };
			month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
		}else{
			String months[] = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
					"Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec" };
			month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
		}

		
		
		
		String s[] = new String[] { "AM", "PM" };

		
		month.setCurrentItem(curMonth);
		month.addChangingListener(listener);

		int curYear = cal.get(Calendar.YEAR);
		
		year.setViewAdapter(new DateNumericAdapter(context, curYear, curYear, 0));
		year.setCurrentItem(cal.get(Calendar.YEAR));

		// day
		updateDays(month, day, year);
		day.setCurrentItem(cal.get(Calendar.DATE) - 1);

		btnset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				Calendar c = updateDays(month, day, year);
				dtp.OnDoneButton(DatePickerDailogExcercise.this, c);
			}
		});
		btncancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dtp.OnCancelButton(DatePickerDailogExcercise.this);

			}
		});

	}

	Calendar updateDays(WheelView month, WheelView day, WheelView year
			) {
		Calendar calendar = Calendar.getInstance();
		// calendar.set(Calendar.YEAR);
		calendar.set(Calendar.MONTH, month.getCurrentItem());
		
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar
				.get(Calendar.DAY_OF_MONTH) - 1));
		
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		
		day.setCurrentItem(curDay - 1, true);
		calendar.set(Calendar.DATE, curDay);

		return calendar;

	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;
		int currentValue;

		public DateNumericAdapter(Context context, int minValue, int maxValue,
                                  int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		int currentItem;
		int currentValue;

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface DatePickerListner {
		public void OnDoneButton(Dialog datedialog, Calendar c);

		public void OnCancelButton(Dialog datedialog);
	}
}
