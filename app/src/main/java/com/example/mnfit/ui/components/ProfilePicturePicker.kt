package com.example.mnfit.ui.components

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mnfit.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

enum class PendingAction { NONE, GALLERY, CAMERA }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfilePicturePicker(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    onImageSelected: (Uri?, Bitmap?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pendingAction by remember { mutableStateOf(PendingAction.NONE) }
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val storagePermission = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedBitmap = null
            onImageSelected(uri, null)
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedImageUri = null
            onImageSelected(null, bitmap)
        }
    }

    Box(
        modifier = modifier
            .size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        when {
            selectedBitmap != null -> {
                Image(
                    bitmap = selectedBitmap!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            selectedImageUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            !photoUrl.isNullOrEmpty() -> {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile_picture),
                    tint = Color.White,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
        }

        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 6.dp)
                .size(36.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_profile_picture),
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Dialog for choosing source
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.change_profile_picture)) },
            text = {
                Column {
                    TextButton(onClick = {
                        showDialog = false
                            galleryLauncher.launch("image/*")
                    }) { Text(stringResource(R.string.pick_from_gallery)) }

                    TextButton(onClick = {
                        showDialog = false
                        if (cameraPermission.status.isGranted) {
                            cameraLauncher.launch(null)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    }) { Text(stringResource(R.string.take_a_photo)) }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    LaunchedEffect(storagePermission.status.isGranted) {
        if (storagePermission.status.isGranted && pendingAction == PendingAction.GALLERY) {
            galleryLauncher.launch("image/*")
            pendingAction = PendingAction.NONE
        }
    }
    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted && pendingAction == PendingAction.CAMERA) {
            cameraLauncher.launch(null)
            pendingAction = PendingAction.NONE
        }
    }
}
