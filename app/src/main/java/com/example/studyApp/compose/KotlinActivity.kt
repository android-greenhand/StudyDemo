package com.example.studyApp.compose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_kotlin.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class KotlinActivity : AppCompatActivity() {
    companion object {
        const val TAG = "KotlinActivity_TAG"
    }

    var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        Log.d(TAG + "onCreate", Thread.currentThread().id.toString())
        job = lifecycleScope.launch(Dispatchers.Main) {
            Log.d(TAG + "launch", Thread.currentThread().id.toString())
            kotlin_text.text = async { getTextAsync() }.await()
            createFlow()
                .flowOn(Dispatchers.IO)
                .filter {
                    Log.d(TAG, "过滤")
                    Log.d(TAG + "filter", Thread.currentThread().id.toString())
                    it % 2 == 1
                }
                .map {
                    it * it
                }
                .onCompletion {
                    kotlin_text2.text = "数据接受完成"
                }
                .collect {
                    kotlin_text2.text = it.toString()
                }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    suspend fun getText(): String {
        return "Sus"
    }

    private suspend fun getTextAsync(): String {
        delay(2000)
        return "this is from 2s message !!!"
    }

    private suspend fun createFlow(): Flow<Int> {
        Log.d(TAG + "createFlow", Thread.currentThread().id.toString())
        return flow {
            for (i in 1..10) {
                Log.d(TAG + "emit", Thread.currentThread().id.toString())
                delay(1000)
                emit(i)

            }
        }
    }


}