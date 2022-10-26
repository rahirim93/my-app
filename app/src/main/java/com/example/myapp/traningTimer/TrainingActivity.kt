package com.example.myapp.traningTimer

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.traningTimer.traningService.Actions
import com.example.myapp.traningTimer.traningService.EndlessService
import com.example.myapp.traningTimer.traningService.ServiceState
import com.example.myapp.traningTimer.traningService.getServiceState
import java.util.*

const val SET_ALARM = "setAlarm"
const val BROADCAST_ACTION = "broadcastAction"
const val SHARED_PREFERENCES_NAME = "name"
const val SHARED_TRAINING_TIME = "training time"

class TrainingActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var notificationManager: NotificationManager

    private var timeTraining = 0

    private lateinit var editTextTime: EditText
    private lateinit var buttonTime: Button

     private lateinit var alarmManager: AlarmManager

    private lateinit var pendingIntent: PendingIntent // Для поиска будильника


    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button
    private lateinit var button: Button

    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private lateinit var mSensorManager: SensorManager
    private lateinit var mOrientation: Sensor

    private var xyAngle = 0f
    private var xzAngle = 0f
    private var zyAngle = 0f

    private lateinit var xyView: TextView
    private lateinit var xzView: TextView
    private lateinit var zyView: TextView

    private var sharedPreferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traning)

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        editTextTime = findViewById(R.id.editTextTime)
        editTextTime.setText(sharedPreferences?.getInt(SHARED_TRAINING_TIME, 0).toString())

        buttonTime = findViewById(R.id.buttonTime)
        buttonTime.setOnClickListener {
            val editor = sharedPreferences?.edit()
            editor?.putInt(SHARED_TRAINING_TIME, editTextTime.text.toString().toInt())
            editor?.apply()
        }


        button = findViewById(R.id.buttonRing)
        button.setOnClickListener {
            val a = RingtoneManager(this)
            val cursor = a.cursor
            for (i in 0..cursor.count) {
                Log.d("TAG", cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX))
                Log.d("TAG", "${a.getRingtoneUri(i)}")
                cursor.moveToNext()
            }
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


        buttonStart = findViewById(R.id.buttonStart)
        buttonStart.setOnClickListener {
            actionOnService(Actions.START)
        }



        val filter = IntentFilter(BROADCAST_ACTION)
        registerReceiver(receiver, filter)

        actionOnService(Actions.START)


        buttonStop = findViewById(R.id.buttonStop)
        buttonStop.setOnClickListener {
            actionOnService(Actions.STOP)
            try {
                alarmManager.cancel(pendingIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Исключение при отключении будильника", Toast.LENGTH_SHORT).show()
            }
        }

        notificationManagerCompat = NotificationManagerCompat.from(this)


        xyView = findViewById(R.id.xyValue)
        xzView = findViewById(R.id.xzValue)
        zyView = findViewById(R.id.zyValue)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        changeMuteMode()
    }



    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val startAlarm = intent?.getIntExtra(SET_ALARM, 0)
            if (startAlarm == 100) {
                    Toast.makeText(context, "Сработала тревога", Toast.LENGTH_SHORT).show()
                    setTestAlarm()
                }
        }
    }

    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            //log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

    private fun changeMuteMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !notificationManager.isNotificationPolicyAccessGranted
        ) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )
            startActivity(intent)
        }
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val a = am.ringerMode
        if (a == 0) {
            am.ringerMode = 2
            Toast.makeText(this, "Режим: $a", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            xyAngle = event.values[0]  //Плоскость XY
            xzAngle = event.values[1] //Плоскость XZ
            zyAngle = event.values[2] //Плоскость ZY
        }

        xyView.text = xyAngle.toInt().toString()
        xzView.text = xzAngle.toInt().toString()
        zyView.text = zyAngle.toInt().toString()
    }


    private fun setTestAlarm() {
        if (sharedPreferences != null) {
            timeTraining = sharedPreferences!!.getInt(SHARED_TRAINING_TIME, 0)
        }
        editTextTime.setText(timeTraining.toString())
        val calendar = Calendar.getInstance().timeInMillis + (timeTraining * 1000)
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = calendar

        //timeToAlarm = calendar2.timeInMillis

        val intent = Intent(this, EndlessService::class.java)
        intent.action = Actions.PLAY.name
        pendingIntent = PendingIntent.getService(
            this,
            0,
            intent,
            0
        )



        setAlarm(calendar2, pendingIntent)


        val intent2 = Intent(this, EndlessService::class.java)
        intent2.action = Actions.STOP_VIBRATOR.name
        startService(intent2)

    }

    private fun setAlarm(calendar: Calendar, alarmActionPendingIntent: PendingIntent) {
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, getAlarmInfoPendingIntent())
        alarmManager.setAlarmClock(alarmClockInfo, alarmActionPendingIntent)
    }
    private fun getAlarmInfoPendingIntent(): PendingIntent {
        val alarmInfoIntent = Intent(this, MainActivity::class.java)
        alarmInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(this, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI)

    }
    override fun onStop() {
        super.onStop()
        mSensorManager.unregisterListener(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}