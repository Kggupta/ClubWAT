package com.example.clubwat.views
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clubwat.model.UserProfile

@Composable
fun ShareDialog(
    friends: MutableList<UserProfile>,
    dismissCallback: () -> Unit,
    chooseFriendCallback: (userId: Int) -> Unit
) {
    AlertDialog(
        title = {Text("Select Friend", textAlign = TextAlign.Center)},
        text = {
            Column {
                for (user in friends) {
                    Card (onClick = {
                        chooseFriendCallback(user.id)
                    },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()) {
                        Column {
                            Text(text = "${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold)
                            Text(text = user.email, fontWeight = FontWeight.Light)
                        }
                    }
                }
                if (friends.size <= 0) {
                    Text("No friends yet!")
                }
            }
        },
        onDismissRequest = {
            dismissCallback()
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    dismissCallback()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
