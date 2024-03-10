package com.example.clubwat.views
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.model.Notification
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.InboxViewModel
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InboxView(
    viewModel: InboxViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getNotifications()
    }

    val notifications by viewModel.notifications.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon =
                {
                    IconButton(
                        onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(notifications) {notification ->
                    Row {
                        val state = rememberDismissState(
                            confirmStateChange = {dismiss ->
                                if (dismiss == DismissValue.DismissedToStart) {
                                    viewModel.deleteNotification(notification.id)
                                }
                                true
                            }
                        )
                        SwipeToDismiss(state = state, background = {
                            Box(modifier = Modifier.fillMaxSize()
                                    .padding(8.dp)
                                    .background(when (state.dismissDirection){
                                    DismissDirection.EndToStart -> Color.LightGray
                                    else -> Color.Transparent
                                    })
                            ) {
                                Icon(modifier=Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(8.dp),
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close")
                            }
                        }) {
                            NotificationItem(notification = notification,
                                navController = navController)
                        }
                    }
                }
            }
        }
    )
}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification,
    navController: NavController
) {
    Card(
        onClick = {
            if (notification.clubId != null) {
              navController.navigate("club/${notification.clubId}")
            } else if (notification.eventId != null) {
              navController.navigate("event/${notification.eventId}")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            val apiDateTime = OffsetDateTime.parse(notification.createDate)
            val duration = Duration.between(apiDateTime, OffsetDateTime.now())

            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            Box  {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Box(modifier=Modifier.fillMaxWidth()) {
                        Text(text = "${notification.sourceUser.firstName} ${notification.sourceUser.lastName}".take(50),
                            fontWeight = FontWeight.Bold)
                        Text(text = when {
                            abs(days) > 0 -> "${abs(days)}d ago"
                            abs(hours) > 0 -> "${abs(hours)}h ago"
                            abs(minutes) >= 0 -> "${abs(minutes)}m ago"
                            else -> "just now"
                        }, fontWeight = FontWeight.Light,
                            modifier=Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    Text(text = notification.content)
                }
            }
        }
    }
}
