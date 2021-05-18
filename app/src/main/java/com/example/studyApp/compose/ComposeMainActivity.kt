package com.example.studyApp.compose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ComposeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagicTheme {
                Scaffold(
                    backgroundColor = Color.White,
                    content = {
                        listContent()
                    }
                )
            }
        }
    }


}

@Preview
@Composable
fun listContent() {
    Column(modifier = Modifier.background(Color.White)) {
        Text(text = "测试", style = TextStyle(color = Color.Blue))
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        Row {
            Text(text = "测试1", style = TextStyle(color = Color.Blue))
            Text(text = "测试2", style = TextStyle(color = Color.Blue))
            Text(text = "测试3", style = TextStyle(color = Color.Blue))
        }
    }
}

//Material Design风格

@Composable
fun MagicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = typography,
        shapes = shapes,
        content = content
    )
}
