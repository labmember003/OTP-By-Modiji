package com.falcon.otpbymodiji

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.otpbymodiji.ui.theme.OTPByModijiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(
            parent = null
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                val mainViewModel: MainViewModel = hiltViewModel()
                val showWalkthrough = mainViewModel.showWalkthrough.collectAsState().value
                if (showWalkthrough) {
                    WalkthroughScreen(onGetStarted = {
                        mainViewModel.setShowWalkthrough(false)
                    })
                } else {
                    MainScreen(mainViewModel, NetworkUtils())
                }
            }
        }
    }
}

@Composable
fun LottieAnimation(animationID: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animationID))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        clipSpec = LottieClipSpec.Frame(
            min = null,
            max = 35,
            maxInclusive = true
        ),
        iterations = 3,
        modifier = Modifier
            .size(400.dp)
    )
}