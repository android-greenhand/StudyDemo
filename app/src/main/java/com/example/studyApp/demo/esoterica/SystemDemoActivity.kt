package com.example.studyApp.demo.esoterica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class SystemDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme() {
                Scaffold(backgroundColor = Color.White) {
                    ListView(intentDataList = initIntentDataList())
                }
            }
        }
    }

    @Preview(name = "Light Mode")
    @Composable
    fun preView() {
        ListView(initIntentDataList())
    }

    @Composable
    fun ListView(intentDataList: List<IntentData>) {

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            items(intentDataList) { intentData ->
                textView(s = intentData.buttonText, intent = intentData.intent)
            }
        }
    }


    @Composable
    fun textView(s: String, intent: Intent) {

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(100f)
        ) {
            Text(text = s, style = TextStyle(color = Color(30, 45, 37, 150), fontSize = 20.sp,textAlign = TextAlign.Center),
                modifier = Modifier
                    .clickable {
                        startActivity(intent)
                    }
                    .padding(bottom = 1.dp)
                    .height(50.dp)
                    .background(Color(0, 255, 119, 100))
                    .fillMaxWidth()

            )
            //Spacer(modifier = Modifier.padding(vertical = 5.dp))
        }

    }


    private fun initIntentDataList(): List<IntentData> {
        val arrayListOf = arrayListOf<IntentData>()


        arrayListOf.add(IntentData("系统的辅助功能界面", Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
        arrayListOf.add(IntentData("添加帐户界面", Intent(Settings.ACTION_ADD_ACCOUNT)))
        arrayListOf.add(IntentData("系统的包含飞行模式的界面", Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)))
        arrayListOf.add(IntentData("系统的更多连接或其它连接界面", Intent(Settings.ACTION_WIRELESS_SETTINGS)))
        arrayListOf.add(IntentData("系统的APN设置界面", Intent(Settings.ACTION_APN_SETTINGS)))
        //   arrayListOf.add(IntentData("根据包名跳转到该app的应用信息界面",Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)))
        arrayListOf.add(IntentData("系统的开发者选项界面", Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)))
        arrayListOf.add(IntentData("系统的应用管理界面(默认应用界面)", Intent(Settings.ACTION_APPLICATION_SETTINGS)))
        arrayListOf.add(IntentData("系统的应用管理界面(默认应用界面)", Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)))
        arrayListOf.add(IntentData("系统的应用管理界面(全部界面)", Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS)))
        arrayListOf.add(IntentData("系统的蓝牙管理界面", Intent(Settings.ACTION_BLUETOOTH_SETTINGS)))
        arrayListOf.add(IntentData("系统的SIM卡和网络管理界面", Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)))
        arrayListOf.add(IntentData("系统的语言和时间管理界面", Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)))
        arrayListOf.add(IntentData("系统的语言和时间管理界面", Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)))
        arrayListOf.add(IntentData("系统的关于手机界面：", Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)))
        arrayListOf.add(IntentData("系统的显示和亮度界面", Intent(Settings.ACTION_DISPLAY_SETTINGS)))
        arrayListOf.add(IntentData("系统的互动屏保界面(API>=18)", Intent(Settings.ACTION_DREAM_SETTINGS)))
        arrayListOf.add(IntentData("系统的存储和备份管理界面", Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)))
        arrayListOf.add(IntentData("系统的存储和备份管理界面", Intent(Settings.ACTION_MEMORY_CARD_SETTINGS)))
        arrayListOf.add(IntentData("系统的存储和备份管理界面", Intent(Settings.ACTION_PRIVACY_SETTINGS)))
        arrayListOf.add(IntentData("系统的语言选择界面", Intent(Settings.ACTION_LOCALE_SETTINGS)))
        arrayListOf.add(IntentData("系统的定位服务界面", Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
        arrayListOf.add(IntentData("系统的网络运营商界面", Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)))
        arrayListOf.add(IntentData("系统的NFC共享界面(需要手机支持NFC)", Intent(Settings.ACTION_NFCSHARING_SETTINGS)))
        arrayListOf.add(IntentData("系统的NFC设置界面(需要手机支持NFC且API>=16)", Intent(Settings.ACTION_NFC_SETTINGS)))
        arrayListOf.add(IntentData("系统的安全设置界面", Intent(Settings.ACTION_SECURITY_SETTINGS)))
        arrayListOf.add(IntentData("系统的设置界面", Intent(Settings.ACTION_SETTINGS)))
        arrayListOf.add(IntentData("系统的声音设置界面", Intent(Settings.ACTION_SOUND_SETTINGS)))
        arrayListOf.add(IntentData("系统的账号界面", Intent(Settings.ACTION_SYNC_SETTINGS)))
        arrayListOf.add(IntentData("系统的个人字典界面", Intent(Settings.ACTION_USER_DICTIONARY_SETTINGS)))
        arrayListOf.add(IntentData("系统的IP设置界面", Intent(Settings.ACTION_WIFI_IP_SETTINGS)))
        arrayListOf.add(IntentData("系统的WLAN界面", Intent(Settings.ACTION_WIFI_SETTINGS)))

        return arrayListOf
    }
}


data class IntentData(val buttonText: String, val intent: Intent)