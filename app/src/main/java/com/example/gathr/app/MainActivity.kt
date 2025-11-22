package com.example.gathr.app
import android.os.Bundle import androidx.activity.ComponentActivity import androidx.activity.compose.setContent import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.gathr.navigation.AppRootNavigation import com.example.gathr.presentation.moderator.PendingsScreen import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.moderator.ModeratorNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MaterialTheme {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF7954AB), Color(0xFF312245))
                            )
                        )
                ) {
                    val navController = rememberNavController()
                    ModeratorNavGraph(navController)
                }
            }
        }
    }
}
