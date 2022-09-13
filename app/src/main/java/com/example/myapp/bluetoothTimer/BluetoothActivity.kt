package com.example.myapp.bluetoothTimer

import com.example.myapp.bluetoothTimer.BlueTimerReceiver
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.MainActivity
import com.example.myapp.NOTIFICATION_CHANNEL_ID
import com.example.myapp.R
import com.example.myapp.log
import java.text.SimpleDateFormat
import java.util.*

class BluetoothActivity : AppCompatActivity() {
    private var timeToAlarm: Long = 0
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var pendingIntent: PendingIntent // Для поиска будильника
    private lateinit var button: Button
    private lateinit var connectionThread: ConnectThread
    // Handler для передачи сообщений между потоками
    private lateinit var handler: Handler
    // Инициализация втроенного Bluetooth устройства
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    // Для будильника
    private lateinit var alarmManager: AlarmManager
    // Искомый, заранее известные, адрес устройства
    val bluetoothAddress = "98:D3:31:F9:8D:32"
    //Переменная для хранения найденного устройства
    private lateinit var myDevice: BluetoothDevice
    var founded = false
    // Кнопка соединения
    private lateinit var buttonConnect: Button
    // Кнопка поезда
    private lateinit var buttonSearch: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue)

        bluetoothAdapter?.startDiscovery()


        notificationManagerCompat = NotificationManagerCompat.from(this)


        button = findViewById(R.id.buttonTestTest)
        button.setOnClickListener {
            var a = alarmManager.nextAlarmClock.triggerTime
            val b = SimpleDateFormat("HH:mm:ss")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = a
            log(b.format(calendar.time))
            alarmManager.cancel(pendingIntent)
        }

        init()

        //Запрос разрешений
        requestPermissions()

        //Регистрация ресивера
        registerMyReceiver()
    }

    private fun init() {
        initButtons()
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        initHandler()
    }

    private fun initHandler() {
        //Handler
        handler = object: Handler(Looper.myLooper()!!){
            override fun handleMessage(msg: Message) {
                //textView.text = "Текущее время на ардуино: ${msg.obj}"
                if (msg.obj.toString().equals("alarm")) {
                    Log.d("TAG", "Сработал будильник")
                    setTestAlarm()
                }
            }
        }
    }

    private fun initButtons() {
        //Кнопка запуска соединения
        buttonConnect = findViewById(R.id.buttonConnect)
        buttonConnect.isEnabled = false // Блокировка кпопки
        buttonConnect.setOnClickListener {
            //Создать поток соединения и запускить
            if (myDevice != null) {
                connectionThread = ConnectThread(myDevice, handler)
                connectionThread.start()
                Toast.makeText(this, "Попытка соединения", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device = null", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch = findViewById(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            bluetoothAdapter?.startDiscovery()
        }
    }

    private fun registerMyReceiver() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    //Инициализация широковещательного приемника
    private val receiver = object  : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address
                    println("Name: $deviceName. Address: $deviceHardwareAddress")
                    if (device?.address.equals(bluetoothAddress) && !founded) {
                        if (device != null) {
                            myDevice = device
                            founded = true
                            buttonConnect.isEnabled = true //Если найдено искомое устройство - разблокировать устройство
                            Toast.makeText(context, "Устройство найдено", Toast.LENGTH_SHORT).show()
                            //Создать поток соединения и запускить
                            if (myDevice != null) {
                                connectionThread = ConnectThread(myDevice, handler)
                                connectionThread.start()
                                Toast.makeText(context, "Попытка соединения", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Device = null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun requestPermissions() {
        // Запрос разрешения на использование геолокации
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN),
            1 )
    }

    override fun onDestroy() {
        super.onDestroy()
        //Отказ от регистрации приемника при закрытии приложения
        unregisterReceiver(receiver)
    }

    private fun getAlarmInfoPendingIntent(): PendingIntent {
        val alarmInfoIntent = Intent(this, MainActivity::class.java)
        alarmInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(this, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun setAlarm(calendar: Calendar, alarmActionPendingIntent: PendingIntent) {
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, getAlarmInfoPendingIntent())
        alarmManager.setAlarmClock(alarmClockInfo, alarmActionPendingIntent)
        //Toast.makeText(this, "Будильник установлен на ${simpleDateFormat.format(calendar.time)}", Toast.LENGTH_SHORT).show()
    }
    private fun setTestAlarm() {
        val calendar = Calendar.getInstance().timeInMillis + 180000
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = calendar

        timeToAlarm = calendar2.timeInMillis

        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, BlueTimerReceiver::class.java),
            0
        )

        setAlarm(calendar2, pendingIntent)
        var notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Будильник")
            .setContentText("Установлен")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(notificationManagerCompat) {
            notify(1, notificationBuilder.build())
        }
        //finish()
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
        val a = (timeToAlarm - Calendar.getInstance().timeInMillis) / 1000
        button.text = "Время: ${a.toString()}"
    }
}