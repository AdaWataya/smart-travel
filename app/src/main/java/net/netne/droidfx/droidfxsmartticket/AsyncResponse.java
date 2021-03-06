package net.netne.droidfx.droidfxsmartticket;

/**
 * Created by Oum Saokosal (2016)
 * Source Code at https://github.com/kosalgeek/generic_asynctask_v2
 * If you have any questions or bugs, drop a comment on
 * my Facebook Page https://facebook.com/kosalgeek or
 * Twitter https://twitter.com/kosalgeek
 * Watch video tutorial at https://youtube.com/user/oumsaokosal
 */
public interface AsyncResponse {
    void process(String result);
    void processPin(String result);
    void processFinish(String result);
}
