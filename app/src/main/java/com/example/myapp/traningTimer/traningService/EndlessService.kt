package com.example.myapp.traningTimer.traningService

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.NOTIFICATION_CHANNEL_ID
import com.example.myapp.R
import com.example.myapp.traningTimer.BROADCAST_ACTION
import com.example.myapp.traningTimer.SET_ALARM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "rahirim"



class EndlessService : Service(), SensorEventListener {

    var timeRightMove: Long = 0

    private var counter: Long = 0
    var vibrateDelay = 1000

    private var calendar: Long = 0

    private var flagUpdateNotification = false

    private lateinit var ringtone: Ringtone
    private lateinit var notificationUri: Uri

    private var flag = true

    private var xyAngle = 0f
    private var xzAngle = 0f
    private var zyAngle = 0f

    private lateinit var vibrator: Vibrator
    private var canVibrate: Boolean = false

    private var rightMove: Boolean = false
    private var leftMove: Boolean = false

    private lateinit var mSensorManager: SensorManager
    private lateinit var mOrientation: Sensor

    // Счетчик времени работы сервиса без запуска приложения
    private var counterTimeWork: Double = 0.0

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onCreate() {
        super.onCreate()
        counter = Calendar.getInstance().timeInMillis
        notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationUri = Uri.parse("content://media/internal/audio/media/119?title=Beep-Beep&canonical=1")
        ringtone = RingtoneManager.getRingtone(this, notificationUri)
        builder = createNotification()
        val notification = createNotification().build()
        startForeground(1, notification)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI)
        @Suppress("DEPRECATION")
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        canVibrate = vibrator.hasVibrator()
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (flag) {
            if (event != null) {
                xyAngle = event.values[0]  //Плоскость XY
                xzAngle = event.values[1] //Плоскость XZ
                zyAngle = event.values[2] //Плоскость ZY
                //vibrate(30)
            }

            if (xzAngle > 0 && xzAngle < 10) {
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }

            if (zyAngle < -40 && !rightMove) {
                Log.d(TAG, "rightMove")
                rightMove = true
                //vibrate(100)
                vibrateDelay = 500
                updateNotification("rightMove")
                timeRightMove = Calendar.getInstance().timeInMillis
            }

            if (zyAngle > 40 && rightMove) {
                Log.d(TAG, "leftMove")
                leftMove = true
                updateNotification("leftMove")
            }

            if (Calendar.getInstance().timeInMillis - counter > vibrateDelay) {
                counter = Calendar.getInstance().timeInMillis
                //Toast.makeText(this, "Да", Toast.LENGTH_SHORT).show()
                vibrate(400)
            }

            // Если сработал признак правого движения и прошло 2 секунды,
            // то признак правого движения сбрасывается.
            if (rightMove && Calendar.getInstance().timeInMillis - timeRightMove > 2000) {
                rightMove = false
                vibrateDelay = 1000
            }

            if (rightMove && leftMove) {
                flagUpdateNotification = true
                vibrate(200)
                rightMove = false
                leftMove = false
                val intent = Intent(BROADCAST_ACTION)
                intent.putExtra(SET_ALARM, 100)
                sendBroadcast(intent)
                if (ringtone.isPlaying) ringtone.stop()
                calendar = Calendar.getInstance().timeInMillis + 180000
                GlobalScope.launch(Dispatchers.IO) {
                    while (flagUpdateNotification) {
                        launch(Dispatchers.IO) {
                            val a = (calendar - Calendar.getInstance().timeInMillis) / 1000
                            updateNotification("$a")
                        }
                        delay(10)
                    }
                    log("End of the loop for the service")
                }
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private fun vibrate(milliseconds: Long) {
        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                Actions.STOP_VIBRATOR.name -> flag = false
                Actions.PLAY.name -> {
                    Toast.makeText(this, "play", Toast.LENGTH_SHORT).show()
                    flag = true
                    vibrateDelay = 1000
                    ringtone.play()
                    flagUpdateNotification = false
                }
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }
    private fun startService() {
        counterTimeWork = 0.0
        if (isServiceStarted) return
        log("Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
//        var a = GlobalScope.launch(Dispatchers.IO) {
//            while (isServiceStarted) {
//                launch(Dispatchers.IO) {
//                    //updateNotification()
//                    counterTimeWork += 5
//                }
//                delay(10000)
//            }
//            log("End of the loop for the service")
//        }
    }
    private fun updateNotification(string: String){
        builder.setContentText(string)
        notificationManagerCompat.notify(1, builder.build())
    }
    private fun stopService() {
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        if(ringtone.isPlaying) ringtone.stop()
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }
    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        }
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT)
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
    }
    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
        //builder.setContentText("Service was destroyed")
        //notificationManagerCompat.notify(1, builder.build())
        mSensorManager.unregisterListener(this)
    }
    private fun createNotification(): NotificationCompat.Builder {
        val builder =NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        return builder
            .setContentTitle("Endless Service")
            .setContentText("This is your favorite endless service working")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setSilent(true)
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility

    }
    override fun onBind(intent: Intent): IBinder? {
        log("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }
}