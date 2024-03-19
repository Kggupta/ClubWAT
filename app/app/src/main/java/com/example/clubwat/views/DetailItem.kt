import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailItem(text: String?, icon: ImageVector, onClick: (() -> Unit)? = null, @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxWidth()) {
    Card(onClick = { if (onClick != null) onClick() }, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = icon,
                    contentDescription = "location"
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = text ?: "",
                    fontWeight = FontWeight.Bold
                )
                if (onClick != null && icon != Icons.Filled.Favorite && icon != Icons.Filled.FavoriteBorder) {
                    Column (Modifier.fillMaxWidth()){
                        Icon(modifier= Modifier.align(Alignment.End),
                            imageVector = Icons.Filled.ChevronRight, contentDescription = "Go")
                    }
                }
            }
        }
    }
}