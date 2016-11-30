package com.example.galba.sendmails;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;


/**
 * Created by galba on 23/11/2016.
 */

public class SendMailsService extends JobService {

    SharedPreferences preferences = null;
    DateTimeZone timezone = DateTimeZone.forID("Asia/Jerusalem");

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static void start(Context context) {
        ComponentName componentName = new ComponentName(context, SendMailsService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(60 * 60 * 1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        String lastMailTime = preferences.getString("lastMailTime", "1987-06-24");
        LocalDateTime previous = LocalDateTime.parse(lastMailTime);
        LocalDateTime now = LocalDateTime.now(timezone);

        if (now.toLocalDate().isAfter(previous.toLocalDate()) & now.getHourOfDay() >= 7) {
            sendMail(params);
            return true;
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void sendMail(final JobParameters params) {


        final Mail mail = new Mail();
        mail.set_subject(getResources().getString(R.string.subject));
        mail.set_user(getResources().getString(R.string.user));
        mail.set_pass(getResources().getString(R.string.password));
        mail.set_from(getResources().getString(R.string.from));

        String[] recipients = {getResources().getString(R.string.destination)};
        mail.set_to(recipients);

        LocalDate date = LocalDate.now(timezone).minus(Period.days(8));
        mail.setBody(getResources().getString(R.string.body) + " " + date.toString());

        AsyncTask sendTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    mail.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalDateTime time = LocalDateTime.now(timezone);
                preferences.edit().putString("lastMailTime", time.toString()).apply();

                return true;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };

        sendTask.execute();
    }
}
