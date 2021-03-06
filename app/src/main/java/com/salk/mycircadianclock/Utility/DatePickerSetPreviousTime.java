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

public class DatePickerSetPreviousTime extends Dialog {

    private Context Mcontex;
    final Calendar cal;
    String language;

    public DatePickerSetPreviousTime(Context context, String slept_status,
                                     final Calendar calendar, final DatePickerListner dtp) {

        super(context);
        Mcontex = context;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.wheel_sleep_view, null);
        language = Locale.getDefault().getDisplayLanguage();
        if (calendar != null) {
            cal = calendar;
        } else {
            cal = Calendar.getInstance();
        }

        final TextView txt_msg = view.findViewById(R.id.titleslw);
        TextView btncancel = view.findViewById(R.id.cancelbuttonslpwh);
        TextView btnset = view.findViewById(R.id.donebuttonslpwh);
        btncancel.setTypeface(Typeface.DEFAULT_BOLD);
        btnset.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout lytdate = view.findViewById(R.id.whcontainer);


        if (slept_status.equalsIgnoreCase(Mcontex.getResources().getString(R.string.Select_your_sleep_time))) {
            txt_msg.setText(Mcontex.getResources().getString(R.string.Select_your_sleep_time));
            txt_msg.setPadding(10, 0, 0, 0);
        } else {
            txt_msg.setText(Mcontex.getResources().getString(R.string.I_woke_up_at));
            txt_msg.setPadding(0, 0, 0, 0);
        }


        final WheelView month = new WheelView(Mcontex);
        final WheelView day = new WheelView(Mcontex);
        final WheelView hour = new WheelView(Mcontex);
        final WheelView min = new WheelView(Mcontex);
        final WheelView AMORPM = new WheelView(Mcontex);


        lytdate.addView(day, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        lytdate.addView(month, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        lytdate.addView(hour, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        lytdate.addView(min, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        lytdate.addView(AMORPM, new LayoutParams(
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
                hour.setEnabled(true);

                System.out.println(hour.getCurrentItem());
                System.out.println(min.getCurrentItem());
                System.out.println(AMORPM.getCurrentItem());
                System.out.println(day.getCurrentItem());


                if ((month.getCurrentItem() > cal.get(Calendar.MONTH) || month.getCurrentItem() < cal.get(Calendar.MONTH))) {

                    month.setCurrentItem(cal.get(Calendar.MONTH));

                } else {
                    updateDays(month, day, hour, min, AMORPM);
                }
                updateDays(month, day, hour, min, AMORPM);

            }
        };
        day.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub

                updateDays(month, day, hour, min, AMORPM);


            }
        });

        int curMonth = cal.get(Calendar.MONTH);
        String s[] = new String[]{"AM", "PM"};

        if (language.matches("fran??ais")) {

            String months[] = new String[]{"jan", "f??v", "mar", "avr", "mai",
                    "juin", "juil", "Ao??t", "sept", "oct", "nove", "d??c"};
            month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
            hour.setViewAdapter(new DateNumericAdapter(Mcontex, 00, 23, cal
                    .get(Calendar.HOUR_OF_DAY)));
            AMORPM.setVisibility(View.GONE);
        } else {
            String months[] = new String[]{"Jan", "Feb", "Mar", "Apr", "May",
                    "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
            month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
            hour.setViewAdapter(new DateNumericAdapter(Mcontex, 01, 12, cal
                    .get(Calendar.HOUR)));
            if (cal.get(Calendar.HOUR) == 0) {
                hour.setCurrentItem(11);
            } else {
                hour.setCurrentItem(cal.get(Calendar.HOUR) - 1);
            }

            AMORPM.setViewAdapter(new DateArrayAdapter(Mcontex, s, cal
                    .get(Calendar.AM_PM)));
            AMORPM.setCurrentItem(cal.get(Calendar.AM_PM));
            AMORPM.addChangingListener(listener);
        }


        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);

        hour.addChangingListener(listener);

        String minutes[] = new String[60];
        for (int i = 0; i <= 59; i++) {
            if (i < 10) {
                minutes[i] = "0" + i;
            } else {
                minutes[i] = String.valueOf(i);
            }
        }
        min.setViewAdapter(new DateArrayAdapter(context, minutes, cal
                .get(Calendar.MINUTE)));
        min.setCurrentItem(cal.get(Calendar.MINUTE));
        min.addChangingListener(listener);

        AMORPM.setViewAdapter(new DateArrayAdapter(Mcontex, s, cal
                .get(Calendar.AM_PM)));
        AMORPM.setCurrentItem(cal.get(Calendar.AM_PM));
        AMORPM.addChangingListener(listener);

        // day
        updateDays(month, day, hour, min, AMORPM);
        day.setCurrentItem(cal.get(Calendar.DATE) - 1);
        month.setCurrentItem(cal.get(Calendar.MONTH));

        min.setCurrentItem(cal.get(Calendar.MINUTE));
        AMORPM.setCurrentItem(cal.get(Calendar.AM_PM));

        btnset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Calendar c = updateDays(month, day, hour, min, AMORPM);
                dtp.OnDoneButton(DatePickerSetPreviousTime.this, c);
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dtp.OnCancelButton(DatePickerSetPreviousTime.this);

            }
        });

    }

    Calendar updateDays(WheelView month, WheelView day, WheelView hour,
                        WheelView min, WheelView am_pm) {
        Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.YEAR);
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar
                .get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
        calendar.set(Calendar.DATE, curDay);


        if (language.matches("fran??ais")) {

            calendar.set(Calendar.HOUR, hour.getCurrentItem());
            calendar.set(Calendar.AM_PM, am_pm.getCurrentItem());

        } else {
            if (hour.getCurrentItem() + 1 == 12 && am_pm.getCurrentItem() == 1) {
                calendar.set(Calendar.HOUR_OF_DAY, hour.getCurrentItem() + 1);

            } else if (hour.getCurrentItem() + 1 == 12 && am_pm.getCurrentItem() == 0) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);

            } else {
                calendar.set(Calendar.HOUR, hour.getCurrentItem() + 1);
                calendar.set(Calendar.AM_PM, am_pm.getCurrentItem());
            }
        }
        calendar.set(Calendar.MINUTE, min.getCurrentItem());

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
                //view.setTextColor(0xFF0000F0);
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

    public int getMonth(String month) {
        int result = 0;
        if (month.equalsIgnoreCase("Jan")) {
            result = 0;
        } else if (month.equalsIgnoreCase("Feb")) {
            result = 1;
        } else if (month.equalsIgnoreCase("Mar")) {
            result = 2;
        } else if (month.equalsIgnoreCase("Apr")) {
            result = 3;
        } else if (month.equalsIgnoreCase(Mcontex.getResources().getString(R.string.May))) {
            result = 4;
        } else if (month.equalsIgnoreCase("Jun")) {
            result = 5;
        } else if (month.equalsIgnoreCase("Jul")) {
            result = 6;
        } else if (month.equalsIgnoreCase("Aug")) {
            result = 7;
        } else if (month.equalsIgnoreCase("Sep")) {
            result = 8;
        } else if (month.equalsIgnoreCase("Oct")) {
            result = 9;
        } else if (month.equalsIgnoreCase("Nov")) {
            result = 10;
        } else {
            result = 11;
        }

        return result;
    }
}
