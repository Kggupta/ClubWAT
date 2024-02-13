import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    // Function to handle sign up logic
    fun login() {
        viewModelScope.launch {
        }
    }
}