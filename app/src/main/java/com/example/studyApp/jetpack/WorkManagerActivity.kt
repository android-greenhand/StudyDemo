package com.example.studyApp.jetpack

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.studyApp.R
import kotlinx.android.synthetic.main.common_input_view_layout.*
import java.util.concurrent.TimeUnit

class WorkManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            textView()
        }
    }

    @Preview
    @Composable
    fun textView() {
        val result = remember {
            mutableStateOf("is waiting")
        }
        Column(Modifier.width(IntrinsicSize.Max)) {
            Text(
                text = "work manager button",
                modifier = Modifier
                    .clickable {
                        generateWork(result)
                    }
                    .padding(5.dp),

                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 1,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = result.value, modifier = Modifier
                .padding(5.dp),

                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                fontSize = 30.sp)


            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "vibrator  button",
                modifier = Modifier
                    .clickable {
                        vibrator(this@WorkManagerActivity)
                    }
                    .padding(5.dp),

                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 1,
                fontSize = 30.sp
            )

            Text(
                text = "work vibrator  button",
                modifier = Modifier
                    .clickable {
                        generateVibratorWork()
                    }
                    .padding(5.dp),

                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 1,
                fontSize = 30.sp
            )


            Text(
                text = "repeat work vibrator  button",
                modifier = Modifier
                    .clickable {
                        generateRepeatVibratorWork()
                    }
                    .padding(5.dp)
                    .background(MaterialTheme.colors.surface),

                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 1,
                fontSize = 30.sp,
                )

        }

    }


    private fun generateWork(result: MutableState<String>) {
        val inputData = Data.Builder().putString(CUSTOM_WORK, "this is from WorkManagerActivity data ").build()
        val work = OneTimeWorkRequest
            .Builder(CustomWork::class.java)
            .setInputData(inputData)
            .build()


        WorkManager
            .getInstance(this)
            .enqueue(work)

        WorkManager
            .getInstance(this)
            .getWorkInfoByIdLiveData(work.id)
            .observe(this, Observer {
                result.value = it.outputData.getString(CUSTOM_WORK) ?: "null"
            })
    }


    private fun generateVibratorWork() {
        val vibratorWork = OneTimeWorkRequest.Builder(VibratorWork::class.java).build()
        WorkManager.getInstance(this).enqueue(vibratorWork)
    }


    private fun generateRepeatVibratorWork() {
        val vibratorWork = PeriodicWorkRequest.Builder(VibratorWork::class.java, 15, TimeUnit.SECONDS).build()
        WorkManager.getInstance(this).enqueue(vibratorWork)
    }

}

const val CUSTOM_WORK = "CustomWork_gzp"

class CustomWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(CUSTOM_WORK, "this is work is running \t accept data is :" + inputData.getString(CUSTOM_WORK))
        Log.d(CUSTOM_WORK, Thread.currentThread().name)

        val data = Data.Builder().putString(CUSTOM_WORK, "this is from custom work data").build()

        return Result.success(data)
    }

}

fun vibrator(context: Context) {

    val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val patter = longArrayOf(1000, 1000, 1000, 1000)

    vibrator.vibrate(VibrationEffect.createWaveform(patter, -1))
}

const val VIBRATOR_WORK = "VIBRATOR_WORK_GZP"

class VibratorWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(VIBRATOR_WORK, "vibrator work is running")

        vibrator(applicationContext)

        return Result.success()
    }
}

