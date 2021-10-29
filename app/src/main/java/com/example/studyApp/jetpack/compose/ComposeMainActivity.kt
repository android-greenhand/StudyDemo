package com.example.studyApp.jetpack.compose

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyApp.R
import kotlin.reflect.KProperty

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


@Composable
fun messageCard(msg: Message) {
    Row(modifier = Modifier
        .background(Color(100))
        .padding(all = 10.dp)) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "this is sample image",
            modifier = Modifier
                .size(40.dp)
                .padding(1.dp)
            // .border(1.5.dp, MaterialTheme.colors.secondaryVariant, RectangleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))


        val isExpanded = remember {
            mutableStateOf<Boolean>(false)
        }

        val surfaceColor: Color by animateColorAsState(
            targetValue = if (isExpanded.value) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        )


        Column(modifier = Modifier.clickable { isExpanded.value = !isExpanded.value }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2)

            Spacer(modifier = Modifier.height(5.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp),
                elevation = 1.dp,
                color = surfaceColor,
            ) {
                Text(text = msg.body,
                    maxLines = if (isExpanded.value) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.body2)
            }

        }
    }
}


@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode")
@Composable
fun prevMessageCard() {
    messageCard(Message("compose", "this is a good dev util"))
}


@Composable
fun conversation(messages: List<Message>) {
    LazyColumn() {
        items(messages) { message -> messageCard(msg = message) }
    }
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode")
@Composable
fun preConversation() {
    val tempSource = listOf(
        Message("compose", "this is a good dev util"),
        Message("compose", "this is a good dev util"),
        Message("compose", "this is a good dev util"),
        Message("在展开消息时显示动画效果", """我们的对话变得更加有趣了。是时候添加动画效果了！
          我们将添加展开消息以显示更多内容的功能，同时为内容大小和背景颜色添加动画效果。
          为了存储此本地界面状态，我们需要跟踪消息是否已扩展。为了跟踪这种状态变化，我们必须使用 remember 和 mutableStateOf 函数。
          可组合函数可以使用 remember 将本地状态存储在内存中，并跟踪传递给 mutableStateOf 的值的变化。
          该值更新时，系统会自动重新绘制使用此状态的可组合项（及其子项）。
          我们将这一功能称为重组。通过使用 Compose 的状态 API（如 remember 和 mutableStateOf），系统会在状态发生任何变化时自动更新界面：
          注意：您需要添加以下导入内容才能正确使用“by”。按 Alt+Enter 键即可添加这些内容。""".trim()),
    )

    val dataSource = ArrayList<Message>()
        .apply {
            for (i in 0..10) {
                addAll(tempSource)
            }
        }
    conversation(dataSource)
}


data class Message(val author: String, val body: String)