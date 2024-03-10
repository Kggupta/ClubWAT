package com.example.clubwat.views
import android.app.KeyguardManager.KeyguardDismissCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.model.UserProfile
import com.example.clubwat.viewmodels.ForYouViewModel

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
