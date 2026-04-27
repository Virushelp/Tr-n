package com.artem.fitathlete

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.artem.fitathlete.data.db.AppDatabase
import com.artem.fitathlete.repository.FitnessRepository
import com.artem.fitathlete.ui.navigation.FitAthleteAppRoot
import com.artem.fitathlete.ui.theme.FitAthleteTheme
import com.artem.fitathlete.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(this)
        val repository = FitnessRepository(db.workoutDao(), db.mealDao())
        viewModel = MainViewModel(repository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            var isWatermelonTheme by rememberSaveable { mutableStateOf(false) }
            FitAthleteTheme(isWatermelonTheme = isWatermelonTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FitAthleteAppRoot(
                        viewModel = viewModel,
                        isWatermelonTheme = isWatermelonTheme,
                        onToggleTheme = { isWatermelonTheme = !isWatermelonTheme }
                    )
                }
            }
        }
    }
}
