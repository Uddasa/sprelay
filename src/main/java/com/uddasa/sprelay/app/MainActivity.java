package com.uddasa.sprelay.app;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private String MAC_FILE_PATH = "/efs/wifi/.mac.info";
    private String mOriginalMAC = "";
    private WifiApManager wifiMan;
    private Handler handler = new Handler();
    private boolean isLandscape = false;

    // Plop
    // DONE on app exit, stop wifi
    // DONE keep wifi awake
    // TODO set mac file permissions
    // TODO import MAC addresses from text file
    // TODO add a selector of setting MAC methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Backup original MAC
        mOriginalMAC = getMAC();

        // Fill MAC list
        Spinner myspinner = (Spinner) findViewById(R.id.spinnerMAC);
        List<String> list = new ArrayList<String>();
        list.add(mOriginalMAC);
        list.add("4E:53:50:4F:4F:40");
        list.add("4E:53:50:4F:4F:41");
        list.add("4E:53:50:4F:4F:42");
        list.add("4E:53:50:4F:4F:43");
        list.add("4E:53:50:4F:4F:44");
        list.add("4E:53:50:4F:4F:45");
        list.add("4E:53:50:4F:4F:46");
        list.add("4E:53:50:4F:4F:47");
        list.add("4E:53:50:4F:4F:48");
        list.add("4E:53:50:4F:4F:49");
        list.add("4E:53:50:4F:4F:50");
        list.add("4E:53:50:4F:4F:51");
        list.add("4E:53:50:4F:4F:52");
        list.add("4E:53:50:4F:4F:53");
        list.add("4E:53:50:4F:4F:54");
        list.add("4E:53:50:4F:4F:55");
        list.add("4E:53:50:4F:4F:56");
        list.add("4E:53:50:4F:4F:57");
        list.add("4E:53:50:4F:4F:58");
        list.add("4E:53:50:4F:4F:59");
        list.add("4E:53:50:4F:4F:5A");
        list.add("4E:53:50:4F:4F:5B");
        list.add("4E:53:50:4F:4F:5C");
        list.add("4E:53:50:4F:4F:5D");
        list.add("4E:53:50:4F:4F:5E");
        list.add("4E:53:50:4F:4F:5F");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(dataAdapter);

        // Fill cooldown list
        Spinner myspinner2 = (Spinner) findViewById(R.id.spinnerCooldown);
        List<String> list2 = new ArrayList<String>();
        list2.add("1");
        list2.add("2");
        list2.add("3");
        list2.add("4");
        list2.add("5");
        list2.add("10");
        list2.add("15");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner2.setAdapter(dataAdapter2);

        // Create wifi AP manager
        wifiMan = new WifiApManager((WifiManager) this.getSystemService(Context.WIFI_SERVICE));

        updateUI();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (wifiMan.isWifiApEnabled() == true)
            stopWifi();
    }

    /*
    protected void onRestart() {
        super.onRestart();
        showMsg("onRestart");
    }
    protected void onStart() {
        super.onStart();
        showMsg("onStart");
    }
    protected void onResume() {
        super.onResume();
        showMsg("onResume");
    }
    protected void onPause() {
        super.onPause();
        showMsg("onPause");
    }
    protected void onStop() {
        super.onStop();
        showMsg("onStop");
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            isLandscape = false;
        }
        updateUI();
    }

    private void showMsg(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    public String getMAC() {
        String content = "";
        File file = new File(MAC_FILE_PATH);
        if (file.exists()) {
            try {
                InputStream istream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(istream);
                BufferedReader bufferedreader = new BufferedReader(inputreader);
                String line;
                while ((line = bufferedreader.readLine()) != null) {
                    content += line + "\n";
                }
                istream.close();
            }
            catch (IOException e) {
                showMsg("There was an error opening file " +  file.toString());
                e.printStackTrace();
            }
        }
        return content;
    }

    public void writeMAC(String inNewMAC) {
        File file = new File(MAC_FILE_PATH);
        if (file.exists()) {
            try {
                OutputStream ostream = new BufferedOutputStream(new FileOutputStream(file));
                OutputStreamWriter outputwriter = new OutputStreamWriter(ostream,"UTF-8");
                outputwriter.write(inNewMAC);
                outputwriter.flush();
                outputwriter.close();

                showMsg("Changed MAC successfully");
            }
            catch (IOException e) {
                showMsg("There was an error opening file " +  file.toString());
                e.printStackTrace();
            }
        }
        updateUI();
    }

    public void writeSelectedMAC() {
        Spinner myspinner = (Spinner) findViewById(R.id.spinnerMAC);
        String newMAC = String.valueOf( myspinner.getSelectedItem() );
        writeMAC(newMAC);
    }

    public void updateUI() {
        // Set current MAC
        String mac = "Current MAC     " + getMAC();
        TextView textmac = (TextView) findViewById(R.id.lblMAC);
        textmac.setText(mac);

        ImageButton btToggle = (ImageButton) findViewById(R.id.buttonToggleWifi);
        ImageButton btPause = (ImageButton) findViewById(R.id.buttonPause);
        ImageButton btNext = (ImageButton) findViewById(R.id.buttonNext);

        if (wifiMan.isWifiApEnabled()) {
            //btToggle.setText("Stop");
            btToggle.setImageResource(R.drawable.stop_512x512);
            btPause.setEnabled(true);
            btNext.setEnabled(true);
            btPause.setVisibility(View.VISIBLE);
            btNext.setVisibility(View.VISIBLE);
        }
        else {
            //btToggle.setText("Start");
            btToggle.setImageResource(R.drawable.play_512x512);
            btPause.setEnabled(false);
            btNext.setEnabled(false);
            btPause.setVisibility(View.INVISIBLE);
            btNext.setVisibility(View.INVISIBLE);
        }

        if (cyclePaused == true)
            btPause.setImageResource(R.drawable.play_512x512);
        else
            btPause.setImageResource(R.drawable.pause512x512);

        /*
        // Handle screen orientation
        LinearLayout layMedia = (LinearLayout) findViewById(R.id.layoutMedia);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layMedia.getLayoutParams();
        //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        if (isLandscape == false) {
            params.addRule(RelativeLayout.RIGHT_OF, R.id.lblCooldown);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.spinnerCooldown);
            //params.addRule(RelativeLayout.ABOVE, R.id.spinnerCooldown);
        }
        layMedia.setLayoutParams(params); //causes layout update
        */
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            cycleMAC();
            handler.postDelayed(this, getCoolDownTimeInMs());
        }
    };

    public void buttonToggleWifi(View v) {
        if (wifiMan.isWifiApEnabled() == false)
            startWifi();
        else
            stopWifi();
        updateUI();
    }

    public int getCoolDownTimeInMs() {
        Spinner myspinner = (Spinner) findViewById(R.id.spinnerCooldown);

        int time = 0;
        try {
            time = Integer.parseInt(String.valueOf( myspinner.getSelectedItem() ));
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        return ( time * 60 * 1000);
    }

    boolean cyclePaused = false;
    public void buttonPauseCycle(View v) {
        if (cyclePaused == false)
            handler.removeCallbacks(runnable);
        else
            handler.postDelayed(runnable, getCoolDownTimeInMs());

        cyclePaused = !cyclePaused;

        updateUI();
    }

    public void buttonNextMAC(View v) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 0);
        updateUI();
    }

    public void startWifi() {
        writeSelectedMAC();
        wifiMan.toggleWifi("attwifi");
        handler.postDelayed(runnable, getCoolDownTimeInMs());
    }

    public void stopWifi() {
        handler.removeCallbacks(runnable);
        wifiMan.toggleWifi("");
        restoreOriginalMAC();
    }

    long lastSelected = 0;
    public void cycleMAC() {
        // Turn OFF wifi
        wifiMan.toggleWifi("");

        Spinner myspinner = (Spinner) findViewById(R.id.spinnerMAC);

        // Increment currently selected and loop when at the end of MAC list
        if (lastSelected == 0)
            lastSelected = myspinner.getSelectedItemId();
        lastSelected++;
        if (lastSelected > myspinner.getCount())
            lastSelected = 1;
        myspinner.setSelection((int)lastSelected);

        // Set new MAC
        String newMAC = String.valueOf( myspinner.getSelectedItem() );
        writeMAC(newMAC);

        // Turn ON wifi
        wifiMan.toggleWifi("attwifi");
        updateUI();
    }

    public void restoreOriginalMAC() {
        writeMAC(mOriginalMAC);
    }

    public static boolean runChmod777(String myfile) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su -c chmod 0777 myfile");
            process.waitFor();
        } catch (Exception e) {
            //Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "+e.getMessage());
            return false;
        }
        finally {
            try {
                process.destroy();
            } catch (Exception e) {
                // nothing
            }
        }
        return true;
    }
}
