package com.example.studyApp.demo.tile

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.studyApp.LaunchActivity
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_tile.*

class TileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(CustomDemoTileService.TAG,Process.myPid().toString())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tile)




        btn.setOnClickListener {

        }
    }

    private val  connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    };

}


class CustomDemoTileService : TileService() {




    companion object {
        const val TAG = "CustomDemoTileServiceTAG"
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Log.d(TAG, "onTileAdded")
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.d(TAG, "onStartListening")


//        if (qsTile.state == Tile.STATE_ACTIVE) {
//            qsTile.label = "inactive"
//            qsTile.icon = Icon.createWithResource(this, R.drawable.img)
//            qsTile.state = Tile.STATE_INACTIVE
//        } else {
//            qsTile.label = "active"
//            qsTile.icon = Icon.createWithResource(this, R.drawable.play)
//            qsTile.state = Tile.STATE_ACTIVE
//        }
//        qsTile.updateTile()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)

    }

    override fun onStopListening() {
        super.onStopListening()
        Log.d(TAG, "onStopListening")
    }

    var mCustomDemoTileString = 1

    override fun onClick() {
        super.onClick()
        Log.d(TAG, "onClick")
        Log.d(TAG, this.toString())


        Log.d(TAG,Process.myPid().toString())
        mCustomDemoTileString = ++ mCustomDemoTileString
        Log.d(TAG,mCustomDemoTileString.toString())
        if (qsTile.state == Tile.STATE_ACTIVE) {
            qsTile.label = "demo应用"
            qsTile.icon = Icon.createWithResource(this, R.drawable.img)
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            qsTile.label = "open demo应用"
            qsTile.icon = Icon.createWithResource(this, R.drawable.play)
            qsTile.state = Tile.STATE_ACTIVE
        }
        qsTile.updateTile()


        startActivityAndCollapse(Intent(this,LaunchActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
//        Toast.makeText(this, "1111", 100).show()

//        val open = openFlagFromSp()
//        if (open) {
//            qsTile.state = Tile.STATE_ACTIVE // 更改成活跃状态
//            qsTile.icon = Icon.createWithResource(this, R.drawable.play)
//        } else {
//            qsTile.state = Tile.STATE_INACTIVE;
//            qsTile.icon = Icon.createWithResource(this, R.drawable.img)
//        }
//        qsTile.updateTile()


//        showDialog(Dialog(this).apply {
//            setTitle("gzp")
//            setContentView(R.layout.activity_tile)
//        })
    }


    private fun openFlagFromSp(): Boolean {
        var boolean: Boolean
        with(getSharedPreferences("Test", MODE_PRIVATE)) {
            boolean = this.getBoolean("openFlag", true)
            this.edit().let {
                it.putBoolean("openFlag", !boolean)
                it.commit()
            }
        }
        return boolean

    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        Log.d(TAG, "onTileRemoved")
    }
}