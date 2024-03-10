package com.example.clubwat.views
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.model.Event
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.EventDetailsViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsView(
    viewModel: EventDetailsViewModel,
    navController: NavController,
    eventId: String?
) {
    LaunchedEffect(Unit) {
        viewModel.getFriends()
        if (eventId != null) {
            viewModel.getEvent(eventId)
        }
    }

    val friends by viewModel.friends.collectAsState()
    val event by viewModel.event.collectAsState()
    var showDetailsView by remember { mutableStateOf(false) }
    var showCalendarEvent by remember { mutableStateOf(false) }
    var showShareView by remember { mutableStateOf(false) }
    var showCalendarConfirmation by remember { mutableStateOf(false) }
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
                        text = viewModel.getEventTitle(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (event != null) {
                        Button(onClick = {
                                if (event?.isAttending == false) {
                                    showCalendarConfirmation = true
                                }
                                viewModel.attendEvent()
                            }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!event!!.isAttending) LightOrange else Color.LightGray,
                            contentColor = if (!event!!.isAttending) Color.White else Color.Black)) {
                            Icon(if (event!!.isAttending) {
                                Icons.AutoMirrored.Filled.ExitToApp
                            } else {
                                Icons.Filled.Add
                            }, contentDescription = "Attend Event")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            viewModel.bookmarkEvent()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!event!!.isBookmarked) LightOrange else Color.LightGray,
                            contentColor = if (!event!!.isBookmarked) Color.White else Color.Black)) {
                            Icon(Icons.Filled.Bookmark, contentDescription = "Bookmark")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        if (event?.privateFlag == false) {
                            Button(onClick = {
                                showShareView = true
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = LightOrange
                            )) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Button(onClick = {
                            navController.navigate("club/${event!!.clubId}")
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = LightOrange
                        )) {
                            Icon(Icons.Filled.Group, contentDescription = "Club")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ClickableText(modifier = Modifier
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                    text = AnnotatedString(viewModel.getEventDescription().take(75) + "..."),
                    style= TextStyle(textAlign = TextAlign.Center),
                    onClick = {
                        showDetailsView = true
                    })
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DetailItem(text = viewModel.getLocation(), icon = Icons.Filled.LocationOn)
                    DetailItem(text = viewModel.getStartDate(), icon = Icons.Filled.AccessTime)
                    DetailItem(text = viewModel.getEndDate(), icon = Icons.Filled.AccessTime)
                }
            }
        }
    )

    if (showShareView) {
        ShareDialog(friends = friends, dismissCallback = { showShareView = false }, chooseFriendCallback = {
            showShareView = false
            viewModel.shareEvent(it)
        })
    }

    if (showCalendarEvent) {
        event?.let { it1 ->
            Calendar(event = it1)
            showCalendarEvent = false
        }
    }

    if (showCalendarConfirmation) {
        AlertDialog(title = {Text("Add To Phone Calendar?")},
            onDismissRequest = {showCalendarConfirmation = false},
            buttons = {
                Row {
                    TextButton(
                        onClick = {
                            showCalendarConfirmation = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            showCalendarEvent = true
                            showCalendarConfirmation = false
                        }
                    ) {
                        Text("Yes")
                    }
                }
        })
    }

    if (showDetailsView) {
        AlertDialog(
            title = {
                Text(text = viewModel.getEventTitle(), textAlign = TextAlign.Center)
            },
            text = {
                Column {
                    Text(text = viewModel.getEventDescription())
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            },
            onDismissRequest = {
                showDetailsView = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showDetailsView = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun DetailItem(text: String?, icon: ImageVector) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "location"
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = text ?: "",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(event: Event) {
    val intent = Intent(Intent.ACTION_EDIT);

    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra(CalendarContract.Events.TITLE, event.title)
    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
    intent.putExtra(
        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
        LocalDateTime.parse(
            event.startDate,
            DateTimeFormatter.ISO_DATE_TIME
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    intent.putExtra(
        CalendarContract.EXTRA_EVENT_END_TIME,
        LocalDateTime.parse(
            event.endDate,
            DateTimeFormatter.ISO_DATE_TIME
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    intent.putExtra(CalendarContract.Events.ALL_DAY, false)
    intent.putExtra(CalendarContract.Events.DESCRIPTION, event.description)
    ContextCompat.startActivity(LocalContext.current, intent, null)
}
