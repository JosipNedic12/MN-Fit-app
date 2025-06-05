package com.example.mnfit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.example.mnfit.R
import com.example.mnfit.ui.theme.gym_LightGray
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val gymLocation = LatLng(45.043114003241875, 18.662695664674573)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(gymLocation, 15f)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            stringResource(R.string.welcome_to_mnfit),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.mnfit_is_your_destination_for_fitness_wellness_and_community_join_us_for_group_classes_personal_training_and_more),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Contact Us
        Text(
            stringResource(R.string.contact_us),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = gym_LightGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.email_contact))
        Text(stringResource(R.string.phone_contact))
        Text(stringResource(R.string.instagram_contact))
        Spacer(modifier = Modifier.height(24.dp))

        // Location with Google Maps
        Text(
            stringResource(R.string.our_location),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = gym_LightGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = gymLocation),
                    title = "MNFit Gym",
                    snippet = "Your fitness destination"
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
