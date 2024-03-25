package com.example.clubwat.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DateTimePickerView(dateTime: MutableState<Calendar>) {
    val context = LocalContext.current
    Button(
        onClick = { showDateTimePicker(context, dateTime) },
        Modifier
            .padding(vertical = 8.dp)
            .height(57.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10),
        border = BorderStroke(0.5.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Text(
            formatDateTimeView(dateTime.value),
            style = TextStyle(fontSize = 17.sp, color = Color.Black)
        )
    }
}

fun formatDateTimeView(calendar: Calendar): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(calendar.time)
}

fun showDateTimePicker(context: Context, dateTime: MutableState<Calendar>) {
    val calendar = Calendar.getInstance()
    val currentYear = dateTime.value.get(Calendar.YEAR)
    val currentMonth = dateTime.value.get(Calendar.MONTH)
    val currentDay = dateTime.value.get(Calendar.DAY_OF_MONTH)
    val currentHour = dateTime.value.get(Calendar.HOUR_OF_DAY)
    val currentMinute = dateTime.value.get(Calendar.MINUTE)

    val dateTimePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    calendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay,
                        selectedHour,
                        selectedMinute
                    )
                    dateTime.value = calendar
                },
                currentHour,
                currentMinute,
                true
            )
            timePickerDialog.show()
        },
        currentYear,
        currentMonth,
        currentDay
    )
    dateTimePickerDialog.show()
}

@Preview
@Composable
fun DateTimePickerPreview() {
    val dateTime = remember { mutableStateOf(Calendar.getInstance()) }
    DateTimePickerView(dateTime = dateTime)
}