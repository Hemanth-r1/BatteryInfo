package in.hr.batteryinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.result);
        loadBatterInfo();
    }

    private void loadBatterInfo() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        //Broadcast Action: External power has been removed from the device.
        // This is intended for applications that wish to register specifically to this notification.
        // Unlike ACTION_BATTERY_CHANGED, applications will be woken for this and so do not have to stay
        // active to receive this notification. This action can be used to implement actions
        // that wait until power is available to trigger.
        //This is a protected intent that can only be sent by the system.
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //register broadcast receiver with intent
        registerReceiver(batteryInfoReceiver, intentFilter);
    }

    private  BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryData(intent);
        }
    };

    private void updateBatteryData(Intent intent){
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        //boolean indicating whether a battery is present.
        if (present){

            StringBuilder batteryInfo = new StringBuilder();
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            //integer containing the current health constant.
            batteryInfo.append("health :" + health).append("\n");

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //integer field containing the current battery level, from 0 to EXTRA_SCALE.
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            // scale maximum battery level

            if (level != -1 && scale != -1){
                int batteryPercent = (int) ((level / scale ) * 100);
                batteryInfo.append("Battery Percentage :" + batteryPercent).append("%\n");
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            //integer indicating whether the device is plugged in to a power source;
            // 0 means it is on battery, other constants are different types of power sources.
            batteryInfo.append("Plugged :" + plugged).append("\n");

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            //integer containing the current status constant.
            batteryInfo.append("Status :" + status).append("\n");

            if (intent.getExtras() != null){
                String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                //String describing the technology of the current battery.
                batteryInfo.append("Technology :" + technology).append("\n");
            }

            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            //integer containing the current battery temperature.
            if (temperature >0) {
                batteryInfo.append("Temperature  :" + (temperature/10f) ).append("C\n");
            }

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            //integer containing the current battery voltage level.
            batteryInfo.append("Voltage :" + voltage).append("mV\n");

            //long capacity = intent.getIntExtra(String.valueOf(BatteryManager.BATTERY_PROPERTY_CAPACITY), 0);
            long capacity = getBatteryCapacity();
            batteryInfo.append("Capacity :" + capacity).append("mAh\n");

            result.setText(batteryInfo.toString());
        }
        else{
            Toast.makeText(MainActivity.this, "No Battery Present", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //Indicates that Lint should treat this type as targeting a given API level, no matter what the project target is.
    private long getBatteryCapacity(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            //Use with getSystemService(String) to retrieve a android.os.BatteryManager for managing battery state.
            long chargeCount = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            //Battery capacity in microampere-hours, as an integer.
            long capacity = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            //Remaining battery capacity as an integer percentage of total capacity (with no fractional part).

            long value = (long) ((chargeCount / capacity )* 100);
            return  value;

        }


        return  0;
    }

}